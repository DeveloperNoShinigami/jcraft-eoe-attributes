package net.arna.jcraft.common.effects;

import net.arna.jcraft.common.entity.stand.StandEntity;
import net.arna.jcraft.common.util.JUtils;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class StandlessEffect extends MobEffect {
    public StandlessEffect() {
        super(MobEffectCategory.NEUTRAL, 0x000000);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide()) {
            return;
        }
        StandEntity<?, ?> stand = JUtils.getStand(entity);
        if (stand != null) {
            stand.desummon();
        }
    }
}
