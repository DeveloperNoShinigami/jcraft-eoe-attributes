package net.arna.jcraft.mixin;

import net.minecraft.world.level.lighting.DataLayerStorageMap;
import net.minecraft.world.level.lighting.LayerLightSectionStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LayerLightSectionStorage.class)
public interface LightStorageAccessor {
    @Accessor
    DataLayerStorageMap<?> getStorage();

    @Accessor
    DataLayerStorageMap<?> getUncachedStorage();
}
