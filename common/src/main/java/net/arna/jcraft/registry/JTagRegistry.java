package net.arna.jcraft.registry;

import net.arna.jcraft.JCraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;

public interface JTagRegistry {

    TagKey<Item> PROTECTS_FROM_SUN = TagKey.create(Registries.ITEM, JCraft.id("protects_from_sun"));
    TagKey<EntityType<?>> CAN_HAVE_STAND = TagKey.create(Registries.ENTITY_TYPE, JCraft.id("can_have_stand"));
    TagKey<EntityType<?>> CANNOT_BE_STUNNED = TagKey.create(Registries.ENTITY_TYPE, JCraft.id("cannot_be_stunned"));

    static void init() {

    }

}
