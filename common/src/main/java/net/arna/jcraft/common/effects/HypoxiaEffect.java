package net.arna.jcraft.common.effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.jetbrains.annotations.NotNull;

public class HypoxiaEffect extends MobEffect {

    public HypoxiaEffect() {
        super(MobEffectCategory.HARMFUL, 0x858c30);
        this.addAttributeModifier(Attributes.MOVEMENT_SPEED,
                "778B48FC-485B-5BA7-58C7-E0D755CE354D",
                -0.2,
                AttributeModifier.Operation.MULTIPLY_BASE
        );
    }

    // Every quarter second
    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration % 5 == 0;
    }

    @Override
    public void applyEffectTick(@NotNull LivingEntity livingEntity, int amplifier) {
        super.applyEffectTick(livingEntity, amplifier);
        livingEntity.setAirSupply((int) (livingEntity.getAirSupply() * 0.9));
    }
}
