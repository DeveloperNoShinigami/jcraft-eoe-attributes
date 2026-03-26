package com.bluelotuscoding.api.attribute;

import com.bluelotuscoding.api.registry.JAttributeRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;

import java.util.HashMap;
import java.util.Map;

public class StandAttributeManager {

    private static final java.util.Set<java.util.UUID> MANAGED_UUIDS = java.util.Set.of(
        JAttributeRegistry.SLOT_1_UUID,
        JAttributeRegistry.SLOT_2_UUID
    );

    /**
     * Resets specifically managed JCraft attributes to their default values 
     * and removes our managed modifiers. This is safe to call on players or stands.
     */
    public static void resetToBase(LivingEntity entity) {
        for (JAttributeRegistry.AttributeEntry e : JAttributeRegistry.ALL) {
            AttributeInstance inst = entity.getAttribute(e.attribute());
            if (inst == null) continue;

            double defaultValue = e.attribute().getDefaultValue();
            if (inst.getBaseValue() != defaultValue) {
                inst.setBaseValue(defaultValue);
            }

            for (java.util.UUID uuid : MANAGED_UUIDS) {
                if (inst.getModifier(uuid) != null) {
                    inst.removeModifier(uuid);
                }
            }
        }
    }

    // =========================================================================
    // Per-Stand Attribute Snapshot System
    // =========================================================================

    /**
     * Captures a snapshot of all JCraft attribute base values from the entity.
     * Returns a CompoundTag with each attribute's NBT key mapped to its current base value.
     */
    public static CompoundTag captureSnapshot(LivingEntity entity) {
        CompoundTag snapshot = new CompoundTag();
        for (JAttributeRegistry.AttributeEntry e : JAttributeRegistry.ALL) {
            AttributeInstance inst = entity.getAttribute(e.attribute());
            if (inst != null) {
                snapshot.putDouble(e.nbtKey(), inst.getBaseValue());
            }
        }
        return snapshot;
    }

    /**
     * Restores attribute base values from a previously captured snapshot.
     * If a key is missing from the snapshot, the attribute is reset to its default.
     */
    public static void restoreSnapshot(LivingEntity entity, CompoundTag snapshot) {
        for (JAttributeRegistry.AttributeEntry e : JAttributeRegistry.ALL) {
            AttributeInstance inst = entity.getAttribute(e.attribute());
            if (inst == null) continue;

            if (snapshot.contains(e.nbtKey())) {
                inst.setBaseValue(snapshot.getDouble(e.nbtKey()));
            } else {
                inst.setBaseValue(e.attribute().getDefaultValue());
            }
        }
    }

    /**
     * Called when a player swaps stands. Saves the old stand's attributes 
     * and loads the new stand's attributes from the per-stand data map.
     *
     * @param entity          The player/user entity
     * @param oldStandId      ResourceLocation of the old stand type (nullable if none)
     * @param newStandId      ResourceLocation of the new stand type (nullable if none)
     * @param perStandData    The persistent map of stand ID -> attribute snapshots
     */
    public static void onStandChange(LivingEntity entity,
                                      ResourceLocation oldStandId,
                                      ResourceLocation newStandId,
                                      Map<String, CompoundTag> perStandData) {
        // 1. Save current attributes under the OLD stand's key
        if (oldStandId != null) {
            CompoundTag snapshot = captureSnapshot(entity);
            perStandData.put(oldStandId.toString(), snapshot);
        }

        // 2. Load attributes for the NEW stand (or reset to defaults)
        if (newStandId != null && perStandData.containsKey(newStandId.toString())) {
            restoreSnapshot(entity, perStandData.get(newStandId.toString()));
        } else {
            resetToBase(entity);
        }
    }
}
