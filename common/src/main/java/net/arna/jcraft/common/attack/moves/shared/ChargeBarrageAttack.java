package net.arna.jcraft.common.attack.moves.shared;

import lombok.NonNull;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.attack.core.IAttacker;
import net.arna.jcraft.common.attack.core.StunType;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.moves.base.AbstractBarrageAttack;
import net.arna.jcraft.common.entity.stand.StandEntity;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.Set;

import static net.arna.jcraft.common.attack.moves.base.AbstractChargeAttack.prepDetachmentMove;

public final class ChargeBarrageAttack<A extends IAttacker<? extends A, ?>> extends AbstractBarrageAttack<ChargeBarrageAttack<A>, A> {
    private final float originalMoveDistance;
    private final boolean quadraticMovement;

    public ChargeBarrageAttack(int cooldown, int windup, int duration, float moveDistance, float damage, int stun,
                               float hitboxSize, float knockback, float offset, int interval, boolean quadraticMovement) {
        super(cooldown, windup, duration, moveDistance, damage, stun, hitboxSize, knockback, offset, interval);
        this.originalMoveDistance = moveDistance;
        this.quadraticMovement = quadraticMovement;

        withCopyOnUse();
        withStunType(StunType.BURSTABLE);
        charge = true;
        ranged = true;
        inflictsSlowness = false;
    }

    @Override
    public void onInitiate(A attacker) {
        super.onInitiate(attacker);
        withMoveDistance(originalMoveDistance);
    }

    @Override
    public void tick(A attacker, int moveStun) {
        super.tick(attacker, moveStun);
        final Entity attackerEntity = attacker.getBaseEntity();
        if (attackerEntity instanceof StandEntity<?, ?> stand) {
            tickChargeBarrageAttack(stand, moveStun < getWindupPoint(), getMoveDistance(), getWindupPoint(), moveStun);
        } else {
            JCraft.LOGGER.error("Trying to tick ChargeBarrageAttack on non-stand entity; " + attackerEntity);
        }
    }

    private Vec3 advanceChargePos(StandEntity<?, ?> attacker, float moveDistance, int windupPoint, int moveStun) {
        if (quadraticMovement) {
            return attacker.position().add(getRotVec(attacker).scale(
                    (moveDistance * moveStun) / (windupPoint * windupPoint)
            ));
        }
        return attacker.position().add(getRotVec(attacker).scale(moveDistance / windupPoint));
    }

    private void tickChargeBarrageAttack(StandEntity<?, ?> attacker, boolean shouldPerform, float moveDistance, int windupPoint, int moveStun) {
        if (shouldPerform) {
            final Vec3 newPos = advanceChargePos(attacker, moveDistance, windupPoint, moveStun);
            attacker.setFreePos(new Vector3f((float) newPos.x, (float) newPos.y, (float) newPos.z));
            attacker.setFree(true);
        } else {
            prepDetachmentMove(attacker, attacker.getUserOrThrow());
        }
    }

    @Override
    public @NonNull Set<LivingEntity> perform(A attacker, LivingEntity user, MoveContext ctx) {
        final Set<LivingEntity> targets = super.perform(attacker, user, ctx);
        final Entity attackerEntity = attacker.getBaseEntity();
        if (targets.isEmpty() || attackerEntity == null) {
            return targets;
        }

        // Moves slower if hit targets are closer, thus not phasing through them.
        Vec3 avgPos = Vec3.ZERO;
        float c = 0;
        for (LivingEntity target : targets) {
            if (target instanceof StandEntity<?, ?>) {
                continue;
            }
            avgPos = avgPos.add(target.position());
            c += 1f;
        }
        avgPos = avgPos.scale(1f / c);
        attackerEntity.lookAt(EntityAnchorArgument.Anchor.EYES, avgPos);
        withMoveDistance((float) avgPos.distanceToSqr(attackerEntity.position()) + 0.1f);

        return targets;
    }

    @Override
    protected Vec3 getOffsetForwardPos(A attacker, Vec3 offsetHeightPos, Vec3 upVec, Vec3 rotVec) {
        return offsetHeightPos.add(rotVec);
    }

    @Override
    protected @NonNull ChargeBarrageAttack<A> getThis() {
        return this;
    }

    @Override
    public @NonNull ChargeBarrageAttack<A> copy() {
        return copyExtras(new ChargeBarrageAttack<>(getCooldown(), getWindup(), getDuration(), getMoveDistance(), getDamage(),
                getStun(), getHitboxSize(), getKnockback(), getOffset(), getInterval(), quadraticMovement));
    }
}
