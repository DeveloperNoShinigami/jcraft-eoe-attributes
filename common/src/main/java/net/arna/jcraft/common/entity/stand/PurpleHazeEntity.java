package net.arna.jcraft.common.entity.stand;

import it.unimi.dsi.fastutil.ints.IntSet;
import lombok.NonNull;
import mod.azure.azurelib.core.animation.AnimationState;
import mod.azure.azurelib.core.animation.RawAnimation;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.api.attack.MoveSetManager;
import net.arna.jcraft.api.stand.*;
import net.arna.jcraft.api.attack.enums.MoveClass;
import net.arna.jcraft.api.attack.enums.MoveInputType;
import net.arna.jcraft.api.attack.MoveMap;
import net.arna.jcraft.api.attack.enums.StunType;
import net.arna.jcraft.api.attack.MoveSet;
import net.arna.jcraft.api.attack.StateContainer;
import net.arna.jcraft.api.attack.moves.AbstractMove;
import net.arna.jcraft.common.attack.moves.purplehaze.BackhandAttack;
import net.arna.jcraft.common.attack.moves.purplehaze.PHGroundSlamAttack;
import net.arna.jcraft.common.attack.moves.purplehaze.PHRekkaAttack;
import net.arna.jcraft.common.attack.moves.purplehaze.PlayMove;
import net.arna.jcraft.common.attack.moves.shared.*;
import net.arna.jcraft.api.component.player.CommonPhComponent;
import net.arna.jcraft.common.util.JParticleType;
import net.arna.jcraft.common.util.JUtils;
import net.arna.jcraft.common.util.StandAnimationState;
import net.arna.jcraft.platform.JComponentPlatformUtils;
import net.arna.jcraft.api.registry.JSoundRegistry;
import net.arna.jcraft.api.registry.JStandTypeRegistry;
import net.arna.jcraft.api.registry.JStatusRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * The {@link StandEntity} for <a href="https://jojowiki.com/Purple_Haze">Purple Haze</a>.
 * @see JStandTypeRegistry#PURPLE_HAZE
 * @see net.arna.jcraft.client.model.entity.stand.PurpleHazeModel PurpleHazeModel
 * @see net.arna.jcraft.client.renderer.entity.stands.PurpleHazeRenderer PurpleHazeRenderer
 */
public final class PurpleHazeEntity extends AbstractPurpleHazeEntity<PurpleHazeEntity, PurpleHazeEntity.State> {
    public static final MoveSet<PurpleHazeEntity, State> MOVE_SET = MoveSetManager.create(JStandTypeRegistry.PURPLE_HAZE,
            PurpleHazeEntity::registerMoves, State.class);
    public static final StandData DATA = StandData.builder()
            .idleRotation(225f)
            .info(StandInfo.builder()
                    .name(Component.translatable("entity.jcraft.purple_haze"))
                    .proCount(3)
                    .conCount(3)
                    .freeSpace(Component.literal("""
                PASSIVE: Rage
                Builds up while the stand is summoned.
                Maxes out after 1 minute. When maxed, aura turns red.
                Rage decreases by half with each living thing Purple Haze kills.
                Purple Haze has a chance to target it's own user which increases with rage.
                
                EVOLUTION: Give Purple Haze any flower after it has killed a stand user.
                Doing this 5 times will evolve it into Purple Haze: Distortion."""))
                    .skinName(Component.literal("Toxin"))
                    .skinName(Component.literal("Stopping Force"))
                    .skinName(Component.literal("Reversal"))
                    .build())
            .summonData(SummonData.of(JSoundRegistry.PH_SUMMON))
            .build();

