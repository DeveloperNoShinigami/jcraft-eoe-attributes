package net.arna.jcraft.registry;

import net.arna.jcraft.JCraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public interface JPlacedFeatureRegistry {

    ResourceKey<PlacedFeature> SAND_DISK = ResourceKey.create(Registries.PLACED_FEATURE, JCraft.id("sand_disk_pf"));

}
