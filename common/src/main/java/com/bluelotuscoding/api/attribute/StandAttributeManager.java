package com.bluelotuscoding.api.attribute;

import com.bluelotuscoding.api.config.JAttributeConfig;
import com.bluelotuscoding.api.registry.JAttributeRegistry;
import net.arna.jcraft.api.component.living.CommonStandComponent;
import net.arna.jcraft.api.stand.StandType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.UUID;

public class StandAttributeManager {

    // Apply config-default values as ADDITION modifiers rather than overwriting base values.
    // This ensures our defaults are additive to any other modifiers already on the attribute
    // (e.g., from other mods or from player command-added modifiers via SLOT_1/SLOT_2 UUIDs).
    // Each attribute has a fixed UUID (in JAttributeConfig) so we can safely remove + re-add
    // the default modifier without duplicating it.
    //
    // The modifier value stored here is the DELTA from the attribute's registry default base:
    //   - Flat bonus attributes (base=0.0): modifierValue = configDefault
    //   - Multiplier attributes (base=1.0): modifierValue = configDefault (as delta from base)
    //     e.g. default 0.0 delta → getValue() = 1.0 + 0.0 = 1.0 (neutral multiplier)
    //   - ALPHA_OVERRIDE (base=-1.0): modifierValue = configDefault (as delta from base -1.0)
    //     e.g. default 0.0 delta → getValue() = -1.0 + 0.0 = -1.0 (disabled state)
    public static void updateAttributes(LivingEntity entity) {
        if (entity.level().isClientSide) return;

        // Combat Stats
        applyDefault(entity, JAttributeRegistry.STAND_DAMAGE,        JAttributeConfig.STAND_DAMAGE_UUID,        JAttributeConfig.defaultStandDamage);
        applyDefault(entity, JAttributeRegistry.STAND_RESISTANCE,    JAttributeConfig.STAND_RESISTANCE_UUID,    JAttributeConfig.defaultStandResistance);
        applyDefault(entity, JAttributeRegistry.STAND_GAUGE_MAX,     JAttributeConfig.STAND_GAUGE_MAX_UUID,     JAttributeConfig.defaultStandGaugeMax);

        // Global Stand Stats
        applyDefault(entity, JAttributeRegistry.IDLE_DISTANCE,       JAttributeConfig.IDLE_DISTANCE_UUID,       JAttributeConfig.defaultIdleDistance);
        applyDefault(entity, JAttributeRegistry.IDLE_ROTATION,       JAttributeConfig.IDLE_ROTATION_UUID,       JAttributeConfig.defaultIdleRotation);
        applyDefault(entity, JAttributeRegistry.BLOCK_DISTANCE,      JAttributeConfig.BLOCK_DISTANCE_UUID,      JAttributeConfig.defaultBlockDistance);
        applyDefault(entity, JAttributeRegistry.ENGAGEMENT_DISTANCE, JAttributeConfig.ENGAGEMENT_DISTANCE_UUID, JAttributeConfig.defaultEngagementDistance);
        applyDefault(entity, JAttributeRegistry.ALPHA_OVERRIDE,      JAttributeConfig.ALPHA_OVERRIDE_UUID,      JAttributeConfig.defaultAlphaOverride);

        // Core Move Stats
        applyDefault(entity, JAttributeRegistry.COOLDOWN_REDUCTION,        JAttributeConfig.COOLDOWN_REDUCTION_UUID,     JAttributeConfig.defaultCooldownReduction);
        applyDefault(entity, JAttributeRegistry.WINDUP_REDUCTION,          JAttributeConfig.WINDUP_REDUCTION_UUID,       JAttributeConfig.defaultWindupReduction);
        applyDefault(entity, JAttributeRegistry.DURATION_MULTIPLIER,       JAttributeConfig.DURATION_MULTIPLIER_UUID,    JAttributeConfig.defaultDurationMultiplier);
        applyDefault(entity, JAttributeRegistry.MOVE_DISTANCE_MULTIPLIER,  JAttributeConfig.MOVE_DIST_MULT_UUID,         JAttributeConfig.defaultMoveDistanceMultiplier);
        applyDefault(entity, JAttributeRegistry.ARMOR_BONUS,               JAttributeConfig.ARMOR_BONUS_UUID,            JAttributeConfig.defaultArmorBonus);
        applyDefault(entity, JAttributeRegistry.CHARGE_DISTANCE_MULTIPLIER,JAttributeConfig.CHARGE_DIST_MULT_UUID,       JAttributeConfig.defaultChargeDistanceMultiplier);
        applyDefault(entity, JAttributeRegistry.LIFE_STEAL,                JAttributeConfig.LIFE_STEAL_UUID,             JAttributeConfig.defaultLifeSteal);
        applyDefault(entity, JAttributeRegistry.KNOCKBACK_MODIFIER,        JAttributeConfig.KNOCKBACK_MODIFIER_UUID,     JAttributeConfig.defaultKnockbackModifier);
        applyDefault(entity, JAttributeRegistry.BLOCK_STUN_REDUCTION,      JAttributeConfig.BLOCK_STUN_REDUCTION_UUID,   JAttributeConfig.defaultBlockStunReduction);
        applyDefault(entity, JAttributeRegistry.ATTACK_RANGE_BONUS,        JAttributeConfig.ATTACK_RANGE_BONUS_UUID,     JAttributeConfig.defaultAttackRangeBonus);

        // Specialized Ability Stats
        applyDefault(entity, JAttributeRegistry.TIME_STOP_DURATION, JAttributeConfig.TIME_STOP_DURATION_UUID, JAttributeConfig.defaultTimeStopDuration);
        applyDefault(entity, JAttributeRegistry.ACCEL_DURATION,     JAttributeConfig.ACCEL_DURATION_UUID,     JAttributeConfig.defaultAccelDuration);
        applyDefault(entity, JAttributeRegistry.ERASURE_DURATION,   JAttributeConfig.ERASURE_DURATION_UUID,   JAttributeConfig.defaultErasureDuration);
        applyDefault(entity, JAttributeRegistry.REWIND_REACH,       JAttributeConfig.REWIND_REACH_UUID,       JAttributeConfig.defaultRewindReach);
        applyDefault(entity, JAttributeRegistry.ERASURE_REACH,      JAttributeConfig.ERASURE_REACH_UUID,      JAttributeConfig.defaultErasureReach);
    }