    private static final @NonNull KnockdownAttack<AbstractPurpleHazeEntity<?, ?>> CROUCHING_LIGHT_FOLLOWUP_ATTACK = BACKHAND_FOLLOWUP.copy().withAnim(State.BACKHAND_FOLLOWUP).allowHitUser();
    private static final @NonNull BackhandAttack CROUCHING_LIGHT_ATTACK = BACKHAND.copy().withFollowup(CROUCHING_LIGHT_FOLLOWUP_ATTACK).allowHitUser();
    private static final @NonNull SimpleAttack<AbstractPurpleHazeEntity<?, ?>> LIGHT_FOLLOWUP_ATTACK = LIGHT_FOLLOWUP.copy().withAnim(State.LIGHT_FOLLOWUP).allowHitUser();
    private static final @NonNull SimpleAttack<AbstractPurpleHazeEntity<?, ?>> LIGHT_ATTACK = LIGHT.copy().withFollowup(LIGHT_FOLLOWUP_ATTACK).withCrouchingVariant(CROUCHING_LIGHT_ATTACK).allowHitUser();
    private static final @NonNull MainBarrageAttack<AbstractPurpleHazeEntity<?, ?>> BARRAGE_ATTACK = AbstractPurpleHazeEntity.BARRAGE.copy().allowHitUser();
    private static final @NonNull SimpleAttack<AbstractPurpleHazeEntity<?, ?>> HEAVY_ATTACK = HEAVY.copy().allowHitUser();
    private static final @NonNull KnockdownAttack<AbstractPurpleHazeEntity<?, ?>> REKKA_3 = REKKA3.copy().withAnim(State.REKKA3).allowHitUser();
    private static final @NonNull SimpleAttack<AbstractPurpleHazeEntity<?, ?>> REKKA_2 = REKKA2.copy().withAnim(State.REKKA2).withFollowup(REKKA_3).allowHitUser();
    private static final @NonNull PHRekkaAttack REKKA_1 = REKKA1.copy().withAnim(State.REKKA1).withFollowup(REKKA_2).allowHitUser();
    private static final @NonNull PHGroundSlamAttack GROUND_SLAM = AbstractPurpleHazeEntity.GROUND_SLAM.copy().allowHitUser();

    public static final SimpleAttack<AbstractPurpleHazeEntity<?, ?>> GRAB_HIT_FINAL = new SimpleAttack<AbstractPurpleHazeEntity<?, ?>>(0, 27,
            34, 0.75f, 4f, 8, 2f, 1.25f, 0f)
            .withImpactSound(JSoundRegistry.IMPACT_1)
            .withHitSpark(JParticleType.HIT_SPARK_2)
            .withLaunch()
            .allowHitUser()
            .withInfo(
                    Component.literal("Grab (Final Hit)"),
                    Component.empty()
            );
    public static final SimpleMultiHitAttack<AbstractPurpleHazeEntity<?, ?>> GRAB_HIT = new SimpleMultiHitAttack<AbstractPurpleHazeEntity<?, ?>>(0,
            34, 0.75f, 1f, 10, 2f, 0f, 0f, IntSet.of(6, 8, 10, 12, 14, 16, 18))
            .withImpactSound(JSoundRegistry.IMPACT_1)
            .withStunType(StunType.UNBURSTABLE)
            .withFinisher(19, GRAB_HIT_FINAL)
            .allowHitUser()
            .withInfo(
                    Component.literal("Grab (Final Hit)"),
                    Component.empty()
            );
    public static final GrabAttack<PurpleHazeEntity, State> GRAB = new GrabAttack<>(
            280, 12, 24, 0.75f, 0f, 45, 1.5f, 0f, 0f,
            GRAB_HIT, StateContainer.of(State.GRAB_HIT), 25, 1)
            .withCrouchingVariant(GROUND_SLAM)
            .withSound(JSoundRegistry.D4C_THROW)
            .withImpactSound(JSoundRegistry.PH_GRAB_HIT)
            .allowHitUser()
            .withInfo(
                    Component.literal("Grab"),
                    Component.literal("unblockable, combo finisher")
            );

    private static final PlayMove PLAY = new PlayMove(0, 30, 31)
            .withInfo(Component.literal("Playing with flower"), Component.empty());

    public static final int MAX_RAGE = 20 * 60;
    private int rage = 0;
    private boolean flowerable = false, hasFlower = false, toEvolve = false;

    public PurpleHazeEntity(Level worldIn) {
        super(JStandTypeRegistry.PURPLE_HAZE.get(), worldIn);

        auraColors = new Vector3f[]{
                new Vector3f(1.0f, 0.2f, 0.6f),
                new Vector3f(0.3f, 1.0f, 0.6f),
                new Vector3f(1.0f, 1.0f, 1.0f),
                new Vector3f(0.5f, 0.3f, 1.0f)
        };
    }

