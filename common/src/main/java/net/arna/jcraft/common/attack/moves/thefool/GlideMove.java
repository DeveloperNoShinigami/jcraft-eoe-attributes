package net.arna.jcraft.common.attack.moves.thefool;

import lombok.NonNull;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.moves.base.AbstractMove;
import net.arna.jcraft.common.entity.stand.TheFoolEntity;
import net.arna.jcraft.common.attack.MobilityType;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import java.util.Set;

public final class GlideMove extends AbstractMove<GlideMove, TheFoolEntity> {
    public GlideMove(int cooldown, int windup, int duration, float moveDistance) {
        super(cooldown, windup, duration, moveDistance);
        mobilityType = MobilityType.FLIGHT;
    }

    @Override
    public void tick(TheFoolEntity attacker, int moveStun) {
        super.tick(attacker, moveStun);

        final LivingEntity user = attacker.getUser();
        if (user == null) {
            return;
        }

        user.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 4, 4, true, false));
        double yVel = attacker.getRemoteJumpInput() ? 0.07 : 0;
        final Vec3 rotVec = user.getLookAngle().scale(0.04);
        user.push(rotVec.x, yVel, rotVec.z);
        user.hurtMarked = true;
    }

    @Override
    public @NonNull Set<LivingEntity> perform(TheFoolEntity attacker, LivingEntity user, MoveContext ctx) {
        attacker.setSand(false); // Ends transformation state
        return Set.of();
    }

    @Override
    protected @NonNull GlideMove getThis() {
        return this;
    }

    @Override
    public @NonNull GlideMove copy() {
        return copyExtras(new GlideMove(getCooldown(), getWindup(), getDuration(), getMoveDistance()));
    }
}
