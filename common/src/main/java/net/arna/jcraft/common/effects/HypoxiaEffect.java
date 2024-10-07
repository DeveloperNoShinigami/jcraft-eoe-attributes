package net.arna.jcraft.common.effects;

import lombok.NonNull;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class HypoxiaEffect extends MobEffect {

    public HypoxiaEffect() {
        super(MobEffectCategory.HARMFUL, 0x858c30);
        this.addAttributeModifier(Attributes.MOVEMENT_SPEED,
                "05090A04-DE4D-C881-80CA-0D0836831466",
                -0.2,
                AttributeModifier.Operation.MULTIPLY_BASE
        );
    }

    // Every quarter second
    @Override
    public boolean isDurationEffectTick(final int duration, final int amplifier) {
        return duration % 20 == 0;
    }

    @Override
    public void applyEffectTick(final @NonNull LivingEntity livingEntity, final int amplifier) {
        super.applyEffectTick(livingEntity, amplifier);
        final int airSupply = livingEntity.getAirSupply();
        if (airSupply <= 0) livingEntity.hurt(livingEntity.damageSources().drown(), 2.0F);
        livingEntity.setAirSupply(airSupply - 20);
    }
}
