package net.arna.jcraft.fabric.datagen;

import net.arna.jcraft.registry.JBlockRegistry;
import net.arna.jcraft.registry.JItemRegistry;
import net.arna.jcraft.registry.JTagRegistry;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.ItemTags;

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
            getTagBuilder(BlockTags.PICKAXE_MINEABLE).add(JBlockRegistry.STELLAR_IRON_BLOCK.getId());
            getTagBuilder(BlockTags.NEEDS_DIAMOND_TOOL).add(JBlockRegistry.METEORITE_BLOCK.getId());
            getTagBuilder(BlockTags.NEEDS_DIAMOND_TOOL).add(JBlockRegistry.METEORITE_IRON_ORE_BLOCK.getId());
            getTagBuilder(BlockTags.NEEDS_IRON_TOOL).add(JBlockRegistry.STELLAR_IRON_BLOCK.getId());
            getTagBuilder(BlockTags.SHOVEL_MINEABLE).add(JBlockRegistry.HOT_SAND_BLOCK.getId());

            getTagBuilder(BlockTags.SOUL_SPEED_BLOCKS).add(JBlockRegistry.SOUL_BLOCK.getId());
            getTagBuilder(BlockTags.BEACON_BASE_BLOCKS).add(JBlockRegistry.STELLAR_IRON_BLOCK.getId());
        }
    }

    public static class JItemTags extends FabricTagProvider.ItemTagProvider {

        public JItemTags(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
            super(output, registriesFuture);
        }

        @Override
        protected void configure(RegistryWrapper.WrapperLookup arg) {
            getTagBuilder(ItemTags.SAND).add(JItemRegistry.HOT_SAND_BLOCK.getId());
            getTagBuilder(ItemTags.SMELTS_TO_GLASS).add(JItemRegistry.HOT_SAND_BLOCK.getId());

            getTagBuilder(ItemTags.BEACON_PAYMENT_ITEMS).add(JItemRegistry.STELLAR_IRON_INGOT.getId());
            getTagBuilder(ItemTags.BOOKSHELF_BOOKS).add(JItemRegistry.DIOS_DIARY.getId());
        }
    }

    public static class JEntityTypeTags extends FabricTagProvider.EntityTypeTagProvider {

        public JEntityTypeTags(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
            super(output, registriesFuture);
        }

        @Override
        protected void configure(RegistryWrapper.WrapperLookup arg) {
            getTagBuilder(JTagRegistry.CAN_HAVE_STAND).add(Registries.ENTITY_TYPE.getId(EntityType.ZOMBIE));
            getTagBuilder(JTagRegistry.CAN_HAVE_STAND).add(Registries.ENTITY_TYPE.getId(EntityType.ZOMBIE_VILLAGER));
            getTagBuilder(JTagRegistry.CAN_HAVE_STAND).add(Registries.ENTITY_TYPE.getId(EntityType.SKELETON));
            getTagBuilder(JTagRegistry.CAN_HAVE_STAND).add(Registries.ENTITY_TYPE.getId(EntityType.WITHER_SKELETON));
            getTagBuilder(JTagRegistry.CAN_HAVE_STAND).add(Registries.ENTITY_TYPE.getId(EntityType.STRAY));
            getTagBuilder(JTagRegistry.CAN_HAVE_STAND).add(Registries.ENTITY_TYPE.getId(EntityType.PIGLIN));
            getTagBuilder(JTagRegistry.CAN_HAVE_STAND).add(Registries.ENTITY_TYPE.getId(EntityType.ZOMBIFIED_PIGLIN));
            getTagBuilder(JTagRegistry.CAN_HAVE_STAND).add(Registries.ENTITY_TYPE.getId(EntityType.VINDICATOR));
            getTagBuilder(JTagRegistry.CAN_HAVE_STAND).add(Registries.ENTITY_TYPE.getId(EntityType.EVOKER));
            getTagBuilder(JTagRegistry.CAN_HAVE_STAND).add(Registries.ENTITY_TYPE.getId(EntityType.PILLAGER));
            getTagBuilder(JTagRegistry.CAN_HAVE_STAND).add(Registries.ENTITY_TYPE.getId(EntityType.WITCH));
            getTagBuilder(JTagRegistry.CAN_HAVE_STAND).add(Registries.ENTITY_TYPE.getId(EntityType.VILLAGER));
            getTagBuilder(JTagRegistry.CAN_HAVE_STAND).add(Registries.ENTITY_TYPE.getId(EntityType.ENDERMAN));
        }
    }

}