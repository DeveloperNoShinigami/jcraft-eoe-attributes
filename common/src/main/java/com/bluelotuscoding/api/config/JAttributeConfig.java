package com.bluelotuscoding.api.config;

import com.bluelotuscoding.JCraftAttributes;
import net.fabricmc.loader.api.FabricLoader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.UUID;

public class JAttributeConfig {
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("jcraft_attributes.toml");

    // -------------------------------------------------------------------------
    // PER-ATTRIBUTE MODIFIER UUIDs
    // One stable, hardcoded UUID per attribute. Used by StandAttributeManager to
    // apply the config-default value as an AttributeModifier (ADDITION operation)
    // instead of overwriting the attribute's base value. This means the default
    // stacks additively with any command-added modifiers (SLOT_1, SLOT_2 from
    // JAttributeRegistry) rather than replacing them.
    // -------------------------------------------------------------------------

    public static final UUID STAND_DAMAGE_UUID           = UUID.fromString("a1000001-cafe-babe-dead-000000000001");
    public static final UUID STAND_RESISTANCE_UUID       = UUID.fromString("a1000002-cafe-babe-dead-000000000002");
    public static final UUID STAND_GAUGE_MAX_UUID        = UUID.fromString("a1000003-cafe-babe-dead-000000000003");
    public static final UUID IDLE_DISTANCE_UUID          = UUID.fromString("a1000004-cafe-babe-dead-000000000004");
    public static final UUID IDLE_ROTATION_UUID          = UUID.fromString("a1000005-cafe-babe-dead-000000000005");
    public static final UUID BLOCK_DISTANCE_UUID         = UUID.fromString("a1000006-cafe-babe-dead-000000000006");
    public static final UUID ENGAGEMENT_DISTANCE_UUID    = UUID.fromString("a1000007-cafe-babe-dead-000000000007");
    public static final UUID ALPHA_OVERRIDE_UUID         = UUID.fromString("a1000008-cafe-babe-dead-000000000008");
    public static final UUID COOLDOWN_REDUCTION_UUID     = UUID.fromString("a1000009-cafe-babe-dead-000000000009");
    public static final UUID WINDUP_REDUCTION_UUID       = UUID.fromString("a100000a-cafe-babe-dead-00000000000a");
    public static final UUID DURATION_MULTIPLIER_UUID    = UUID.fromString("a100000b-cafe-babe-dead-00000000000b");
    public static final UUID MOVE_DIST_MULT_UUID         = UUID.fromString("a100000c-cafe-babe-dead-00000000000c");
    public static final UUID ARMOR_BONUS_UUID            = UUID.fromString("a100000d-cafe-babe-dead-00000000000d");
    public static final UUID CHARGE_DIST_MULT_UUID       = UUID.fromString("a100000e-cafe-babe-dead-00000000000e");
    public static final UUID LIFE_STEAL_UUID             = UUID.fromString("a100000f-cafe-babe-dead-00000000000f");
    public static final UUID KNOCKBACK_MODIFIER_UUID     = UUID.fromString("a1000010-cafe-babe-dead-000000000010");
    public static final UUID BLOCK_STUN_REDUCTION_UUID   = UUID.fromString("a1000011-cafe-babe-dead-000000000011");
    public static final UUID ATTACK_RANGE_BONUS_UUID     = UUID.fromString("a1000012-cafe-babe-dead-000000000012");
    public static final UUID TIME_STOP_DURATION_UUID     = UUID.fromString("a1000013-cafe-babe-dead-000000000013");
    public static final UUID ACCEL_DURATION_UUID         = UUID.fromString("a1000014-cafe-babe-dead-000000000014");
    public static final UUID ERASURE_DURATION_UUID       = UUID.fromString("a1000015-cafe-babe-dead-000000000015");
    public static final UUID REWIND_REACH_UUID           = UUID.fromString("a1000016-cafe-babe-dead-000000000016");
    public static final UUID ERASURE_REACH_UUID          = UUID.fromString("a1000017-cafe-babe-dead-000000000017");
    public static final UUID KNOCKBACK_BONUS_UUID        = UUID.fromString("a1000018-cafe-babe-dead-000000000018");

    // -------------------------------------------------------------------------
    // CONFIG DEFAULTS — applied as the initial ADDITION modifier value on spawn
    // or first stand equip. Player-applied modifiers stack on top of these.
    // -------------------------------------------------------------------------

    public static double defaultStandDamage = 0.0;
    public static double defaultStandResistance = 0.0;
    public static double defaultStandGaugeMax = 0.0;

    // Global Stand Stats Defaults
    public static double defaultIdleDistance = 0.0;
    public static double defaultIdleRotation = 0.0;
    public static double defaultBlockDistance = 0.0;
    public static double defaultEngagementDistance = 0.0;
    public static double defaultAlphaOverride = -1.0;

    // Core Move Stats Defaults
    // Note: Duration/Distance multipliers default to 0.0 here because the registry base is 1.0.
    // The effective multiplier = registryBase (1.0) + modifierDelta (0.0) = 1.0 (neutral).
    public static double defaultCooldownReduction = 0.0;
    public static double defaultWindupReduction = 0.0;
    public static double defaultDurationMultiplier = 0.0;
    public static double defaultMoveDistanceMultiplier = 0.0;
    public static double defaultArmorBonus = 0.0;
    public static double defaultChargeDistanceMultiplier = 0.0;
    public static double defaultLifeSteal = 0.0;
    public static double defaultKnockbackModifier = 0.0;
    public static double defaultBlockStunReduction = 0.0;
    public static double defaultAttackRangeBonus = 0.0;

