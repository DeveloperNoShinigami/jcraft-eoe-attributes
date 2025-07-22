package net.arna.jcraft.api.attack;

import it.unimi.dsi.fastutil.Pair;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.api.MoveSelectionResult;
import net.arna.jcraft.api.MoveUsage;
import net.arna.jcraft.api.attack.enums.MobilityType;
import net.arna.jcraft.api.attack.enums.MoveClass;
import net.arna.jcraft.api.attack.enums.MoveInputType;
import net.arna.jcraft.api.attack.moves.AbstractMove;
import net.arna.jcraft.api.attack.moves.AbstractSimpleAttack;
import net.arna.jcraft.api.component.living.CommonCooldownsComponent;
import net.arna.jcraft.api.stand.StandEntity;
import net.arna.jcraft.common.ai.AttackerBrainInfo;
import net.arna.jcraft.common.ai.CombatInstantContext;
import net.arna.jcraft.common.attack.moves.shared.MainBarrageAttack;
import net.arna.jcraft.common.config.JServerConfig;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * Anything that can use moves must implement this interface.
 * It provides basic functionality that moves need.
 *
 * @param <A> The type of the class implementing this interface
 * @param <S> The type of the animation state enum
 */
public interface IAttacker<A extends IAttacker<? extends A, S>, S extends Enum<?>> {
    boolean hasUser();

    LivingEntity getUser();

    LivingEntity getUserOrThrow();

    int getMoveStun();

    void setMoveStun(final int moveStun);

    // This cannot be called getWorld because it doesn't get remapped since it's in an interface.
    // StandEntity implements
    default Level getEntityWorld() {
        return getBaseEntity().level();
    }

    LivingEntity getBaseEntity();

    DamageSource getDamageSource();

    MoveMap<A, S> getMoveMap();

    boolean initMove(final MoveClass type);

    boolean canHoldMove(final @Nullable MoveInputType type);

    default void onUserMoveInput(final AbstractMove<?, ? super A> currentMove, final MoveInputType type, final boolean pressed, final boolean moveInitiated) {
        if ((moveInitiated && pressed && canHoldMove(type))) {
            setHoldingType(type);
        }
        if (getHoldingType() == type) {
            setHolding(pressed);
        }
        if (currentMove != null) {
            currentMove.onUserMoveInput(getThis(), type, pressed, moveInitiated);
        }
    }

    boolean isHolding();

    void setHolding(final boolean holding);

    MoveInputType getHoldingType();

    void setHoldingType(final MoveInputType holdingType);

    boolean canAttack();

    void cancelMove();

    boolean isRemote();

    AbstractMove<?, ? super A> getCurrentMove();

    void setCurrentMove(final AbstractMove<?, ? super A> move);

    default void setMove(final AbstractMove<?, ? super A> move, S state) {
        setCurrentMove(move);
        setState(state);
    }

    S getState();

    void setState(final S state);

    default void playAttackerSound(final SoundEvent sound, final float volume, final float pitch) {
        getBaseEntity().playSound(sound, volume, pitch);
    }

    /**
     * Called when a move is performed.
     * @param move The move that was performed. Should probably not be used.
     * @param targets The targets that were hit by the move. May be empty.
     */
    default void onPerform(AbstractMove<?, ? super A> move, Set<LivingEntity> targets) {}

    void setPerformedThisTick(final boolean b);
    boolean performedThisTick();

    A getThis();

    MoveUsage getMoveUsage();

    void plan(int aiLevel, AttackerBrainInfo info, CombatInstantContext combatCtx);

    // MOVE SELECTION LOGIC; STAYING HERE UNTIL I REWORK THE _ENTIRE_ ATTACKER USER AI SYSTEM

    default @Nullable MoveMap.Entry<A, S> getFirstValidEntry(final MoveClass moveClass) {
        boolean hasUser = hasUser();
        boolean crouching = hasUser && getUserOrThrow().isShiftKeyDown();
        boolean aerial = hasUser && !getUserOrThrow().onGround();
        return getMoveMap().getFirstValidEntry(moveClass, getThis(), crouching, aerial);
    }

