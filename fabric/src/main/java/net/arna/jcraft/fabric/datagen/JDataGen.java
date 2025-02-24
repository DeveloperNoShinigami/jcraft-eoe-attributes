package net.arna.jcraft.fabric.datagen;

import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.attack.core.data.MoveSet;
import net.arna.jcraft.common.entity.stand.StandType;
import net.arna.jcraft.common.spec.SpecType;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;

import java.util.Arrays;

public final class JDataGen implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator) {
        final FabricDataGenerator.Pack pack = generator.createPack();
        pack.addProvider(JModelProvider::new);
        //pack.addProvider(JLanguageProvider::new);
        pack.addProvider(JLootTableProviders.BlockLoot::new);
        pack.addProvider(JLootTableProviders.EntityLoot::new);
        pack.addProvider(JTagProviders.JBlockTags::new);
        pack.addProvider(JTagProviders.JItemTags::new);
        pack.addProvider(JTagProviders.JEntityTypeTags::new);
        pack.addProvider(JAdvancementProvider::new);
        pack.addProvider(JRecipeProvider::new);
        pack.addProvider(JWorldProvider::new);
        pack.addProvider(JEvolutionProvider::new);

        Arrays.stream(StandType.values())
                .filter(t -> t != StandType.NONE)
                .forEach(type -> {
                    if (!MoveSet.hasMoveSets(type)) {
                        JCraft.LOGGER.error("No move sets found for stand type {}", type);
                        return;
                    }

                    pack.addProvider((FabricDataOutput output) -> new JStandMoveSetProvider<>(output, type));
                });

        Arrays.stream(SpecType.values())
                .filter(t -> t != SpecType.NONE)
                .forEach(type -> {
                    if (!MoveSet.hasMoveSets(type)) {
                        JCraft.LOGGER.error("No move sets found for spec type {}", type);
                        return;
                    }

                    pack.addProvider((FabricDataOutput output) -> new JSpecMoveSetProvider<>(output, type));
                });
    }

    @Override
    public void buildRegistry(RegistrySetBuilder registryBuilder) {
        registryBuilder.add(Registries.CONFIGURED_FEATURE, JConfiguredFeatureProvider::bootstrap);
        registryBuilder.add(Registries.PLACED_FEATURE, JPlacedFeatureProvider::bootstrap);
        registryBuilder.add(Registries.BIOME, JBiomeProvider::bootstrap);
    }
}
