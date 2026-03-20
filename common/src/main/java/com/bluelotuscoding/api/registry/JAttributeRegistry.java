package com.bluelotuscoding.api.registry;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;

import java.util.UUID;

public class JAttributeRegistry {
    public static final String MOD_ID = "jcraft_attributes";

    // -------------------------------------------------------------------------
    // COMBAT STATS — applied to ALL Stands via AttacksMixin on Attacks.damage()
    // -------------------------------------------------------------------------

    // STAND_DAMAGE
    // Type: Flat addition | Unit: damage points | Applies to: ALL Stands
    // Added directly to the damage float in Attacks.damage() before it hits the
    // victim.
    // Chosen as flat so small investments have consistent, readable impact at any
    // damage level.
    // Formula: finalDamage = baseDamage + standDamage
    public static final Attribute STAND_DAMAGE = register("stand_damage",
            new RangedAttribute("attribute.name.jcraft.stand_damage", 0.0, 0.0, 1024.0).setSyncable(true));

    // STAND_RESISTANCE
    // Type: Diminishing-returns multiplier | Unit: resistance value | Applies to:
    // ALL Stands (as victim)
    // Reduces incoming Stand damage using a diminishing-returns formula.
    // Chosen over a flat reduction to prevent stacking to zero damage
    // (invulnerability).
    // Formula: finalDamage = rawDamage / (1 + standResistance)
    // e.g. resistance=1.0 → 50% damage, resistance=3.0 → 25% damage
    public static final Attribute STAND_RESISTANCE = register("stand_resistance",
            new RangedAttribute("attribute.name.jcraft.stand_resistance", 0.0, -1024.0, 1024.0).setSyncable(true));

    // STAND_GAUGE_MAX
    // Type: Flat addition | Unit: gauge units | Applies to: ALL Stands
    // Added to StandEntity.getMaxStandGauge() return value. Allows Stands to have a
    // larger energy pool.
    // Formula: finalGauge = baseGauge + standGaugeMax
    public static final Attribute STAND_GAUGE_MAX = register("stand_gauge_max",
            new RangedAttribute("attribute.name.jcraft.stand_gauge_max", 0.0, 0.0, 1024.0).setSyncable(true));

    // -------------------------------------------------------------------------
    // STAND POSITIONING — applied via StandEntityMixin on StandEntity
    // -------------------------------------------------------------------------

    // IDLE_DISTANCE
    // Type: Flat addition | Unit: blocks (float) | Applies to: ALL Stands
    // Added to the Stand's distance offset while not blocking. Shifts where the
    // Stand hovers.
    // Useful for Stands that should appear closer or farther from the user by
    // default.
    // Formula: finalDistance = baseDistance + idleDistance
    public static final Attribute IDLE_DISTANCE = register("idle_distance",
            new RangedAttribute("attribute.name.jcraft.idle_distance", 0.0, 0.0, 32.0).setSyncable(true));

    // IDLE_ROTATION
    // Type: Flat addition | Unit: degrees | Range: -360 to 360 | Applies to: ALL
    // Stands
    // Added to the Stand's rotation offset while idle. Rotates the Stand's orbit
    // position around the user.
    // Formula: finalRotation = baseRotation + idleRotation
    public static final Attribute IDLE_ROTATION = register("idle_rotation",
            new RangedAttribute("attribute.name.jcraft.idle_rotation", 0.0, -360.0, 360.0).setSyncable(true));

    // BLOCK_DISTANCE
    // Type: Flat addition | Unit: blocks (float) | Applies to: ALL Stands
    // Added on top of IDLE_DISTANCE when the Stand is in blocking state.
    // Lets blocking stances have a different spatial feel from idle.
    // Formula: finalBlockDistance = (baseDistance + idleDistance) + blockDistance
    public static final Attribute BLOCK_DISTANCE = register("block_distance",
            new RangedAttribute("attribute.name.jcraft.block_distance", 0.0, 0.0, 32.0).setSyncable(true));

