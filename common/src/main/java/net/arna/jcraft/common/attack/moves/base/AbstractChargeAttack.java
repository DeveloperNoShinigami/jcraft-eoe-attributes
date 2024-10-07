package net.arna.jcraft.common.attack.moves.base;

import lombok.Getter;
import lombok.NonNull;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.entity.stand.StandEntity;
import net.arna.jcraft.common.util.StandAnimationState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.Set;

@Getter
public abstract class AbstractChargeAttack<T extends AbstractChargeAttack<T, A, S>, A extends StandEntity<A, S>, S extends Enum<S> & StandAnimationState<A>>
        extends AbstractSimpleAttack<T, A> {
    protected final S hitAnimState;

    protected AbstractChargeAttack(final int cooldown, final int windup, final int duration, final float moveDistance, final float damage, final int stun,
                                   final float hitboxSize, final float knockback, final float offset, final S hitAnimState) {
        super(cooldown, windup, duration, moveDistance, damage, stun, hitboxSize, knockback, offset);

        this.hitAnimState = hitAnimState;

        charge = true;
        ranged = true;

        // Charge attacks can't backstab
        this.withBackstab(false);
    }

    @Override
    public boolean shouldPerform(final A attacker, final int moveStun) {
        return hasWindupPassed(attacker, moveStun);
    }

    @Override
    public @NonNull Set<LivingEntity> perform(final A attacker, final LivingEntity user, final MoveContext ctx) {
        final Set<LivingEntity> targets = super.perform(attacker, user, ctx);
        if (!targets.isEmpty()) endCharge(attacker);
        return targets;
    }

    protected void endCharge(final A attacker) {
        attacker.setCurrentMove(null);
        attacker.setMoveStun(10);
        attacker.setState(hitAnimState);
    }

    @Override
    public void tick(final A attacker, final int moveStun) {
        super.tick(attacker, moveStun);

        tickChargeAttack(attacker, shouldPerform(attacker, moveStun), getMoveDistance(), getWindupPoint());
    }

    protected Vec3 advanceChargePos(final StandEntity<?, ?> attacker, final float moveDistance, final int windupPoint) {
        return attacker.position().add(getRotVec(attacker).scale(moveDistance / windupPoint));
    }

    protected void tickChargeAttack(final StandEntity<A, S> attacker, final boolean shouldPerform, final float moveDistance, final int windupPoint) {
        if (shouldPerform) {
            //float t = 1f - (float) curMoveStun / (float) realInitTime;
            final Vec3 newPos = advanceChargePos(attacker, moveDistance, windupPoint);
            //stand.setDistanceOffset(1 + attackDist * t * t);
            attacker.setFreePos(new Vector3f((float) newPos.x, (float) newPos.y, (float) newPos.z));
            attacker.setFree(true);
        } else {
            prepDetachmentMove(attacker, attacker.getUserOrThrow());
        }
    }

    public static void prepDetachmentMove(final StandEntity<?, ?> attacker, final LivingEntity user) {
        attacker.setPos(user.position());
        attacker.setYHeadRot(user.getYHeadRot());
        attacker.setYBodyRot(user.getYHeadRot());
        attacker.setRotationOffset(attacker.attackRotation);
    }

    @Override
    protected Vec3 getOffsetForwardPos(final A attacker, final Vec3 offsetHeightPos, final Vec3 upVec, final Vec3 rotVec) {
        return offsetHeightPos.add(rotVec);
    }
}
