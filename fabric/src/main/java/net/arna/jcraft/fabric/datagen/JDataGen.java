package net.arna.jcraft.fabric.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

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
    }
}