    @Override
    public Vector3f getAuraColor() {
        if (rage == MAX_RAGE) {
            return new Vector3f(1f, 0f, 0f);
        }
        return super.getAuraColor();
    }

    @Override
    public void desummon() {
        if (toEvolve && hasUser()) {
            JComponentPlatformUtils.getStandComponent(getUserOrThrow())
                    .setType(JStandTypeRegistry.PURPLE_HAZE_DISTORTION.get());
            JCraft.summon(level(), getUserOrThrow());
        }

        super.desummon();
    }

    private static void registerMoves(MoveMap<PurpleHazeEntity, State> moves) {
        MoveMap.Entry<PurpleHazeEntity, State> light = moves.register(MoveClass.LIGHT, LIGHT_ATTACK, State.PUNCH);
        light.withFollowup(State.LIGHT_FOLLOWUP);
        light.withCrouchingVariant(State.BACKHAND).withFollowup(State.BACKHAND_FOLLOWUP);

        moves.register(MoveClass.BARRAGE, BARRAGE_ATTACK, State.BARRAGE);
        moves.register(MoveClass.HEAVY, HEAVY_ATTACK, State.HEAVY);

        moves.register(MoveClass.SPECIAL1, LAUNCH_CAPSULE, State.LAUNCH).withCrouchingVariant(State.LAUNCH2);
        moves.register(MoveClass.SPECIAL2, REKKA_1, State.REKKA1);
        moves.register(MoveClass.SPECIAL3, GRAB, State.GRAB).withCrouchingVariant(State.GROUND_SLAM);

        moves.register(MoveClass.ULTIMATE, FULL_RELEASE, State.FULL_RELEASE);
    }

    @Override
    public void queueMove(MoveInputType type) {
        if (!remoteControllable() && queuedMove == MoveInputType.STAND_SUMMON) {
            return;
        }
        super.queueMove(type);
    }

    public boolean handleMove(MoveClass moveClass) {
        MoveMap.Entry<PurpleHazeEntity, State> entry = getFirstValidEntry(moveClass);
        if (entry == null) {
            return false;
        }

        LivingEntity stateChecker = (isRemote() && !remoteControllable()) ? this : getUserOrThrow();

        if (hasUser() && !stateChecker.onGround() && entry.getAerialVariant() != null) {
            entry = entry.getAerialVariant();
        }
        if (hasUser() && stateChecker.isShiftKeyDown() && entry.getCrouchingVariant() != null) {
            entry = entry.getCrouchingVariant();
        }
        if (hasUser() && !stateChecker.onGround() && entry.getAerialVariant() != null) {
            entry = entry.getAerialVariant();
        }

        AbstractMove<?, ? super PurpleHazeEntity> move = entry.getMove();
        return handleMove(move.isCopyOnUse() ? move.copy() : move, entry.getCooldownType(), entry.getAnimState());
    }

    @Override
    public boolean allowMoveHandling() {
        return remoteControllable();
    }

    @Override
    public boolean remoteControllable() {
        return false;
    }

