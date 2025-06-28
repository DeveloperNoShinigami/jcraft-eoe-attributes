package net.arna.jcraft.datagen.providers.data;

import net.arna.jcraft.api.registry.JConfiguredFeatureRegistry;
import net.arna.jcraft.api.registry.JPlacedFeatureRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.placement.OrePlacements;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

// see https://minecraft.fandom.com/wiki/Custom_feature
public class JPlacedFeatureProvider {

    public static void bootstrap(BootstapContext<PlacedFeature> context) {
        final var cfLookup = context.lookup(Registries.CONFIGURED_FEATURE);
        final var sandDiskCf = cfLookup.getOrThrow(JConfiguredFeatureRegistry.SAND_DISK);
        // can generate at 64 (sea level) ± 32 blocks
        final var sandDiskHeightRange = HeightRangePlacement.uniform(VerticalAnchor.absolute(32), VerticalAnchor.absolute(96));
        context.register(JPlacedFeatureRegistry.SAND_DISK, new PlacedFeature(sandDiskCf, OrePlacements.commonOrePlacement(1, sandDiskHeightRange)));
    }

}