    // ENGAGEMENT_DISTANCE
    // Type: Flat addition | Unit: blocks (double) | Applies to: ALL Stands
    // Added to the base engagement distance (6.0 blocks) via
    // IAttacker.getEngagementDistance().
    // Controls how far away a Stand can reach to engage a target automatically.
    // Formula: finalEngagement = 6.0 + engagementDistance
    public static final Attribute ENGAGEMENT_DISTANCE = register("engagement_distance",
            new RangedAttribute("attribute.name.jcraft.engagement_distance", 0.0, 0.0, 64.0).setSyncable(true));

    // ALPHA_OVERRIDE
    // Type: Direct value override | Unit: alpha float | Range: -1.0 (disabled) to
    // 1.0 (fully opaque)
    // Applies to: ALL Stands — applied every tick via MobEntityAttributesMixin
    // A value of -1.0 means no override (Stand uses its own default transparency).
    // Any value >= 0.0 overrides the Stand's render alpha directly.
    // Base is -1.0 (disabled state) — per-stand values replace this via
    // setBaseValue.
    public static final Attribute ALPHA_OVERRIDE = register("alpha_override",
            new RangedAttribute("attribute.name.jcraft.alpha_override", -1.0, -1.0, 1.0).setSyncable(true));

    // -------------------------------------------------------------------------
    // CORE MOVE STATS — applied via AbstractMoveMixin and related mixins
    // -------------------------------------------------------------------------

    // COOLDOWN_REDUCTION
    // Type: Percentage reduction | Unit: ratio (0.0–1.0) | Applies to: ALL Stands
    // Reduces all move cooldown durations by a percentage. Injected into
    // CommonCooldownsComponentImpl.setCooldown().
    // Chosen as a ratio so it scales proportionally with all move types.
    // Formula: finalCooldown = baseCooldown * (1 - cooldownReduction)
    // e.g. 0.5 = 50% shorter cooldowns
    public static final Attribute COOLDOWN_REDUCTION = register("cooldown_reduction",
            new RangedAttribute("attribute.name.jcraft.cooldown_reduction", 0.0, 0.0, 1024.0).setSyncable(true));

    // WINDUP_REDUCTION
    // Type: Percentage reduction | Unit: ratio (0.0–1.0) | Applies to: ALL Stands
    // Shortens how long before a move activates (windup ticks). Injected into
    // AbstractMove.hasWindupPassed().
    // Lets skilled players invest in faster execution without removing the windup
    // mechanic entirely.
    // Formula: effectiveWindup = windup * (1 - windupReduction)
    // e.g. 0.3 means the move activates at 70% of its normal windup tick count
    public static final Attribute WINDUP_REDUCTION = register("windup_reduction",
            new RangedAttribute("attribute.name.jcraft.windup_reduction", 0.0, 0.0, 1024.0).setSyncable(true));

    // DURATION_MULTIPLIER
    // Type: Multiplier | Unit: multiplier (base 1.0, max 10.0) | Applies to: ALL
    // Stands with duration moves
    // Scales the duration of moves/abilities. Applied in AbstractMoveMixin and
    // specialized move mixins.
    // Chosen as a multiplier so the effect scales correctly with both short and
    // long moves.
    // Formula: finalDuration = baseDuration * getValue()
    // Default base = 1.0 (neutral). Per-stand ADDITION modifiers offset from 1.0.
    // e.g. per-stand modifier = 0.5 → getValue() = 1.5 → 150% duration
    public static final Attribute DURATION_MULTIPLIER = register("duration_multiplier",
            new RangedAttribute("attribute.name.jcraft.duration_multiplier", 1.0, 0.0, 10.0).setSyncable(true));

    // MOVE_DISTANCE_MULTIPLIER
    // Type: Multiplier | Unit: multiplier (base 1.0, max 10.0) | Applies to: ALL
    // Stands
    // Scales how far moves travel. Injected into AbstractMove.getMoveDistance().
    // Formula: finalDistance = baseMoveDistance * getValue()
    // Default base = 1.0 (neutral). Per-stand ADDITION modifiers offset from 1.0.
    public static final Attribute MOVE_DISTANCE_MULTIPLIER = register("move_distance_multiplier",
            new RangedAttribute("attribute.name.jcraft.move_distance_multiplier", 1.0, 0.0, 10.0).setSyncable(true));

