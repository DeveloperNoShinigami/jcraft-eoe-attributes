package net.arna.jcraft.datagen.providers.data;

import net.arna.jcraft.api.registry.JBlockRegistry;
import net.arna.jcraft.api.registry.JConfiguredFeatureRegistry;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.DiskConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.RuleBasedBlockStateProvider;

// see https://minecraft.fandom.com/wiki/Custom_feature
public class JConfiguredFeatureProvider {

    public static void bootstrap(BootstapContext<ConfiguredFeature<?,?>> context) {
        final Block hotSand = JBlockRegistry.HOT_SAND_BLOCK.get();
        final var diskConfig = new DiskConfiguration(RuleBasedBlockStateProvider.simple(Blocks.SAND), BlockPredicate.matchesBlocks(hotSand), UniformInt.of(3, 6), 2);
        context.register(JConfiguredFeatureRegistry.SAND_DISK, new ConfiguredFeature<>(Feature.DISK, diskConfig));
    }

}
