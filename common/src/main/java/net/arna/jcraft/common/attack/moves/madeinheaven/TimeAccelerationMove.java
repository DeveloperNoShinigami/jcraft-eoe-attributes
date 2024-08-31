package net.arna.jcraft.common.attack.moves.madeinheaven;

import lombok.Getter;
import lombok.NonNull;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.moves.base.AbstractMove;
import net.arna.jcraft.common.entity.stand.MadeInHeavenEntity;
import net.arna.jcraft.common.network.s2c.TimeAccelStatePacket;
import net.arna.jcraft.registry.JStatusRegistry;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.IntSupplier;

@Getter
public final class TimeAccelerationMove extends AbstractMove<TimeAccelerationMove, MadeInHeavenEntity> {
    private final IntSupplier accelerationDuration;

    public TimeAccelerationMove(int cooldown, int windup, int duration, float moveDistance, IntSupplier accelerationDuration) {
        super(cooldown, windup, duration, moveDistance);
        this.accelerationDuration = accelerationDuration;
    }

    @Override
    public @NonNull Set<LivingEntity> perform(MadeInHeavenEntity attacker, LivingEntity user, MoveContext ctx) {
        int accelTime = accelerationDuration.getAsInt();
        attacker.setAccelTime(accelTime);
        attacker.setAfterimage(true);
        TimeAccelStatePacket.sendStart(Objects.requireNonNull(attacker.getServer()).getPlayerList(), attacker, accelTime);

        return Set.of();
    }

    public void tickTimeAcceleration(MadeInHeavenEntity attacker) {
        int aTime = attacker.getAccelTime();
        attacker.setAccelTime(aTime - 1);

        if (aTime > 1) {
            List<Entity> toCatch = attacker.level().getEntitiesOfClass(Entity.class,
                    attacker.getBoundingBox().inflate(96), EntitySelector.NO_CREATIVE_OR_SPECTATOR);
            for (Entity entity : toCatch) {
                if (entity instanceof LivingEntity) {
                    continue;
                }
                entity.tick();
            }
        } else if (aTime == 1) {
            if (attacker.getSpeedometer() == MadeInHeavenEntity.MAXIMUM_SPEEDOMETER) {
                List<LivingEntity> toCatch = attacker.level().getEntitiesOfClass(LivingEntity.class,
                        attacker.getBoundingBox().inflate(96),
                        EntitySelector.NO_CREATIVE_OR_SPECTATOR.and(e -> e != attacker && e != attacker.getUser()));

                for (LivingEntity entity : toCatch) // 15s of Standless to any victims of Universe Reset
                {
                    entity.addEffect(new MobEffectInstance(JStatusRegistry.STANDLESS.get(), 300, 0, true, false));
                }
            }

            attacker.setAfterimage(false);
            attacker.setSpeedometer(0);
        }
    }

    @Override
    protected @NonNull TimeAccelerationMove getThis() {
        return this;
    }

    @Override
    public @NonNull TimeAccelerationMove copy() {
        return copyExtras(new TimeAccelerationMove(getCooldown(), getWindup(), getDuration(), getMoveDistance(),
                getAccelerationDuration()));
    }
}
