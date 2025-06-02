package net.arna.jcraft.registry;

import net.arna.jcraft.JCraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;

public interface JTagRegistry {

    TagKey<Item> EQUIPABLES = TagKey.create(Registries.ITEM, new ResourceLocation("c", "equipables"));
    TagKey<Item> PROTECTS_FROM_SUN = TagKey.create(Registries.ITEM, JCraft.id("protects_from_sun"));
    TagKey<Item> SAND_BLOCKS = TagKey.create(Registries.ITEM, new ResourceLocation("c", "sand_blocks"));
    TagKey<Item> BLINDS_ON_IMPACT = TagKey.create(Registries.ITEM, JCraft.id("blinds_on_impact"));
    TagKey<Item> BURNS_ON_IMPACT = TagKey.create(Registries.ITEM, JCraft.id("burns_on_impact"));
    TagKey<Item> POISONS_ON_IMPACT = TagKey.create(Registries.ITEM, JCraft.id("poisons_on_impact"));
    TagKey<Item> EXPLODES_ON_IMPACT = TagKey.create(Registries.ITEM, JCraft.id("explodes_on_impact"));
    TagKey<Item> HEAVY_IMPACT = TagKey.create(Registries.ITEM, JCraft.id("heavy_impact"));
    TagKey<Item> BRITTLE = TagKey.create(Registries.ITEM, JCraft.id("brittle"));
    TagKey<Item> SOUL_LOG_ITEMS = TagKey.create(Registries.ITEM, JCraft.id("soul_logs"));

    TagKey<Block> SOUL_LOG_BLOCKS = TagKey.create(Registries.BLOCK, JCraft.id("soul_logs"));
    TagKey<Block> IRON_BLOCKS = TagKey.create(Registries.BLOCK, JCraft.id("iron_blocks"));

    TagKey<EntityType<?>> FERROUS_ENTITIES = TagKey.create(Registries.ENTITY_TYPE, JCraft.id("ferrous_entities"));
    TagKey<EntityType<?>> CAN_HAVE_STAND = TagKey.create(Registries.ENTITY_TYPE, JCraft.id("can_have_stand"));
    TagKey<EntityType<?>> CANNOT_BE_STUNNED = TagKey.create(Registries.ENTITY_TYPE, JCraft.id("cannot_be_stunned"));

    TagKey<Biome> METEORS_CAN_FALL = TagKey.create(Registries.BIOME, JCraft.id("meteors_can_fall"));

    static void init() {
        // intentionally left empty
    }
}
