package net.arna.jcraft.common.effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;

public class WSPoisonEffect extends MobEffect {

    public WSPoisonEffect() {
        super(MobEffectCategory.HARMFUL, 0xccccdd);
    }

    // Should the status effect be applied and under what condition?
    @Override
    public boolean isDurationEffectTick(final int duration, final int amplifier) {
        return true;
    }

    // Stun heavily reduces speed
    @Override
    public void applyEffectTick(final LivingEntity entity, final int amplifier) {
        if (entity instanceof final Mob mob) {
            mob.setDeltaMovement(mob.getDeltaMovement().scale(0.2));

            mob.setTarget(null);
            mob.setAggressive(false);
        } else {
            entity.setPose(Pose.SWIMMING);
        }
    }
}