    /**
     * Gets the fallback move to default to when selecting an attack to perform for attacker user AI.
     * If this always returns {@code null}, the AI will not be able to perform any attacks.
     * @return The first valid light attack, or the first valid heavy attack if no light attacks are available.
     */
    default AbstractMove<?, ? super A> getFallbackMove() {
        MoveMap.Entry<A, S> lightEntry = getFirstValidEntry(MoveClass.LIGHT);
        if (lightEntry != null) return lightEntry.getMove();

        MoveMap.Entry<A, S> heavyEntry = getFirstValidEntry(MoveClass.HEAVY);
        if (heavyEntry == null) {
            JCraft.LOGGER.warn("Couldn't find light or heavy attack entry while running selectAttack on attacker: {}", this);
            return null;
        } else {
            return heavyEntry.getMove();
        }
    }

    /**
     * Used to help AIs that use attackers with unique moves
     */
    default MoveSelectionResult specificMoveSelectionCriterion(AbstractMove<?, ? super A> attack, LivingEntity mob, LivingEntity target, int stunTicks,
                                                              int enemyMoveStun, double distance, StandEntity<?, ?> enemyStand, AbstractMove<?, ?> enemyAttack) {
        return attack.specificMoveSelectionCriterion(getThis(), mob, target, stunTicks, enemyMoveStun, distance, enemyStand, enemyAttack);
    }