    // ARMOR_BONUS
    // Type: Flat damage reduction | Unit: damage points | Applies to: ALL entities
    // (player/Stand user)
    // Reduces incoming damage by a flat amount. Injected into LivingEntity.hurt()
    // via @ModifyVariable.
    // Chosen as flat to give a clear, predictable defensive value.
    // Formula: finalIncomingDamage = max(0.1, incomingDamage - armorBonus)
    public static final Attribute ARMOR_BONUS = register("armor_bonus",
            new RangedAttribute("attribute.name.jcraft.armor_bonus", 0.0, 0.0, 1024.0).setSyncable(true));

    // CHARGE_DISTANCE_MULTIPLIER
    // Type: Multiplier | Unit: multiplier (base 1.0, max 10.0) | Applies to: ALL
    // Stands with charge attacks
    // Scales how far charge attacks travel. Injected into
    // AbstractChargeAttack.tickChargeAttack().
    // Formula: finalChargeDistance = baseMoveDistance * getValue()
    // Default base = 1.0 (neutral). Per-stand ADDITION modifiers offset from 1.0.
    public static final Attribute CHARGE_DISTANCE_MULTIPLIER = register("charge_distance_multiplier",
            new RangedAttribute("attribute.name.jcraft.charge_distance_multiplier", 1.0, 0.0, 10.0).setSyncable(true));

    // LIFE_STEAL
    // Type: Percentage of damage dealt | Unit: ratio (0.0–1.0) | Applies to: ALL
    // Stands (AbstractSimpleAttack)
    // Heals the attacker for a portion of damage dealt. Applied after
    // AbstractSimpleAttack.perform() completes.
    // Chosen as a ratio so it scales with actual damage output rather than being a
    // fixed heal.
    // Formula: healAmount = totalDamageDealt * numberOfTargetsHit * lifeSteal
    // e.g. 0.2 = heal 20% of damage dealt per target hit
    public static final Attribute LIFE_STEAL = register("life_steal",
            new RangedAttribute("attribute.name.jcraft.life_steal", 0.0, 0.0, 1.0).setSyncable(true));

    // KNOCKBACK_MODIFIER
    // Type: Flat modifier | Unit: knockback units | Range: -10 to 10 | Applies to:
    // ALL Stands (AbstractSimpleAttack)
    // Added to the knockback value applied to victims on hit. Negative values
    // reduce knockback.
    // Formula: finalKnockback = baseKnockback + knockbackModifier
    public static final Attribute KNOCKBACK_MODIFIER = register("knockback_modifier",
            new RangedAttribute("attribute.name.jcraft.knockback_modifier", 0.0, -10.0, 10.0).setSyncable(true));

    // BLOCK_STUN_REDUCTION
    // Type: Flat reduction | Unit: stun ticks | Applies to: ALL Stands
    // (AbstractSimpleAttack)
    // Reduces how many ticks the attacker is stunned when their attack is blocked.
    // Lets aggressive Stands stay active through blocked exchanges.
    // Formula: finalBlockStun = baseBlockStun - blockStunReduction
    public static final Attribute BLOCK_STUN_REDUCTION = register("block_stun_reduction",
            new RangedAttribute("attribute.name.jcraft.block_stun_reduction", 0.0, 0.0, 1024.0).setSyncable(true));

    // ATTACK_RANGE_BONUS
    // Type: Flat addition | Unit: blocks (float) | Applies to: ALL Stands
    // (AbstractSimpleAttack)
    // Added to the hitbox range used in AbstractSimpleAttack.perform(). Increases
    // melee reach.
    // Formula: finalRange = baseRange + attackRangeBonus
    public static final Attribute ATTACK_RANGE_BONUS = register("attack_range_bonus",
            new RangedAttribute("attribute.name.jcraft.attack_range_bonus", 0.0, 0.0, 32.0).setSyncable(true));

    // -------------------------------------------------------------------------
    // SPECIALIZED ABILITY STATS — Stand-specific; no effect on other Stands
    // -------------------------------------------------------------------------

