package net.arna.jcraft.fabric.datagen;

import net.arna.jcraft.registry.JBlockRegistry;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;

import java.util.concurrent.CompletableFuture;

public class JTagProviders {
    public static class JBlockTags extends FabricTagProvider.BlockTagProvider {

        public JBlockTags(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
            super(output, registriesFuture);
        }

        @Override
        protected void configure(RegistryWrapper.WrapperLookup arg) {
            getTagBuilder(BlockTags.PICKAXE_MINEABLE).add(JBlockRegistry.METEORITE_BLOCK.getId());
            getTagBuilder(BlockTags.PICKAXE_MINEABLE).add(JBlockRegistry.METEORITE_IRON_ORE_BLOCK.getId());
            getTagBuilder(BlockTags.NEEDS_DIAMOND_TOOL).add(JBlockRegistry.METEORITE_BLOCK.getId());
            getTagBuilder(BlockTags.NEEDS_DIAMOND_TOOL).add(JBlockRegistry.METEORITE_IRON_ORE_BLOCK.getId());
            getTagBuilder(BlockTags.SHOVEL_MINEABLE).add(JBlockRegistry.HOT_SAND_BLOCK.getId());
        }
    }

    public static class JItemTags extends FabricTagProvider.ItemTagProvider {

        public JItemTags(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
            super(output, registriesFuture);
        }

        @Override
        protected void configure(RegistryWrapper.WrapperLookup arg) {

        }
    }

    public static class JEntityTypeTags extends FabricTagProvider.EntityTypeTagProvider {

        public JEntityTypeTags(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
            super(output, registriesFuture);
        }

        @Override
        protected void configure(RegistryWrapper.WrapperLookup arg) {

        }
    }

}