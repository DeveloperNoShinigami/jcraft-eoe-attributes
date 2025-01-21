package net.arna.jcraft.fabric.datagen;

import net.arna.jcraft.registry.JBlockRegistry;
import net.arna.jcraft.registry.JEntityTypeRegistry;
import net.arna.jcraft.registry.JItemRegistry;
import net.arna.jcraft.registry.JTagRegistry;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagBuilder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Blocks;

import java.util.concurrent.CompletableFuture;

public class JTagProviders {
    public static class JBlockTags extends FabricTagProvider.BlockTagProvider {

        public JBlockTags(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
            super(output, registriesFuture);
        }

        @Override
        protected void addTags(HolderLookup.Provider arg) {
            getOrCreateRawBuilder(BlockTags.MINEABLE_WITH_PICKAXE).addElement(JBlockRegistry.METEORITE_BLOCK.getId());
            getOrCreateRawBuilder(BlockTags.MINEABLE_WITH_PICKAXE).addElement(JBlockRegistry.POLISHED_METEORITE_BLOCK.getId());
            getOrCreateRawBuilder(BlockTags.MINEABLE_WITH_PICKAXE).addElement(JBlockRegistry.METEORITE_IRON_ORE_BLOCK.getId());
            getOrCreateRawBuilder(BlockTags.MINEABLE_WITH_PICKAXE).addElement(JBlockRegistry.STELLAR_IRON_BLOCK.getId());
            getOrCreateRawBuilder(BlockTags.MINEABLE_WITH_PICKAXE).addElement(JBlockRegistry.CINDERELLA_GREEN_BLOCK.getId());
            getOrCreateRawBuilder(BlockTags.NEEDS_DIAMOND_TOOL).addElement(JBlockRegistry.METEORITE_BLOCK.getId());
            getOrCreateRawBuilder(BlockTags.NEEDS_DIAMOND_TOOL).addElement(JBlockRegistry.POLISHED_METEORITE_BLOCK.getId());
            getOrCreateRawBuilder(BlockTags.NEEDS_DIAMOND_TOOL).addElement(JBlockRegistry.METEORITE_IRON_ORE_BLOCK.getId());
            getOrCreateRawBuilder(BlockTags.NEEDS_IRON_TOOL).addElement(JBlockRegistry.STELLAR_IRON_BLOCK.getId());
            getOrCreateRawBuilder(BlockTags.MINEABLE_WITH_SHOVEL).addElement(JBlockRegistry.HOT_SAND_BLOCK.getId());
            getOrCreateRawBuilder(BlockTags.MINEABLE_WITH_AXE).addElement(JBlockRegistry.SOUL_WOOD_BLOCK.getId());

            getOrCreateRawBuilder(BlockTags.LOGS_THAT_BURN).addTag(JTagRegistry.SOUL_LOG_BLOCKS.location());
            getOrCreateRawBuilder(BlockTags.SOUL_SPEED_BLOCKS).addElement(JBlockRegistry.SOUL_BLOCK.getId());
            getOrCreateRawBuilder(BlockTags.SOUL_SPEED_BLOCKS).addElement(JBlockRegistry.SOUL_WOOD_BLOCK.getId());
            getOrCreateRawBuilder(BlockTags.SOUL_FIRE_BASE_BLOCKS).addElement(JBlockRegistry.SOUL_BLOCK.getId());
            getOrCreateRawBuilder(BlockTags.SOUL_FIRE_BASE_BLOCKS).addElement(JBlockRegistry.SOUL_WOOD_BLOCK.getId());
            getOrCreateRawBuilder(BlockTags.BEACON_BASE_BLOCKS).addElement(JBlockRegistry.STELLAR_IRON_BLOCK.getId());
            // we do not want bamboo on hot sand, hence we do not add hot sand to the sand tag
            getOrCreateRawBuilder(BlockTags.SNOW_LAYER_CANNOT_SURVIVE_ON).addElement(JBlockRegistry.HOT_SAND_BLOCK.getId());

            getOrCreateRawBuilder(JTagRegistry.SOUL_LOG_BLOCKS).addElement(JBlockRegistry.SOUL_WOOD_BLOCK.getId());

            getOrCreateRawBuilder(JTagRegistry.IRON_BLOCKS).addElement(BuiltInRegistries.BLOCK.getKey(Blocks.IRON_BLOCK));
            getOrCreateRawBuilder(JTagRegistry.IRON_BLOCKS).addElement(BuiltInRegistries.BLOCK.getKey(Blocks.IRON_BARS));
            getOrCreateRawBuilder(JTagRegistry.IRON_BLOCKS).addElement(BuiltInRegistries.BLOCK.getKey(Blocks.IRON_DOOR));
            getOrCreateRawBuilder(JTagRegistry.IRON_BLOCKS).addElement(BuiltInRegistries.BLOCK.getKey(Blocks.IRON_TRAPDOOR));
            getOrCreateRawBuilder(JTagRegistry.IRON_BLOCKS).addElement(BuiltInRegistries.BLOCK.getKey(Blocks.IRON_ORE));
            getOrCreateRawBuilder(JTagRegistry.IRON_BLOCKS).addElement(BuiltInRegistries.BLOCK.getKey(Blocks.DEEPSLATE_IRON_ORE));
            getOrCreateRawBuilder(JTagRegistry.IRON_BLOCKS).addElement(BuiltInRegistries.BLOCK.getKey(Blocks.RAW_IRON_BLOCK));
            getOrCreateRawBuilder(JTagRegistry.IRON_BLOCKS).addElement(BuiltInRegistries.BLOCK.getKey(Blocks.ANVIL));
            getOrCreateRawBuilder(JTagRegistry.IRON_BLOCKS).addElement(BuiltInRegistries.BLOCK.getKey(Blocks.CHIPPED_ANVIL));
            getOrCreateRawBuilder(JTagRegistry.IRON_BLOCKS).addElement(BuiltInRegistries.BLOCK.getKey(Blocks.DAMAGED_ANVIL));
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
            getOrCreateRawBuilder(ItemTags.SOUL_FIRE_BASE_BLOCKS).addElement(JItemRegistry.SOUL_BLOCK.getId());
            getOrCreateRawBuilder(ItemTags.ARROWS).addElement(JItemRegistry.STAND_ARROW.getId());

            getOrCreateRawBuilder(JTagRegistry.SOUL_LOG_ITEMS).addElement(JItemRegistry.SOUL_WOOD_BLOCK.getId());
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
            getOrCreateRawBuilder(EntityTypeTags.ARROWS).addElement(JEntityTypeRegistry.STAND_ARROW_PROJECTILE.getId());

            // possible mob stand users
            TagBuilder canHaveStandBuilder = getOrCreateRawBuilder(JTagRegistry.CAN_HAVE_STAND);

            canHaveStandBuilder.addElement(BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.VILLAGER));
            canHaveStandBuilder.addElement(BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.WANDERING_TRADER));