    // TIME_STOP_DURATION
    // Type: Flat addition | Unit: ticks | Applies to: ALL Stands with
    // AbstractTimeStopMove
    // Adds ticks to time-stop type abilities on top of the DURATION_MULTIPLIER
    // scaling.
    // Allows fine-grained tuning of time stop length independent of other duration
    // scaling.
    // Formula: finalDuration = (baseDuration * DURATION_MULTIPLIER.getValue()) +
    // timeStopDuration
    public static final Attribute TIME_STOP_DURATION = register("time_stop_duration",
            new RangedAttribute("attribute.name.jcraft.time_stop_duration", 0.0, -1024.0, 1024.0).setSyncable(true));

    // ACCEL_DURATION
    // Type: Flat addition | Unit: ticks | Applies to: MADE IN HEAVEN ONLY
    // (TimeAccelerationMove)
    // No effect on any other Stand. Scales Made in Heaven's signature time
    // acceleration independently
    // from other duration attributes, so MiH players can specialize without
    // affecting other mechanics.
    // Formula: finalDuration = (baseDuration * DURATION_MULTIPLIER.getValue()) +
    // accelDuration
    public static final Attribute ACCEL_DURATION = register("accel_duration",
            new RangedAttribute("attribute.name.jcraft.accel_duration", 0.0, -1024.0, 1024.0).setSyncable(true));

    // ERASURE_DURATION
    // Type: Flat addition | Unit: ticks | Applies to: KING CRIMSON (TimeEraseMove)
    // + CREAM (ConsumeAttack)
    // Shared between King Crimson's time erase and Cream's void ability — both
    // involve "erasing" time/space.
    // No effect on any other Stand.
    // Formula: finalDuration = (baseDuration * DURATION_MULTIPLIER.getValue()) +
    // erasureDuration
    // (for Cream's void: finalVoidTicks = baseVoidTicks + erasureDuration)
    public static final Attribute ERASURE_DURATION = register("erasure_duration",
            new RangedAttribute("attribute.name.jcraft.erasure_duration", 0.0, -1024.0, 1024.0).setSyncable(true));

    // REWIND_REACH
    // Type: Flat addition | Unit: blocks | Applies to: MANDOM ONLY (RewindMove)
    // Added to Mandom's rewind reach field via @Redirect in perform(). No effect on
    // any other Stand.
    // Lets players specializing in Mandom extend the signature time rewind
    // ability's range.
    // Formula: finalReach = baseReach + rewindReach
    public static final Attribute REWIND_REACH = register("rewind_reach",
            new RangedAttribute("attribute.name.jcraft.rewind_reach", 0.0, -1024.0, 1024.0).setSyncable(true));

    // ERASURE_REACH
    // Type: Flat addition | Unit: blocks | Applies to: THE HAND ONLY
    // (EraseSpaceAttack)
    // Added to the raycast range (base 16 blocks) used in
    // EraseSpaceAttack.perform() via @Redirect.
    // No effect on any other Stand. Chosen to let players spec into The Hand's
    // defining move.
    // Formula: finalRange = 16.0 + erasureReach
    public static final Attribute ERASURE_REACH = register("erasure_reach",
            new RangedAttribute("attribute.name.jcraft.erasure_reach", 0.0, -1024.0, 1024.0).setSyncable(true));

    // ERASURE_SIZE
    // Type: Flat addition | Unit: scale float | Applies to: CREAM ONLY
    // (ConsumeAttack)
    // Formula: finalSize = baseSize + erasureSize
    public static final Attribute ERASURE_SIZE = register("erasure_size",
            new RangedAttribute("attribute.name.jcraft.erasure_size", 0.0, -32.0, 32.0).setSyncable(true));

    // -------------------------------------------------------------------------
    // SLOT UUIDS — Used for the three-slot modifier system (base / slot1 / slot2)
    // -------------------------------------------------------------------------

    public static final UUID SLOT_1_UUID = UUID.fromString("6a3d1c8e-5b2f-4a0d-9e7c-3f4b5a6c7d8e");
    public static final UUID SLOT_2_UUID = UUID.fromString("7b4e2d9f-6c30-5b1e-0f8d-4a5b6c7d8e9f");

    private static Attribute register(String name, Attribute attribute) {
        System.out.println("[JCraft Attributes] Registering attribute: " + name);
        return Registry.register(BuiltInRegistries.ATTRIBUTE, new ResourceLocation(MOD_ID, name), attribute);
    }

    public static void init() {
    }
}
