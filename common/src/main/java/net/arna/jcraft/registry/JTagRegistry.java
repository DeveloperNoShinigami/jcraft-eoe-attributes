package net.arna.jcraft.registry;

import net.arna.jcraft.JCraft;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

public interface JTagRegistry {

    TagKey<EntityType<?>> CAN_HAVE_STAND = TagKey.of(RegistryKeys.ENTITY_TYPE, JCraft.id("can_have_stand"));
    TagKey<EntityType<?>> CANNOT_BE_STUNNED = TagKey.of(RegistryKeys.ENTITY_TYPE, JCraft.id("cannot_be_stunned"));

    static void init() {

    }

}
