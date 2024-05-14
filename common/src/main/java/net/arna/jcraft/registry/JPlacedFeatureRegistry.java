package net.arna.jcraft.registry;

import net.arna.jcraft.JCraft;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.gen.feature.PlacedFeature;

public interface JPlacedFeatureRegistry {

    RegistryKey<PlacedFeature> SAND_DISK = RegistryKey.of(RegistryKeys.PLACED_FEATURE, JCraft.id("sand_disk_pf"));

}
