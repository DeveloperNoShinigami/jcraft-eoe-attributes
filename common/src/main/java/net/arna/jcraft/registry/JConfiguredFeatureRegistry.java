package net.arna.jcraft.registry;

import net.arna.jcraft.JCraft;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.gen.feature.ConfiguredFeature;

public interface JConfiguredFeatureRegistry {

    RegistryKey<ConfiguredFeature<?,?>> SAND_DISK = RegistryKey.of(RegistryKeys.CONFIGURED_FEATURE, JCraft.id("sand_disk_cf"));

}
