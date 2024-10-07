package net.arna.jcraft.common.effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class OutOfBodyEffect extends MobEffect {
    public OutOfBodyEffect() {
        super(MobEffectCategory.NEUTRAL, 0x5B6EE1);
    }

    @Override
    public boolean isDurationEffectTick(final int duration, final int amplifier) {
        return false;
    }
}
