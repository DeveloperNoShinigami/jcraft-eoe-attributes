package net.arna.jcraft.registry;

import net.arna.jcraft.JCraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

public interface JConfiguredFeatureRegistry {

    ResourceKey<ConfiguredFeature<?,?>> SAND_DISK = ResourceKey.create(Registries.CONFIGURED_FEATURE, JCraft.id("sand_disk_cf"));

}