    // Removes any existing per-stand default modifier and re-applies it with the current config value.
    // If the delta is 0.0, no modifier is added (the attribute's registry base provides neutral behavior).
    // Uses addPermanentModifier so the modifier is included in instance.save() → persists per-stand in NBT.
    private static void applyDefault(LivingEntity entity, Attribute attribute, UUID modifierId, double delta) {
        AttributeInstance instance = entity.getAttribute(attribute);
        if (instance == null) return;
        instance.removeModifier(modifierId);
        if (delta != 0.0) {
            instance.addPermanentModifier(new AttributeModifier(
                modifierId,
                "jcraft_attributes:default",
                delta,
                AttributeModifier.Operation.ADDITION
            ));
        }
    }

    public static void saveAttributes(LivingEntity entity, CommonStandComponent component) {
        if (entity.level().isClientSide || !(component instanceof JAttributesComponent jAttrs)) return;

        StandType currentType = component.getType();
        if (currentType == null) return;

        String standId = currentType.getId().toString();
        CompoundTag data = new CompoundTag();

        saveAttribute(entity, JAttributeRegistry.STAND_DAMAGE, "StandDamage", data);
        saveAttribute(entity, JAttributeRegistry.STAND_RESISTANCE, "StandResistance", data);
        saveAttribute(entity, JAttributeRegistry.STAND_GAUGE_MAX, "StandGaugeMax", data);

        // Global Stand Stats
        saveAttribute(entity, JAttributeRegistry.IDLE_DISTANCE, "IdleDistance", data);
        saveAttribute(entity, JAttributeRegistry.IDLE_ROTATION, "IdleRotation", data);
        saveAttribute(entity, JAttributeRegistry.BLOCK_DISTANCE, "BlockDistance", data);
        saveAttribute(entity, JAttributeRegistry.ENGAGEMENT_DISTANCE, "EngagementDistance", data);
        saveAttribute(entity, JAttributeRegistry.ALPHA_OVERRIDE, "AlphaOverride", data);

        // Core Move Stats
        saveAttribute(entity, JAttributeRegistry.COOLDOWN_REDUCTION, "CooldownReduction", data);
        saveAttribute(entity, JAttributeRegistry.WINDUP_REDUCTION, "WindupReduction", data);
        saveAttribute(entity, JAttributeRegistry.DURATION_MULTIPLIER, "DurationMultiplier", data);
        saveAttribute(entity, JAttributeRegistry.MOVE_DISTANCE_MULTIPLIER, "MoveDistanceMultiplier", data);
        saveAttribute(entity, JAttributeRegistry.ARMOR_BONUS, "ArmorBonus", data);
        saveAttribute(entity, JAttributeRegistry.CHARGE_DISTANCE_MULTIPLIER, "ChargeDistanceMultiplier", data);
        saveAttribute(entity, JAttributeRegistry.LIFE_STEAL, "LifeSteal", data);
        saveAttribute(entity, JAttributeRegistry.KNOCKBACK_MODIFIER, "KnockbackModifier", data);
        saveAttribute(entity, JAttributeRegistry.BLOCK_STUN_REDUCTION, "BlockStunReduction", data);
        saveAttribute(entity, JAttributeRegistry.ATTACK_RANGE_BONUS, "AttackRangeBonus", data);

        // Specialized Ability Stats
        saveAttribute(entity, JAttributeRegistry.TIME_STOP_DURATION, "TimeStopDuration", data);
        saveAttribute(entity, JAttributeRegistry.ACCEL_DURATION, "AccelDuration", data);
        saveAttribute(entity, JAttributeRegistry.ERASURE_DURATION, "ErasureDuration", data);
        saveAttribute(entity, JAttributeRegistry.REWIND_REACH, "RewindReach", data);
        saveAttribute(entity, JAttributeRegistry.ERASURE_REACH, "ErasureReach", data);

        jAttrs.getStandAttributeMap().put(standId, data);
    }

