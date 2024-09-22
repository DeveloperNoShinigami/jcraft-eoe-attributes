package net.arna.jcraft.common.attack.moves.shared;

import lombok.Getter;
import lombok.NonNull;
import net.arna.jcraft.common.attack.core.IAttacker;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.moves.base.AbstractMove;
import net.arna.jcraft.common.gravity.api.GravityChangerAPI;
import net.arna.jcraft.common.util.JUtils;
import net.arna.jcraft.common.attack.MobilityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import java.util.Set;

@Getter
public final class JumpMove<A extends IAttacker<? extends A, ?>> extends AbstractMove<JumpMove<A>, A> {
    private final float strength;

    public JumpMove(int cooldown, int windup, int duration, float moveDistance, float strength) {
        super(cooldown, windup, duration, moveDistance);
        this.strength = strength;
        mobilityType = MobilityType.DASH;
    }

    @Override
    public boolean canBeInitiated(A attacker) {
        return super.canBeInitiated(attacker) && attacker.getUser().onGround();
    }

    @Override
    public @NonNull Set<LivingEntity> perform(A attacker, LivingEntity user, MoveContext ctx) {
        if (user.onGround()) {
            final Vec3 upVel = Vec3.atLowerCornerOf(GravityChangerAPI.getGravityDirection(user).getNormal()).scale(-0.5);
            final Vec3 jumpVel = Vec3.directionFromRotation(user.getXRot(), user.getYRot()).scale(strength).add(upVel);
            JUtils.setVelocity(user, jumpVel.x, jumpVel.y, jumpVel.z);
        }

        return Set.of();
    }

    @Override
    protected @NonNull JumpMove<A> getThis() {
        return this;
    }

    @Override
    public @NonNull JumpMove<A> copy() {
        return copyExtras(new JumpMove<>(getCooldown(), getWindup(), getDuration(), getMoveDistance(), strength));
    }
}
