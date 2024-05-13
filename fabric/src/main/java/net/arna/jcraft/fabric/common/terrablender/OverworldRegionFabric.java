package net.arna.jcraft.fabric.common.terrablender;

import com.mojang.datafixers.util.Pair;
import net.arna.jcraft.registry.JBiomeRegistry;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import terrablender.api.Region;
import terrablender.api.RegionType;
import terrablender.api.VanillaParameterOverlayBuilder;
import terrablender.api.ParameterUtils.*;

import java.util.function.Consumer;

public class OverworldRegionFabric extends Region {
    public OverworldRegionFabric(Identifier name, int weight) {
        super(name, RegionType.OVERWORLD, weight);
    }

    @Override
    public void addBiomes(Registry<Biome> registry, Consumer<Pair<MultiNoiseUtil.NoiseHypercube, RegistryKey<Biome>>> mapper) {
        VanillaParameterOverlayBuilder builder = new VanillaParameterOverlayBuilder();
        // The parameters for this biome are chosen to resemble the ones for the desert biome.
        new ParameterPointListBuilder()
                .temperature(Temperature.HOT)
                .humidity(Humidity.FULL_RANGE)
                .continentalness(Continentalness.MID_INLAND)
                .erosion(Erosion.EROSION_4, Erosion.EROSION_6)
                .depth(Depth.SURFACE)
                .weirdness(Weirdness.FULL_RANGE)
                .build().forEach(point -> builder.add(point, JBiomeRegistry.DEVILS_PALM));
        // add our points to the mapper
        builder.build().forEach(mapper);
    }
}