    public static void loadAttributes(LivingEntity entity, CommonStandComponent component) {
        if (entity.level().isClientSide || !(component instanceof JAttributesComponent jAttrs)) return;

        StandType newType = component.getType();
        if (newType == null) {
            updateAttributes(entity);
            return;
        }

        String standId = newType.getId().toString();
        CompoundTag data = jAttrs.getStandAttributeMap().get(standId);

        if (data != null) {
            // Restore the full attribute state for this stand (base + all modifiers including
            // the per-stand default modifier and any command-added SLOT_1/SLOT_2 modifiers).
            loadAttribute(entity, JAttributeRegistry.STAND_DAMAGE, "StandDamage", data);
            loadAttribute(entity, JAttributeRegistry.STAND_RESISTANCE, "StandResistance", data);
            loadAttribute(entity, JAttributeRegistry.STAND_GAUGE_MAX, "StandGaugeMax", data);

            // Global Stand Stats
            loadAttribute(entity, JAttributeRegistry.IDLE_DISTANCE, "IdleDistance", data);
            loadAttribute(entity, JAttributeRegistry.IDLE_ROTATION, "IdleRotation", data);
            loadAttribute(entity, JAttributeRegistry.BLOCK_DISTANCE, "BlockDistance", data);
            loadAttribute(entity, JAttributeRegistry.ENGAGEMENT_DISTANCE, "EngagementDistance", data);
            loadAttribute(entity, JAttributeRegistry.ALPHA_OVERRIDE, "AlphaOverride", data);

            // Core Move Stats
            loadAttribute(entity, JAttributeRegistry.COOLDOWN_REDUCTION, "CooldownReduction", data);
            loadAttribute(entity, JAttributeRegistry.WINDUP_REDUCTION, "WindupReduction", data);
            loadAttribute(entity, JAttributeRegistry.DURATION_MULTIPLIER, "DurationMultiplier", data);
            loadAttribute(entity, JAttributeRegistry.MOVE_DISTANCE_MULTIPLIER, "MoveDistanceMultiplier", data);
            loadAttribute(entity, JAttributeRegistry.ARMOR_BONUS, "ArmorBonus", data);
            loadAttribute(entity, JAttributeRegistry.CHARGE_DISTANCE_MULTIPLIER, "ChargeDistanceMultiplier", data);
            loadAttribute(entity, JAttributeRegistry.LIFE_STEAL, "LifeSteal", data);
            loadAttribute(entity, JAttributeRegistry.KNOCKBACK_MODIFIER, "KnockbackModifier", data);
            loadAttribute(entity, JAttributeRegistry.BLOCK_STUN_REDUCTION, "BlockStunReduction", data);
            loadAttribute(entity, JAttributeRegistry.ATTACK_RANGE_BONUS, "AttackRangeBonus", data);

            // Specialized Ability Stats
            loadAttribute(entity, JAttributeRegistry.TIME_STOP_DURATION, "TimeStopDuration", data);
            loadAttribute(entity, JAttributeRegistry.ACCEL_DURATION, "AccelDuration", data);
            loadAttribute(entity, JAttributeRegistry.ERASURE_DURATION, "ErasureDuration", data);
            loadAttribute(entity, JAttributeRegistry.REWIND_REACH, "RewindReach", data);
            loadAttribute(entity, JAttributeRegistry.ERASURE_REACH, "ErasureReach", data);
        } else {
            // First time equipping this stand — apply config defaults as starting point
            updateAttributes(entity);
        }
    }

    // Saves the full attribute state (base value + all modifiers) to NBT.
    // This captures both the config-default modifier and any command-added modifiers (SLOT_1, SLOT_2).
    private static void saveAttribute(LivingEntity entity, Attribute attribute, String key, CompoundTag tag) {
        AttributeInstance instance = entity.getAttribute(attribute);
        if (instance != null) {
            tag.put(key, instance.save());
        }
    }

    // Restores the full attribute state from NBT (base + all modifiers).
    // instance.load() clears existing modifiers first, then re-adds from NBT — no stacking issues.
    private static void loadAttribute(LivingEntity entity, Attribute attribute, String key, CompoundTag tag) {
        AttributeInstance instance = entity.getAttribute(attribute);
        if (instance != null && tag.contains(key, Tag.TAG_COMPOUND)) {
            instance.load(tag.getCompound(key));
        }
    }

    public static void onStandChange(LivingEntity entity) {
        // Handled by CommonStandComponentImplMixin calling saveAttributes/loadAttributes directly
    }
}
