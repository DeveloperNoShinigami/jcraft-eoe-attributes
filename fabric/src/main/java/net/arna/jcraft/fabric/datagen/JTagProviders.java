package net.arna.jcraft.fabric.datagen;

import net.arna.jcraft.registry.JBlockRegistry;
import net.arna.jcraft.registry.JItemRegistry;
import net.arna.jcraft.registry.JTagRegistry;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EntityType;
import java.util.concurrent.CompletableFuture;

public class JTagProviders {
    public static class JBlockTags extends FabricTagProvider.BlockTagProvider {

        public JBlockTags(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
            super(output, registriesFuture);
        }

        @Override
        protected void addTags(HolderLookup.Provider arg) {
            getOrCreateRawBuilder(BlockTags.MINEABLE_WITH_PICKAXE).addElement(JBlockRegistry.METEORITE_BLOCK.getId());
            getOrCreateRawBuilder(BlockTags.MINEABLE_WITH_PICKAXE).addElement(JBlockRegistry.METEORITE_IRON_ORE_BLOCK.getId());
            getOrCreateRawBuilder(BlockTags.MINEABLE_WITH_PICKAXE).addElement(JBlockRegistry.STELLAR_IRON_BLOCK.getId());
            getOrCreateRawBuilder(BlockTags.MINEABLE_WITH_PICKAXE).addElement(JBlockRegistry.CINDERELLA_GREEN_BLOCK.getId());
            getOrCreateRawBuilder(BlockTags.NEEDS_DIAMOND_TOOL).addElement(JBlockRegistry.METEORITE_BLOCK.getId());
            getOrCreateRawBuilder(BlockTags.NEEDS_DIAMOND_TOOL).addElement(JBlockRegistry.METEORITE_IRON_ORE_BLOCK.getId());
            getOrCreateRawBuilder(BlockTags.NEEDS_IRON_TOOL).addElement(JBlockRegistry.STELLAR_IRON_BLOCK.getId());
            getOrCreateRawBuilder(BlockTags.MINEABLE_WITH_SHOVEL).addElement(JBlockRegistry.HOT_SAND_BLOCK.getId());

            getOrCreateRawBuilder(BlockTags.SOUL_SPEED_BLOCKS).addElement(JBlockRegistry.SOUL_BLOCK.getId());
            getOrCreateRawBuilder(BlockTags.SOUL_FIRE_BASE_BLOCKS).addElement(JBlockRegistry.SOUL_BLOCK.getId());
            getOrCreateRawBuilder(BlockTags.BEACON_BASE_BLOCKS).addElement(JBlockRegistry.STELLAR_IRON_BLOCK.getId());
            // we do not want bamboo on hot sand, hence we do not add hot sand to the sand tag
            getOrCreateRawBuilder(BlockTags.SNOW_LAYER_CANNOT_SURVIVE_ON).addElement(JBlockRegistry.HOT_SAND_BLOCK.getId());
        }
    }

    public static class JItemTags extends FabricTagProvider.ItemTagProvider {

        public JItemTags(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
            super(output, registriesFuture);
        }

        @Override
        protected void addTags(HolderLookup.Provider arg) {
            getOrCreateRawBuilder(ItemTags.TRIM_MATERIALS).addElement(JItemRegistry.STELLAR_IRON_INGOT.getId());

            getOrCreateRawBuilder(ItemTags.SAND).addElement(JItemRegistry.HOT_SAND_BLOCK.getId());
            getOrCreateRawBuilder(ItemTags.SMELTS_TO_GLASS).addElement(JItemRegistry.HOT_SAND_BLOCK.getId());

            getOrCreateRawBuilder(ItemTags.BEACON_PAYMENT_ITEMS).addElement(JItemRegistry.STELLAR_IRON_INGOT.getId());
            getOrCreateRawBuilder(ItemTags.BOOKSHELF_BOOKS).addElement(JItemRegistry.DIOS_DIARY.getId());

            getOrCreateRawBuilder(JTagRegistry.PROTECTS_FROM_SUN).addElement(JItemRegistry.KARS_HEADWRAP.getId());
            getOrCreateRawBuilder(JTagRegistry.PROTECTS_FROM_SUN).addElement(JItemRegistry.RED_HAT.getId());
            getOrCreateRawBuilder(JTagRegistry.PROTECTS_FROM_SUN).addElement(JItemRegistry.PUCCIS_HAT.getId());
        }
    }

    public static class JEntityTypeTags extends FabricTagProvider.EntityTypeTagProvider {

        public JEntityTypeTags(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
            super(output, registriesFuture);
        }

        @Override
        protected void addTags(HolderLookup.Provider arg) {
            // possible mob stand users
            getOrCreateRawBuilder(JTagRegistry.CAN_HAVE_STAND).addElement(BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.ZOMBIE));
            getOrCreateRawBuilder(JTagRegistry.CAN_HAVE_STAND).addElement(BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.ZOMBIE_VILLAGER));
            getOrCreateRawBuilder(JTagRegistry.CAN_HAVE_STAND).addElement(BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.SKELETON));
            getOrCreateRawBuilder(JTagRegistry.CAN_HAVE_STAND).addElement(BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.WITHER_SKELETON));
            getOrCreateRawBuilder(JTagRegistry.CAN_HAVE_STAND).addElement(BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.STRAY));
            getOrCreateRawBuilder(JTagRegistry.CAN_HAVE_STAND).addElement(BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.PIGLIN));
            getOrCreateRawBuilder(JTagRegistry.CAN_HAVE_STAND).addElement(BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.ZOMBIFIED_PIGLIN));
            getOrCreateRawBuilder(JTagRegistry.CAN_HAVE_STAND).addElement(BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.VINDICATOR));
            getOrCreateRawBuilder(JTagRegistry.CAN_HAVE_STAND).addElement(BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.EVOKER));
            getOrCreateRawBuilder(JTagRegistry.CAN_HAVE_STAND).addElement(BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.PILLAGER));
            getOrCreateRawBuilder(JTagRegistry.CAN_HAVE_STAND).addElement(BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.WITCH));
            getOrCreateRawBuilder(JTagRegistry.CAN_HAVE_STAND).addElement(BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.VILLAGER));
            getOrCreateRawBuilder(JTagRegistry.CAN_HAVE_STAND).addElement(BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.ENDERMAN));
            // impossible to stun
            getOrCreateRawBuilder(JTagRegistry.CANNOT_BE_STUNNED).addElement(BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.WARDEN));
        }
    }

}