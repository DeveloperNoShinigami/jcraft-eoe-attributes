package net.arna.jcraft.fabric.datagen;

import net.arna.jcraft.api.attack.MoveSetManager;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;

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
        pack.addProvider(JStandDataProvider::new);

        // Each type needs its own MoveSetProvider as they have different state classes
        // and thus different codecs.
        for (final ResourceLocation type : MoveSetManager.getMoveSets().keySet()) {
            pack.addProvider((FabricDataOutput output) -> new JMoveSetProvider<>(output, type));
        }
    }

    @Override
    public void buildRegistry(RegistrySetBuilder registryBuilder) {
        registryBuilder.add(Registries.CONFIGURED_FEATURE, JConfiguredFeatureProvider::bootstrap);
        registryBuilder.add(Registries.PLACED_FEATURE, JPlacedFeatureProvider::bootstrap);
        registryBuilder.add(Registries.BIOME, JBiomeProvider::bootstrap);
    }
}
