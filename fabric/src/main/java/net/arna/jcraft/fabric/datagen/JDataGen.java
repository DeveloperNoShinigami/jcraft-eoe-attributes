package net.arna.jcraft.fabric.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.registry.RegistryBuilder;
import net.minecraft.registry.RegistryKeys;

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
    }

    @Override
    public void buildRegistry(RegistryBuilder registryBuilder) {
        registryBuilder.addRegistry(RegistryKeys.CONFIGURED_FEATURE, JConfiguredFeatureProvider::bootstrap);
        registryBuilder.addRegistry(RegistryKeys.PLACED_FEATURE, JPlacedFeatureProvider::bootstrap);
        registryBuilder.addRegistry(RegistryKeys.BIOME, JBiomeProvider::bootstrap);
    }
}