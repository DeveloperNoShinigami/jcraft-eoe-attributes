package net.arna.jcraft.common.attack.moves.goldexperience.requiem;

import lombok.NonNull;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.moves.base.AbstractMove;
import net.arna.jcraft.common.entity.stand.GEREntity;
import net.arna.jcraft.common.util.MobilityType;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import java.util.Set;

public class FlightMove extends AbstractMove<FlightMove, GEREntity> {
    public FlightMove(int cooldown, int windup, int duration, float moveDistance) {
        super(cooldown, windup, duration, moveDistance);
        mobilityType = MobilityType.FLIGHT;
    }

    @Override
    public @NonNull Set<LivingEntity> perform(GEREntity attacker, LivingEntity user, MoveContext ctx) {
        attacker.setFlightTime(20);
        return Set.of();
    }

    public void tickFlight(GEREntity attacker) {
        if (!attacker.hasUser()) {
            return;
        }
        LivingEntity user = attacker.getUserOrThrow();
        // Must be run on client and server because of fun mod compatibility
        int flightTime = attacker.getFlightTime();
        flightTime -= 1;
        attacker.setFlightTime(flightTime);
        if (user instanceof Player playerEntity) {
            if (!playerEntity.isCreative() && !playerEntity.isSpectator()) {
                playerEntity.getAbilities().flying = (flightTime > 1);
            }
        } else if (flightTime > 1) {
            double y = user.getY();
            Vec3 vel = new Vec3(user.getDeltaMovement().x, 0.0, user.getDeltaMovement().z);
            // Targetting priority
            LivingEntity targetEntity = (LivingEntity) user.getCombatTracker().getMostSignificantFall().source().getEntity();
            if (targetEntity == null && user instanceof Mob mob) {
                targetEntity = mob.getTarget();
            }
            if (targetEntity == null) {
                targetEntity = user.getLastHurtByMob();
            }
            // If target wasn't found, search in a radius
            Vec3 target = targetEntity != null ? targetEntity.getEyePosition() :
                    attacker.position().add(Math.sin(attacker.tickCount * 0.2) * 3, 0, Math.cos(attacker.tickCount * 0.2) * 3);

            double dY = Mth.clamp(target.y() - y, -1, 1);
            y += dY;

            vel = vel.add(target.subtract(user.position()).normalize()).scale(0.4);

            user.setDeltaMovement(vel);
            user.setPosRaw(user.getX(), y, user.getZ());

            user.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 5, 1, true, false));
        }
    }

    @Override
    protected @NonNull FlightMove getThis() {
        return this;
    }

    @Override
    public @NonNull FlightMove copy() {
        return copyExtras(new FlightMove(getCooldown(), getWindup(), getDuration(), getMoveDistance()));
    }
}
