package net.arna.jcraft.fabric.datagen;

import net.arna.jcraft.registry.JBlockRegistry;
import net.arna.jcraft.registry.JConfiguredFeatureRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registerable;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.gen.blockpredicate.BlockPredicate;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.DiskFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.stateprovider.PredicatedStateProvider;

public class JConfiguredFeatureProvider {

    public static void bootstrap(Registerable<ConfiguredFeature<?,?>> context) {
        final Block hotSand = JBlockRegistry.HOT_SAND_BLOCK.get();
        final var diskConfig = new DiskFeatureConfig(PredicatedStateProvider.of(Blocks.SAND), BlockPredicate.matchingBlocks(hotSand), UniformIntProvider.create(3, 6), 2);
        context.register(JConfiguredFeatureRegistry.SAND_DISK, new ConfiguredFeature<>(Feature.DISK, diskConfig));
    }

}
