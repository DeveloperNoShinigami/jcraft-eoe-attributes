package net.arna.jcraft.mixin;

import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.lighting.LightEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LevelLightEngine.class)
public interface LightingProviderAccessor {
    @Accessor
    LightEngine<?, ?> getBlockLightProvider();

    @Accessor
    LightEngine<?, ?> getSkyLightProvider();
}
