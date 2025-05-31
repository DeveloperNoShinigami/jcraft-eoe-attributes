package net.arna.jcraft.fabric.datagen;

import net.arna.jcraft.registry.JBlockRegistry;
import net.arna.jcraft.registry.JEntityTypeRegistry;
import net.arna.jcraft.registry.JItemRegistry;
import net.arna.jcraft.registry.JTagRegistry;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagBuilder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
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

            final var sandBlocks = getOrCreateTagBuilder(JTagRegistry.SAND_BLOCKS);
            sandBlocks.forceAddTag(ItemTags.SAND);
            sandBlocks.add(Items.SANDSTONE);
            sandBlocks.add(Items.SANDSTONE_STAIRS);
            sandBlocks.add(Items.SANDSTONE_SLAB);
            sandBlocks.add(Items.SANDSTONE_WALL);
            sandBlocks.add(Items.CHISELED_SANDSTONE);
            sandBlocks.add(Items.SMOOTH_SANDSTONE);
            sandBlocks.add(Items.SMOOTH_SANDSTONE_STAIRS);
            sandBlocks.add(Items.SMOOTH_SANDSTONE_SLAB);
            sandBlocks.add(Items.CUT_SANDSTONE);
            sandBlocks.add(Items.CUT_STANDSTONE_SLAB);
            sandBlocks.add(Items.RED_SANDSTONE);
            sandBlocks.add(Items.RED_SANDSTONE_STAIRS);
            sandBlocks.add(Items.RED_SANDSTONE_SLAB);
            sandBlocks.add(Items.RED_SANDSTONE_WALL);
            sandBlocks.add(Items.CHISELED_RED_SANDSTONE);
            sandBlocks.add(Items.SMOOTH_RED_SANDSTONE);
            sandBlocks.add(Items.SMOOTH_RED_SANDSTONE_STAIRS);
            sandBlocks.add(Items.SMOOTH_RED_SANDSTONE_SLAB);
            sandBlocks.add(Items.CUT_RED_SANDSTONE);
            sandBlocks.add(Items.CUT_RED_SANDSTONE_SLAB);

            final var blindsOnImpact = getOrCreateTagBuilder(JTagRegistry.BLINDS_ON_IMPACT);
            blindsOnImpact.add(Items.PACKED_MUD);
            blindsOnImpact.addTag(JTagRegistry.SAND_BLOCKS);
            blindsOnImpact.add(Items.SEA_LANTERN);
            blindsOnImpact.addOptional(ConventionalItemTags.GLASS_BLOCKS.location());
            blindsOnImpact.addOptional(ConventionalItemTags.GLASS_PANES.location());
            blindsOnImpact.add(Items.GRASS_BLOCK);
            blindsOnImpact.add(Items.PODZOL);
            blindsOnImpact.add(Items.DIRT);
            blindsOnImpact.add(Items.COARSE_DIRT);
            // not rooted dirt, it wouldn't fall apart on impact
            blindsOnImpact.add(Items.MUD);
            blindsOnImpact.add(Items.CLAY);
            blindsOnImpact.add(Items.GRAVEL);
            blindsOnImpact.add(Items.SNOW_BLOCK);
            blindsOnImpact.add(Items.CRIMSON_NYLIUM);
            blindsOnImpact.add(Items.WARPED_NYLIUM);
            blindsOnImpact.add(Items.SOUL_SAND);
            blindsOnImpact.add(Items.SOUL_SOIL);
            blindsOnImpact.add(JItemRegistry.SOUL_BLOCK.getId());
            blindsOnImpact.add(Items.NETHERRACK);
            blindsOnImpact.add(Items.GLOWSTONE);
            blindsOnImpact.add(Items.SHROOMLIGHT);
            blindsOnImpact.add(JItemRegistry.DIO_CAPE.getId());

            final var burnsOnImpact = getOrCreateTagBuilder(JTagRegistry.BURNS_ON_IMPACT);
            burnsOnImpact.add(Items.MAGMA_BLOCK);
            burnsOnImpact.add(Items.CAMPFIRE);
            burnsOnImpact.add(Items.SOUL_CAMPFIRE);
            burnsOnImpact.add(Items.LAVA_BUCKET);
            burnsOnImpact.add(Items.FIRE_CHARGE);

            final var poisonsOnImpact = getOrCreateTagBuilder(JTagRegistry.POISONS_ON_IMPACT);
            poisonsOnImpact.add(Items.PUFFERFISH);
            poisonsOnImpact.add(Items.PUFFERFISH_BUCKET);

            final var explodesOnImpact = getOrCreateTagBuilder(JTagRegistry.EXPLODES_ON_IMPACT);
            explodesOnImpact.add(Items.END_CRYSTAL);
            explodesOnImpact.add(Items.TNT);
            explodesOnImpact.add(Items.TNT_MINECART);

            final var heavyImpact = getOrCreateTagBuilder(JTagRegistry.HEAVY_IMPACT);
            heavyImpact.add(JItemRegistry.METEORITE_BLOCK.getId());
            heavyImpact.add(JItemRegistry.POLISHED_METEORITE_BLOCK.getId());
            heavyImpact.add(Items.IRON_BLOCK);
            heavyImpact.add(JItemRegistry.STELLAR_IRON_BLOCK.getId());
            heavyImpact.add(Items.GOLD_BLOCK);
            heavyImpact.add(Items.DIAMOND_BLOCK);
            heavyImpact.add(Items.NETHERITE_BLOCK);
            heavyImpact.add(Items.RAW_IRON_BLOCK);
            heavyImpact.add(Items.RAW_GOLD_BLOCK);
            heavyImpact.add(Items.BLAST_FURNACE);
            heavyImpact.add(Items.ANVIL);
            heavyImpact.add(Items.CHIPPED_ANVIL);
            heavyImpact.add(Items.DAMAGED_ANVIL);
            heavyImpact.add(Items.OBSIDIAN);
            heavyImpact.add(Items.CRYING_OBSIDIAN);
            heavyImpact.add(Items.RESPAWN_ANCHOR);

            final var brittle = getOrCreateTagBuilder(JTagRegistry.BRITTLE);
            brittle.add(Items.GLASS);
            brittle.add(Items.TINTED_GLASS);
            brittle.add(Items.WHITE_STAINED_GLASS);
            brittle.add(Items.LIGHT_GRAY_STAINED_GLASS);
            brittle.add(Items.GRAY_STAINED_GLASS);
            brittle.add(Items.BLACK_STAINED_GLASS);
            brittle.add(Items.BROWN_STAINED_GLASS);
            brittle.add(Items.RED_STAINED_GLASS);
            brittle.add(Items.ORANGE_STAINED_GLASS);
            brittle.add(Items.YELLOW_STAINED_GLASS);
            brittle.add(Items.LIME_STAINED_GLASS);
            brittle.add(Items.GREEN_STAINED_GLASS);
            brittle.add(Items.CYAN_STAINED_GLASS);
            brittle.add(Items.LIGHT_BLUE_STAINED_GLASS);
            brittle.add(Items.BLUE_STAINED_GLASS);
            brittle.add(Items.PURPLE_STAINED_GLASS);
            brittle.add(Items.MAGENTA_STAINED_GLASS);
            brittle.add(Items.PINK_STAINED_GLASS);
            brittle.add(Items.GLASS_PANE);
            brittle.add(Items.WHITE_STAINED_GLASS_PANE);
            brittle.add(Items.LIGHT_GRAY_STAINED_GLASS_PANE);
            brittle.add(Items.GRAY_STAINED_GLASS_PANE);
            brittle.add(Items.BLACK_STAINED_GLASS_PANE);
            brittle.add(Items.BROWN_STAINED_GLASS_PANE);
            brittle.add(Items.RED_STAINED_GLASS_PANE);
            brittle.add(Items.ORANGE_STAINED_GLASS_PANE);
            brittle.add(Items.YELLOW_STAINED_GLASS_PANE);
            brittle.add(Items.LIME_STAINED_GLASS_PANE);
            brittle.add(Items.GREEN_STAINED_GLASS_PANE);
            brittle.add(Items.CYAN_STAINED_GLASS_PANE);
            brittle.add(Items.LIGHT_BLUE_STAINED_GLASS_PANE);
            brittle.add(Items.BLUE_STAINED_GLASS_PANE);
            brittle.add(Items.PURPLE_STAINED_GLASS_PANE);
            brittle.add(Items.MAGENTA_STAINED_GLASS_PANE);
            brittle.add(Items.PINK_STAINED_GLASS_PANE);
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