    @Override
    public void standBlock() {
        if (!hasUser()) {
            return;
        }
        // Projectile deflection
        List<Projectile> toDeflect = this.level().getEntitiesOfClass(Projectile.class, this.getBoundingBox().inflate(0.75f), EntitySelector.ENTITY_STILL_ALIVE);

        for (Projectile projectile : toDeflect) {
            if (projectile.getOwner() == getUserOrThrow()) {
                continue;
            }
            projectile.setDeltaMovement(projectile.getDeltaMovement().scale(-0.5).add(0, -0.1, 0));
            projectile.hurtMarked = true;
        }

        if (!isRemote()) {
            JCraft.stun(getUserOrThrow(), 2, 2);
        }
        getUserOrThrow().addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 5, 3, false, false, true));
    }

    @Override
    public void tick() {
        super.tick();

        if (hasFlower) {
            if (getMoveStun() > 0) {
                rage = 0;
                if (navigation.isInProgress()) {
                    navigation.stop();
                }
            } else {
                desummon();
            }
        } else {
            if (++rage >= MAX_RAGE) {
                rage = MAX_RAGE;
            }

            if (!hasUser()) {
                return;
            }
            LivingEntity user = getUser();

            if (!level().isClientSide()) {
                boolean isRemote = isRemote();

                if (!remoteControllable()) {
                    if (getAlphaOverride() != 1.0f) {
                        setAlphaOverride(1.0f);
                    }

                    LivingEntity target = getTarget();
                    if ((target != null && !target.isAlive()) || (target instanceof Player player && (player.isCreative() || player.isSpectator()))) {
                        target = null;
                    }
                    // else if (target instanceof StandEntity<?,?> standTarget && standTarget.hasUser()) { setTarget(standTarget.getUserOrThrow()); }

                    if (target == null) {
                        List<LivingEntity> potentialTargets = level().getEntitiesOfClass(
                                LivingEntity.class,
                                getBoundingBox().inflate(64.0),
                                EntitySelector.NO_CREATIVE_OR_SPECTATOR.and(EntitySelector.LIVING_ENTITY_STILL_ALIVE));
                        potentialTargets.remove(this);

                        Comparator<Entity> distanceComparator = (entity1, entity2) -> {
                            double distance1 = this.distanceToSqr(entity1);
                            double distance2 = this.distanceToSqr(entity2);
                            return Double.compare(distance1, distance2);
                        };
                        potentialTargets.sort(distanceComparator);

                        for (LivingEntity potentialTarget : potentialTargets) {
                            if (!hasLineOfSight(potentialTarget)) {
                                continue;
                            }
                            if (potentialTarget instanceof StandEntity<?, ?> standEntity && standEntity.hasUser()) {
                                setTarget(standEntity.getUserOrThrow());
                                break;
                            }
                            if (potentialTarget == user) {
                                if (tickCount % 20 == 0 && random.nextDouble() * MAX_RAGE <= rage) {
                                    setTarget(user);
                                    break;
                                }
                                continue;
                            }
                            setTarget(potentialTarget);
                            break;
                        }
                    } else {
                        double speed = getAttributeValue(Attributes.MOVEMENT_SPEED);
                        // user is not null (see above)
                        if (Objects.requireNonNull(user).hasEffect(JStatusRegistry.DAZED.get())) {
                            speed = user.getSpeed();
                        }
                        if (tickCount % 4 == 0) // Pathfinding is expensive
                        {
                            navigation.moveTo(target, speed);
                        }
                    }

                    if (!isRemote) {
                        setRemote(true);
                    }
                }

                if (isRemote) {
                    tickRemoteState(getMoveControl().getSpeedModifier(), getMoveControl().strafeRight, onGround());
                }
            }
        }
    }

    @Override
    public float getSpeed() {
        return 0.15F;
    }

    @Override
    protected float getFlyingSpeed() {
        return 0.02F;
    }

    @Override
    protected void tickRemoteState(double f, double s, boolean dashing) {
        LivingEntity user = getUserOrThrow();
        if (getState() == State.IDLE) {
            if (JUtils.canAct(user)) {
                if (s > 0) {
                    setStateNoReset(dashing ? State.RIGHT : State.RIGHT_DASH);
                }
                if (s < 0) {
                    setStateNoReset(dashing ? State.LEFT : State.LEFT_DASH);
                }
                if (f < 0) {
                    setStateNoReset(dashing ? State.BACKWARD : State.BACKWARD_DASH);
                }
                if (f > 0) {
                    setStateNoReset(dashing ? State.FORWARD : State.FORWARD_DASH);
                }
            } else {
                setStateNoReset(State.HURT);
            }
        }
    }

    @Override
    @NonNull
    public PurpleHazeEntity getThis() {
        return this;
    }

    @Override
    public void freshKill(@Nullable LivingEntity entity) {
        super.freshKill(entity);
        if (!StandTypeUtil.isNone(JComponentPlatformUtils.getStandComponent(entity).getType())) {
            flowerable = true;
        }
    }

    @Override
    protected @NotNull InteractionResult mobInteract(Player player, @NotNull InteractionHand hand) {
        final ItemStack stack = player.getItemInHand(hand);
        if (player == getUser() && stack.is(ItemTags.FLOWERS)) {
            if (!flowerable) {
                return InteractionResult.FAIL;
            }

            setItemInHand(InteractionHand.MAIN_HAND, stack.copy());
            stack.shrink(1);
            flowerable = false;
            hasFlower = true;

            if (!level().isClientSide()) {
                setMove(PLAY, State.PLAY);

                final CommonPhComponent ph = JComponentPlatformUtils.getPhData(player);
                ph.increaseLevel();
                if (ph.getLevel() >= 5) {
                    ph.resetLevel();
                    toEvolve = true;
                }
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    // Animation code
    public enum State implements StandAnimationState<PurpleHazeEntity> {
        IDLE((PurpleHaze, builder) -> builder.setAnimation(RawAnimation.begin().thenLoop("animation.purple_haze.idle"))),
        PUNCH(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.purple_haze.light"))),
        BLOCK(builder -> builder.setAnimation(RawAnimation.begin().thenLoop("animation.purple_haze.block"))),
        HEAVY(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.purple_haze.heavy"))),

        FULL_RELEASE(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.purple_haze.full_release"))),
        GROUND_SLAM(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.purple_haze.ground_slam"))),

        BARRAGE(builder -> builder.setAnimation(RawAnimation.begin().thenLoop("animation.purple_haze.barrage"))),
        LAUNCH(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.purple_haze.launch"))),
        LAUNCH2(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.purple_haze.launch2"))),

        REKKA1(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.purple_haze.rekka1"))),
        REKKA2(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.purple_haze.rekka2"))),
        REKKA3(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.purple_haze.rekka3"))),

        GRAB(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.purple_haze.grab"))),
        GRAB_HIT(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.purple_haze.grab_hit"))),

        BACKHAND(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.purple_haze.backhand"))),
        BACKHAND_FOLLOWUP(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.purple_haze.backhand_followup"))),
        LIGHT_FOLLOWUP(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.purple_haze.light_followup"))),
        HURT(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.purple_haze.hurt"))),
        PLAY(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.purple_haze.play"))),

        FORWARD(builder -> builder.setAnimation(RawAnimation.begin().thenLoop("animation.purple_haze.forw"))),
        BACKWARD(builder -> builder.setAnimation(RawAnimation.begin().thenLoop("animation.purple_haze.back"))),
        LEFT(builder -> builder.setAnimation(RawAnimation.begin().thenLoop("animation.purple_haze.left"))),
        RIGHT(builder -> builder.setAnimation(RawAnimation.begin().thenLoop("animation.purple_haze.right"))),
        FORWARD_DASH(builder -> builder.setAnimation(RawAnimation.begin().thenLoop("animation.purple_haze.fdash"))),
        BACKWARD_DASH(builder -> builder.setAnimation(RawAnimation.begin().thenLoop("animation.purple_haze.bdash"))),
        LEFT_DASH(builder -> builder.setAnimation(RawAnimation.begin().thenLoop("animation.purple_haze.ldash"))),
        RIGHT_DASH(builder -> builder.setAnimation(RawAnimation.begin().thenLoop("animation.purple_haze.rdash"))),
        ;

        private final BiConsumer<PurpleHazeEntity, AnimationState<PurpleHazeEntity>> animator;

        State(Consumer<AnimationState<PurpleHazeEntity>> animator) {
            this((silverChariot, builder) -> animator.accept(builder));
        }

        State(BiConsumer<PurpleHazeEntity, AnimationState<PurpleHazeEntity>> animator) {
            this.animator = animator;
        }

        @Override
        public void playAnimation(PurpleHazeEntity attacker, AnimationState<PurpleHazeEntity> state) {
            animator.accept(attacker, state);
        }
    }

    @Override
    protected State[] getStateValues() {
        return State.values();
    }

    @Override
    protected @NonNull String getSummonAnimation() {
        return "animation.purple_haze.summon";
    }

    @Override
    public State getBlockState() {
        return State.BLOCK;
    }
}