     /**
      * Selects an attack to perform for attacker user AI.
      * @param cooldowns The cooldowns component of the mob.
      * @param mob The mob entity using the attacker.
      * @param target The target entity.
      * @param stunTicks The stun ticks of the mob.
      * @param enemyMoveStun The move stun of the enemy attack, if any.
      * @param distance The distance to the target.
      * @param enemyStand The enemy stand, if any.
      * @param enemyAttack The current attack of the enemy, if any.
      * @return A Pair containing the selected move and whether it requires crouching, or null if no suitable attack was found.
      */
     default @Nullable Pair<AbstractMove<?, ? super A>, Boolean> selectAttack(
             CommonCooldownsComponent cooldowns, LivingEntity mob,
             LivingEntity target, int stunTicks, int enemyMoveStun,
             double distance, StandEntity<?, ?> enemyStand, AbstractMove<?, ?> enemyAttack) {

        AbstractMove<?, ? super A> selectedAttack;
        boolean needsCrouch = false;
        boolean doFinalChecks = true; // Refuses to run the move if certain conditions are met
        boolean enemyIsAttacking = enemyAttack != null;

        // If the opponent is countering, don't attack
        if (enemyIsAttacking && enemyAttack.isCounter()) {
            return null;
        }
        int movesOnCooldown = 0;

        selectedAttack = getFallbackMove(); // Fallback to light or heavy attack
        if (selectedAttack == null) {
            return null;
        }

        int selectedAttackInitTime = selectedAttack.getDuration() - selectedAttack.getWindup();

        for (AbstractMove<?, ? super A> attack : getMoveMap().asMovesList()) {
            needsCrouch = attack.isCrouchingVariant();
            int windupPoint = attack.getWindupPoint();

            if (attack.isFollowup()) {
                // Discount any followup attacks when there is no move to follow up from
                if (getCurrentMove() == null || getCurrentMove().getFollowup() != null) continue;
            } else if (cooldowns.getCooldown(attack.getMoveClass().getDefaultCooldownType()) > 0) {
                // Discount any on-cooldown non-followup attacks
                movesOnCooldown++;
                continue;
            }

            // Selection of characteristic moves with custom usage logic
            MoveSelectionResult result = specificMoveSelectionCriterion(attack, mob, target, stunTicks, enemyMoveStun, distance, enemyStand, enemyAttack);
            if (result == MoveSelectionResult.USE) {
                selectedAttack = attack;
                break;
            }
            if (result == MoveSelectionResult.STOP) {
                continue;
            }

            // Use mobility if opponent is far away
            if (attack.getMobilityType() != null) {
                // ...and isn't being comboed or is blocking
                if (stunTicks > 0) {
                    continue;
                }

                if (attack.getMobilityType() != MobilityType.HIGHJUMP && distance > 6) {
                    if (target.onGround()) {
                        if (attack.getMobilityType() == MobilityType.TELEPORT) {
                            // Intentionally looks at target's feet as to hit the ground exactly at it
                            mob.lookAt(EntityAnchorArgument.Anchor.EYES, target.position());
                        } else if (attack.getMobilityType() == MobilityType.DASH) {
                            // Look at target itself as a dash works best at that angle
                            mob.lookAt(EntityAnchorArgument.Anchor.EYES, target.getEyePosition().add(0, 0.5, 0));
                        }
                    }

                    if (attack.getMobilityType() == MobilityType.FLIGHT) {
                        mob.lookAt(EntityAnchorArgument.Anchor.EYES, target.getEyePosition());
                    }

                    selectedAttack = attack;
                    break;
                } // If target is considerably above the mob, or the mob is going to get hit
                else if (target.getY() > mob.getY() + 2 || (enemyAttack != null && enemyStand != null && enemyAttack.hasWindupPassed(enemyStand))) {
                    selectedAttack = attack;
                    break;
                }
            }

            // Use counter if opponent is using a non-ranged move
            if (enemyIsAttacking && enemyAttack != null && !enemyAttack.isRanged() && attack.isCounter()) {
                if (enemyStand != null && !enemyStand.blocking && enemyMoveStun > 0) {
                    selectedAttack = attack;
                    break;
                }
                continue;
            }

            boolean isBarrage = attack.isBarrage();
            boolean isCharge = attack.isCharge();
            if (distance <= 5) {
                //todo: expand on mob.canSee(target), because placing fences down doesn't cause them to want to break through
                if (isBarrage && !isCharge && !mob.hasLineOfSight(target)) // Mine towards target if possible
                {
                    if (attack instanceof MainBarrageAttack<?>) {
                        selectedAttack = attack;
                        needsCrouch = true;
                        doFinalChecks = false; // Disregards range limitation
                        break;
                    }
                }

                /*
                Use a barrage (or variant thereof) if the opponent is stunned, not blocking, and it's off cooldown,
                because it's a free combo extender and has a lower windup than light
                 */
                if (distance <= 2) {
                    if (isBarrage || (attack.isMultiHit() && attack.hasWindupPassed(this))) {
                        // Combo extend
                        if (enemyStand == null || !enemyStand.blocking) {
                            selectedAttack = attack;
                            break;
                        }
                        continue;
                    }
                }
            }

            // If the opponent is out of exactly twice the range it would take him to get to the user within the move being complete, use a projectile
            if (attack.isRanged() && distance > attack.getDuration() * target.getAttributeValue(Attributes.MOVEMENT_SPEED) * 2) {
                mob.lookAt(EntityAnchorArgument.Anchor.EYES, target.getEyePosition());
                selectedAttack = attack;
                break;
            }

            // If the opponent isn't using a move, prioritize attack with higher or equal initiation time
            if (windupPoint <= stunTicks && windupPoint >= selectedAttackInitTime) {
                selectedAttackInitTime = windupPoint;
                selectedAttack = attack;
            }
        }

        if (movesOnCooldown > 5 && JServerConfig.SURVIVAL_CDC.getValue() && !(mob instanceof StandEntity<?, ?>)) {
            cooldowns.cooldownCancel(); // >5 = 80+%
        }

        if (doFinalChecks) {
            if (selectedAttack.isCounter()) {
                if (stunTicks > 0) {
                    selectedAttack = null; // You can't combo into a counter
                }
            } else {
                if ( // Non-ranged offensive attacks aren't chosen if the opponent is too far
                        selectedAttack.getMobilityType() == null &&
                                selectedAttack instanceof AbstractSimpleAttack<?, ?> boxAttack &&
                                boxAttack.getHitboxSize() > 0 &&
                                !selectedAttack.isRanged() &&
                                distance > selectedAttack.getMoveDistance() + boxAttack.getHitboxSize()) {
                    selectedAttack = null;
                }
            }
        }

        return Pair.of(selectedAttack, needsCrouch);
    }
}