            canHaveStandBuilder.addElement(BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.ENDERMAN));

            canHaveStandBuilder.addElement(BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.PIGLIN));
            canHaveStandBuilder.addElement(BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.PIGLIN_BRUTE));
            canHaveStandBuilder.addElement(BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.ZOMBIFIED_PIGLIN));

            canHaveStandBuilder.addElement(BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.ZOMBIE));
            canHaveStandBuilder.addElement(BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.ZOMBIE_VILLAGER));
            canHaveStandBuilder.addElement(BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.HUSK));

            canHaveStandBuilder.addElement(BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.SKELETON));
            canHaveStandBuilder.addElement(BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.WITHER_SKELETON));
            canHaveStandBuilder.addElement(BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.STRAY));

            canHaveStandBuilder.addElement(BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.VINDICATOR));
            canHaveStandBuilder.addElement(BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.EVOKER));
            canHaveStandBuilder.addElement(BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.PILLAGER));
            canHaveStandBuilder.addElement(BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.WITCH));

            // ferromagnetic entities
            TagBuilder ferrousEntitiesBuilder = getOrCreateRawBuilder(JTagRegistry.FERROUS_ENTITIES);

            ferrousEntitiesBuilder.addElement(BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.IRON_GOLEM));
            ferrousEntitiesBuilder.addElement(BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.FISHING_BOBBER));

            ferrousEntitiesBuilder.addElement(BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.MINECART));
            ferrousEntitiesBuilder.addElement(BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.CHEST_MINECART));
            ferrousEntitiesBuilder.addElement(BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.FURNACE_MINECART));
            ferrousEntitiesBuilder.addElement(BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.HOPPER_MINECART));
            ferrousEntitiesBuilder.addElement(BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.COMMAND_BLOCK_MINECART));
            ferrousEntitiesBuilder.addElement(BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.TNT_MINECART));
            ferrousEntitiesBuilder.addElement(BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.SPAWNER_MINECART));

            ferrousEntitiesBuilder.addElement(BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.TRIDENT));

            ferrousEntitiesBuilder.addElement(BuiltInRegistries.ENTITY_TYPE.getKey(JEntityTypeRegistry.KNIFE.get()));
            ferrousEntitiesBuilder.addElement(BuiltInRegistries.ENTITY_TYPE.getKey(JEntityTypeRegistry.SCALPEL.get()));
            ferrousEntitiesBuilder.addElement(BuiltInRegistries.ENTITY_TYPE.getKey(JEntityTypeRegistry.BISECT.get()));

            ferrousEntitiesBuilder.addElement(BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.ARROW));
            ferrousEntitiesBuilder.addElement(BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.SPECTRAL_ARROW));

            ferrousEntitiesBuilder.addElement(BuiltInRegistries.ENTITY_TYPE.getKey(JEntityTypeRegistry.STAND_ARROW_PROJECTILE.get()));

            ferrousEntitiesBuilder.addElement(BuiltInRegistries.ENTITY_TYPE.getKey(JEntityTypeRegistry.RAZOR.get()));

            // impossible to stun
            getOrCreateRawBuilder(JTagRegistry.CANNOT_BE_STUNNED).addElement(BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.WARDEN));
        }
    }

}