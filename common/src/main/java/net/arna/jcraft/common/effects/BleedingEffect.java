package net.arna.jcraft.common.effects;

import net.arna.jcraft.common.entity.damage.JDamageSources;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class BleedingEffect extends MobEffect {
    public BleedingEffect() {
        super(MobEffectCategory.HARMFUL, 0x6F1616);
    }

    @Override
    public boolean isDurationEffectTick(final int duration, final int amplifier) {
        int i = 40 >> amplifier;
        if (i > 0) {
            return duration % i == 0;
        } else {
            return true;
        }
    }

    @Override
    public void applyEffectTick(final LivingEntity entity, final int amplifier) {
        entity.hurt(JDamageSources.bleeding(entity.level()), 1.0f);
    }
}
