package net.arna.jcraft.fabric.datagen;

import net.arna.jcraft.registry.JBiomeRegistry;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.biome.SpawnSettings;

public class JBiomeProvider {

    public static void bootstrap(Registerable<Biome> context) {
        context.register(JBiomeRegistry.DEVILS_PALM, devilsPalm(context));
    }

    private static Biome devilsPalm(Registerable<Biome> context) {
        SpawnSettings.Builder spawnBuilder = new SpawnSettings.Builder();

        GenerationSettings.LookupBackedBuilder generationBuilder =
                new GenerationSettings.LookupBackedBuilder(context.getRegistryLookup(RegistryKeys.PLACED_FEATURE),
                        context.getRegistryLookup(RegistryKeys.CONFIGURED_CARVER));

        return new Biome.Builder()
                .precipitation(false)
                .downfall(0f)
                .temperature(3f)
                .generationSettings(generationBuilder.build())
                .spawnSettings(spawnBuilder.build())
                .effects((new BiomeEffects.Builder())
                        .waterColor(10203353)
                        .waterFogColor(6645121)
                        .skyColor(14987035)
                        .fogColor(16314057)
                        .build())
                .build();
    }

}
