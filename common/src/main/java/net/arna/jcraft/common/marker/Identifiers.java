package net.arna.jcraft.common.marker;

import net.minecraft.resources.ResourceLocation;

public interface Identifiers {

    String MINECRAFT = "minecraft";

    // Entity
    ResourceLocation POSITION = new ResourceLocation(MINECRAFT, "pos");
    ResourceLocation VELOCITY = new ResourceLocation(MINECRAFT, "vel");
    ResourceLocation PITCH = new ResourceLocation(MINECRAFT, "xrot");
    ResourceLocation YAW = new ResourceLocation(MINECRAFT, "yrot");
    ResourceLocation YAW_HEAD = new ResourceLocation(MINECRAFT, "yheadrot");
    ResourceLocation FALL_DISTANCE = new ResourceLocation(MINECRAFT, "fall_distance");
    ResourceLocation FIRE = new ResourceLocation(MINECRAFT, "fire");
    ResourceLocation AIR = new ResourceLocation(MINECRAFT, "air");
    ResourceLocation GROUNDED = new ResourceLocation(MINECRAFT, "grounded");
    ResourceLocation INVULNERABLE = new ResourceLocation(MINECRAFT, "invulnerable");
    ResourceLocation PORTAL_COOLDOWN = new ResourceLocation(MINECRAFT, "portal_cooldown");
    ResourceLocation UUID = new ResourceLocation(MINECRAFT, "uuid");
    ResourceLocation CUSTOM_NAME = new ResourceLocation(MINECRAFT, "custom_name");
    ResourceLocation CUSTOM_NAME_VISIBLE = new ResourceLocation(MINECRAFT, "custom_name_visible");
    ResourceLocation SILENT = new ResourceLocation(MINECRAFT, "silent");
    ResourceLocation NO_GRAVITY = new ResourceLocation(MINECRAFT, "no_gravity");
    ResourceLocation GLOWING = new ResourceLocation(MINECRAFT, "glowing");
    ResourceLocation TICKS_FROZEN = new ResourceLocation(MINECRAFT, "ticks_frozen");
    ResourceLocation VISUAL_FIRE = new ResourceLocation(MINECRAFT, "visual_fire");
    ResourceLocation TAGS = new ResourceLocation(MINECRAFT, "tags");

    // Living Entity
    ResourceLocation HEALTH = new ResourceLocation(MINECRAFT, "health");
    ResourceLocation HURT_TIME = new ResourceLocation(MINECRAFT, "hurt_time");
    ResourceLocation HURT_BY_TIMESTAMP = new ResourceLocation(MINECRAFT, "hurt_by_timestamp");
    ResourceLocation DEATH_TIME = new ResourceLocation(MINECRAFT, "death_time");
    ResourceLocation ABSORPTION_AMOUNT = new ResourceLocation(MINECRAFT, "absorption_amount");
    ResourceLocation ATTRIBUTES = new ResourceLocation(MINECRAFT, "attributes");
    ResourceLocation ACTIVE_EFFECTS = new ResourceLocation(MINECRAFT, "active_effects");
    ResourceLocation FALL_FLYING = new ResourceLocation(MINECRAFT, "active_effects");
    ResourceLocation SLEEPING_POSITION = new ResourceLocation(MINECRAFT, "sleeping_pos");
    ResourceLocation BRAIN = new ResourceLocation(MINECRAFT, "brain");

    // Mobs
    ResourceLocation CAN_PICKUP_LOOT = new ResourceLocation(MINECRAFT, "can_pickup_loot");
    ResourceLocation PERSISTENCE_REQUIRED = new ResourceLocation(MINECRAFT, "can_pickup_loot");
    ResourceLocation ARMOR_ITEMS = new ResourceLocation(MINECRAFT, "armor_items");
    ResourceLocation HAND_ITEMS = new ResourceLocation(MINECRAFT, "hand_items");
    ResourceLocation LEFT_HANDED_MOB = new ResourceLocation(MINECRAFT, "left_handed_mob");
    ResourceLocation NO_AI = new ResourceLocation(MINECRAFT, "no_ai");

    // Ageable Mobs
    ResourceLocation AGE = new ResourceLocation(MINECRAFT, "age");

    // Animals
    ResourceLocation LOVE = new ResourceLocation(MINECRAFT, "love");

}
