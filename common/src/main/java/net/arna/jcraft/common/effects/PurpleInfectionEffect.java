package net.arna.jcraft.common.effects;

import net.arna.jcraft.common.entity.damage.JDamageSources;
import net.arna.jcraft.common.entity.stand.StandType;
import net.arna.jcraft.platform.JComponentPlatformUtils;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class PurpleInfectionEffect extends MobEffect {
    public PurpleInfectionEffect() {
        super(MobEffectCategory.HARMFUL, 0xA34AB5);
    }

    @Override
    public boolean isDurationEffectTick(final int duration, final int amplifier) {
        int i = 0b101000 >> amplifier;
        if (i > 0) {
            return duration % i == 0;
        } else {
            return true;
        }
    }

    @Override
    public void applyEffectTick(final LivingEntity entity, final int amplifier) {
        StandType standType = JComponentPlatformUtils.getStandData(entity).getType();
        float damage = 0.6666f; // 1/3rd of a heart
        if (standType == StandType.PURPLE_HAZE_DISTORTION) {
            damage /= 3.0f;
        }
        entity.hurt(JDamageSources.phpoison(entity.level()), damage);
    }
}