    // Specialized Ability Stats Defaults
    public static double defaultTimeStopDuration = 0.0;
    public static double defaultAccelDuration = 0.0;
    public static double defaultErasureDuration = 0.0;
    public static double defaultRewindReach = 0.0;
    public static double defaultErasureReach = 0.0;

    public static void load() {
        if (Files.exists(CONFIG_PATH)) {
            try {
                Properties props = new Properties();
                props.load(Files.newInputStream(CONFIG_PATH));

                defaultStandDamage = Double.parseDouble(props.getProperty("default_stand_damage", "0.0"));
                defaultStandResistance = Double.parseDouble(props.getProperty("default_stand_resistance", "0.0"));
                defaultStandGaugeMax = Double.parseDouble(props.getProperty("default_stand_gauge_max", "0.0"));

                // Load Global Stand Stats
                defaultIdleDistance = Double.parseDouble(props.getProperty("default_idle_distance", "0.0"));
                defaultIdleRotation = Double.parseDouble(props.getProperty("default_idle_rotation", "0.0"));
                defaultBlockDistance = Double.parseDouble(props.getProperty("default_block_distance", "0.0"));
                defaultEngagementDistance = Double.parseDouble(props.getProperty("default_engagement_distance", "0.0"));
                defaultAlphaOverride = Double.parseDouble(props.getProperty("default_alpha_override", "-1.0"));

                // Load Core Move Stats
                defaultCooldownReduction = Double.parseDouble(props.getProperty("default_cooldown_reduction", "0.0"));
                defaultWindupReduction = Double.parseDouble(props.getProperty("default_windup_reduction", "0.0"));
                defaultDurationMultiplier = Double.parseDouble(props.getProperty("default_duration_multiplier", "0.0"));
                defaultMoveDistanceMultiplier = Double.parseDouble(props.getProperty("default_move_distance_multiplier", "0.0"));
                defaultArmorBonus = Double.parseDouble(props.getProperty("default_armor_bonus", "0.0"));
                defaultChargeDistanceMultiplier = Double.parseDouble(props.getProperty("default_charge_distance_multiplier", "0.0"));
                defaultLifeSteal = Double.parseDouble(props.getProperty("default_life_steal", "0.0"));
                defaultKnockbackModifier = Double.parseDouble(props.getProperty("default_knockback_modifier", "0.0"));
                defaultBlockStunReduction = Double.parseDouble(props.getProperty("default_block_stun_reduction", "0.0"));
                defaultAttackRangeBonus = Double.parseDouble(props.getProperty("default_attack_range_bonus", "0.0"));

                // Load Specialized Ability Stats
                defaultTimeStopDuration = Double.parseDouble(props.getProperty("default_time_stop_duration", "0.0"));
                defaultAccelDuration = Double.parseDouble(props.getProperty("default_accel_duration", "0.0"));
                defaultErasureDuration = Double.parseDouble(props.getProperty("default_erasure_duration", "0.0"));
                defaultRewindReach = Double.parseDouble(props.getProperty("default_rewind_reach", "0.0"));
                defaultErasureReach = Double.parseDouble(props.getProperty("default_erasure_reach", "0.0"));
            } catch (IOException | NumberFormatException e) {
                JCraftAttributes.LOGGER.error("Failed to load jcraft_attributes.toml", e);
            }
        } else {
            save();
        }
    }

    public static void save() {
        try {
            StringBuilder content = new StringBuilder();
            content.append("default_stand_damage = ").append(defaultStandDamage).append("\n");
            content.append("default_stand_resistance = ").append(defaultStandResistance).append("\n");
            content.append("default_stand_gauge_max = ").append(defaultStandGaugeMax).append("\n");

            // Save Global Stand Stats
            content.append("default_idle_distance = ").append(defaultIdleDistance).append("\n");
            content.append("default_idle_rotation = ").append(defaultIdleRotation).append("\n");
            content.append("default_block_distance = ").append(defaultBlockDistance).append("\n");
            content.append("default_engagement_distance = ").append(defaultEngagementDistance).append("\n");
            content.append("default_alpha_override = ").append(defaultAlphaOverride).append("\n");

            // Save Core Move Stats
            content.append("default_cooldown_reduction = ").append(defaultCooldownReduction).append("\n");
            content.append("default_windup_reduction = ").append(defaultWindupReduction).append("\n");
            content.append("default_duration_multiplier = ").append(defaultDurationMultiplier).append("\n");
            content.append("default_move_distance_multiplier = ").append(defaultMoveDistanceMultiplier).append("\n");
            content.append("default_armor_bonus = ").append(defaultArmorBonus).append("\n");
            content.append("default_charge_distance_multiplier = ").append(defaultChargeDistanceMultiplier).append("\n");
            content.append("default_life_steal = ").append(defaultLifeSteal).append("\n");
            content.append("default_knockback_modifier = ").append(defaultKnockbackModifier).append("\n");
            content.append("default_block_stun_reduction = ").append(defaultBlockStunReduction).append("\n");
            content.append("default_attack_range_bonus = ").append(defaultAttackRangeBonus).append("\n");

            // Save Specialized Ability Stats
            content.append("default_time_stop_duration = ").append(defaultTimeStopDuration).append("\n");
            content.append("default_accel_duration = ").append(defaultAccelDuration).append("\n");
            content.append("default_erasure_duration = ").append(defaultErasureDuration).append("\n");
            content.append("default_rewind_reach = ").append(defaultRewindReach).append("\n");
            content.append("default_erasure_reach = ").append(defaultErasureReach).append("\n");

            Files.writeString(CONFIG_PATH, content.toString());
        } catch (IOException e) {
            JCraftAttributes.LOGGER.error("Failed to save jcraft_attributes.toml", e);
        }
    }
}
