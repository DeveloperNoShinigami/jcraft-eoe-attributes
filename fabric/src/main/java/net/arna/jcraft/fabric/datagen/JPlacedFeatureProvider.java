package net.arna.jcraft.fabric.datagen;

import net.arna.jcraft.registry.JConfiguredFeatureRegistry;
import net.arna.jcraft.registry.JPlacedFeatureRegistry;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.feature.OrePlacedFeatures;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.placementmodifier.HeightRangePlacementModifier;

// see https://minecraft.fandom.com/wiki/Custom_feature
public class JPlacedFeatureProvider {

    public static void bootstrap(Registerable<PlacedFeature> context) {
        final var cfLookup = context.getRegistryLookup(RegistryKeys.CONFIGURED_FEATURE);
        final var sandDiskCf = cfLookup.getOrThrow(JConfiguredFeatureRegistry.SAND_DISK);
        // can generate at 64 (sea level) ± 32 blocks
        final var sandDiskHeightRange = HeightRangePlacementModifier.uniform(YOffset.fixed(32), YOffset.fixed(96));
        context.register(JPlacedFeatureRegistry.SAND_DISK, new PlacedFeature(sandDiskCf, OrePlacedFeatures.modifiersWithCount(1, sandDiskHeightRange)));
    }

}
