package com.bluelotuscoding.api.registry;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;

import java.util.List;
import java.util.UUID;

public class JAttributeRegistry {
    public static final String MOD_ID = "jcraft_attributes";
    private static final List<java.util.function.Supplier<Attribute>> REGISTRY_TASKS = new java.util.ArrayList<>();

    // -------------------------------------------------------------------------
    // COMBAT STATS — applied to ALL Stands via AttacksMixin on Attacks.damage()
    // -------------------------------------------------------------------------

    // STAND_DAMAGE
    // Type: Flat addition | Unit: damage points | Applies to: ALL Stands
    // Formula: finalDamage = baseDamage + standDamage
    public static final Attribute STAND_DAMAGE = deferredRegister("stand_damage", 
            new RangedAttribute("attribute.name.jcraft.stand_damage", 0.0, -1024.0, 1024.0).setSyncable(true));

    // STAND_RESISTANCE
    // Type: Diminishing-returns multiplier | Unit: resistance value | Applies to: ALL Stands (as victim)
    // Formula: finalDamage = rawDamage / (1 + standResistance)
    // e.g. resistance=1.0 → 50% damage, resistance=3.0 → 25% damage
    public static final Attribute STAND_RESISTANCE = deferredRegister("stand_resistance",
            new RangedAttribute("attribute.name.jcraft.stand_resistance", 0.0, -1024.0, 1024.0).setSyncable(true));

    // STAND_GAUGE_MAX
    // Type: Flat addition | Unit: gauge units | Applies to: ALL Stands
    // Formula: finalGauge = baseGauge + standGaugeMax
    public static final Attribute STAND_GAUGE_MAX = deferredRegister("stand_gauge_max",
            new RangedAttribute("attribute.name.jcraft.stand_gauge_max", 0.0, -1024.0, 1024.0).setSyncable(true));

    // -------------------------------------------------------------------------
    // CORE MOVE STATS — applied via AbstractMoveMixin and related mixins
    // -------------------------------------------------------------------------

    // COOLDOWN_REDUCTION
    // Type: Percentage reduction | Unit: ratio (0.0–1.0) | Applies to: ALL Stands
    // Formula: finalCooldown = baseCooldown * (1 - cooldownReduction)
    // e.g. 0.5 = 50% shorter cooldowns
    public static final Attribute COOLDOWN_REDUCTION = deferredRegister("cooldown_reduction",
            new RangedAttribute("attribute.name.jcraft.cooldown_reduction", 0.0, 0.0, 1024.0).setSyncable(true));

    // DURATION_MULTIPLIER
    // Type: Multiplier | Unit: multiplier (base 1.0, max 10.0) | Applies to: ALL Stands with duration moves
    // Formula: finalDuration = baseDuration * getValue()
    // Default base = 1.0 (neutral). Per-stand ADDITION modifiers offset from 1.0.
    public static final Attribute DURATION_MULTIPLIER = deferredRegister("duration_multiplier",
            new RangedAttribute("attribute.name.jcraft.duration_multiplier", 1.0, 0.0, 10.0).setSyncable(true));

    // ARMOR_BONUS
    // Type: Flat damage reduction | Unit: damage points | Applies to: ALL entities (player/Stand user)
    // Formula: finalIncomingDamage = max(0.1, incomingDamage - armorBonus)
    public static final Attribute ARMOR_BONUS = deferredRegister("armor_bonus",
            new RangedAttribute("attribute.name.jcraft.armor_bonus", 0.0, -1024.0, 1024.0).setSyncable(true));

    // LIFE_STEAL
    // Type: Percentage of damage dealt | Unit: ratio (0.0–1.0) | Applies to: ALL Stands (AbstractSimpleAttack)
    // Formula: healAmount = totalDamageDealt * numberOfTargetsHit * lifeSteal
    // e.g. 0.2 = heal 20% of damage dealt per target hit
    public static final Attribute LIFE_STEAL = deferredRegister("life_steal",
            new RangedAttribute("attribute.name.jcraft.life_steal", 0.0, 0.0, 1.0).setSyncable(true));

    // KNOCKBACK_MODIFIER
    // Type: Flat modifier | Unit: knockback units | Applies to: ALL Stands (AbstractSimpleAttack)
    // Formula: finalKnockback = baseKnockback + knockbackModifier
    public static final Attribute KNOCKBACK_MODIFIER = deferredRegister("knockback_modifier",
            new RangedAttribute("attribute.name.jcraft.knockback_modifier", 0.0, -10.0, 10.0).setSyncable(true));

    // BLOCK_STUN_REDUCTION
    // Type: Flat reduction | Unit: stun ticks | Applies to: ALL Stands (AbstractSimpleAttack)
    // Formula: finalBlockStun = baseBlockStun - blockStunReduction
    public static final Attribute BLOCK_STUN_REDUCTION = deferredRegister("block_stun_reduction",
            new RangedAttribute("attribute.name.jcraft.block_stun_reduction", 0.0, -1024.0, 1024.0).setSyncable(true));

    // ATTACK_RANGE_BONUS
    // Type: Flat addition | Unit: blocks (float) | Applies to: ALL Stands (AbstractSimpleAttack)
    // Formula: finalRange = baseRange + attackRangeBonus
    public static final Attribute ATTACK_RANGE_BONUS = deferredRegister("attack_range_bonus",
            new RangedAttribute("attribute.name.jcraft.attack_range_bonus", 0.0, 0.0, 32.0).setSyncable(true));

    // WINDUP_REDUCTION
    // Type: Percentage reduction | Unit: ratio (0.0–1.0) | Applies to: ALL Moves
    // Formula: finalWindup = baseWindup * (1 - windupReduction)
    public static final Attribute WINDUP_REDUCTION = deferredRegister("windup_reduction",
            new RangedAttribute("attribute.name.jcraft.windup_reduction", 0.0, 0.0, 1.0).setSyncable(true));

    // -------------------------------------------------------------------------
    // SPECIALIZED ABILITY STATS — Stand-specific; no effect on other Stands
    // -------------------------------------------------------------------------

    // TIME_STOP_DURATION
    // Type: Flat addition | Unit: ticks | Applies to: ALL Stands with AbstractTimeStopMove
    // Formula: finalDuration = (baseDuration * DURATION_MULTIPLIER.getValue()) + timeStopDuration
    public static final Attribute TIME_STOP_DURATION = deferredRegister("time_stop_duration",
            new RangedAttribute("attribute.name.jcraft.time_stop_duration", 0.0, -1024.0, 1024.0).setSyncable(true));

    // ACCEL_DURATION
    // Type: Flat addition | Unit: ticks | Applies to: MADE IN HEAVEN ONLY (TimeAccelerationMove)
    // Formula: finalDuration = (baseDuration * DURATION_MULTIPLIER.getValue()) + accelDuration
    public static final Attribute ACCEL_DURATION = deferredRegister("accel_duration",
            new RangedAttribute("attribute.name.jcraft.accel_duration", 0.0, -1024.0, 1024.0).setSyncable(true));

    // ERASURE_DURATION
    // Type: Flat addition | Unit: ticks | Applies to: KING CRIMSON (TimeEraseMove) + CREAM (ConsumeAttack)
    // Formula: finalDuration = (baseDuration * DURATION_MULTIPLIER.getValue()) + erasureDuration
    public static final Attribute ERASURE_DURATION = deferredRegister("erasure_duration",
            new RangedAttribute("attribute.name.jcraft.erasure_duration", 0.0, -1024.0, 1024.0).setSyncable(true));

    // REWIND_REACH
    // Type: Flat addition | Unit: blocks | Applies to: MANDOM ONLY (RewindMove)
    // Formula: finalReach = baseReach + rewindReach
    public static final Attribute REWIND_REACH = deferredRegister("rewind_reach",
            new RangedAttribute("attribute.name.jcraft.rewind_reach", 0.0, -1024.0, 1024.0).setSyncable(true));

    // ERASURE_REACH
    // Type: Flat addition | Unit: blocks | Applies to: THE HAND ONLY (EraseSpaceAttack)
    // Formula: finalRange = 16.0 + erasureReach
    public static final Attribute ERASURE_REACH = deferredRegister("erasure_reach",
            new RangedAttribute("attribute.name.jcraft.erasure_reach", 0.0, -1024.0, 1024.0).setSyncable(true));

    // ERASURE_SIZE
    // Type: Flat addition | Unit: scale float | Applies to: CREAM ONLY (ConsumeAttack)
    // Formula: finalSize = baseSize + erasureSize
    public static final Attribute ERASURE_SIZE = deferredRegister("erasure_size",
            new RangedAttribute("attribute.name.jcraft.erasure_size", 0.0, -32.0, 32.0).setSyncable(true));

    public static final UUID SLOT_1_UUID = UUID.fromString("6a3d1c8e-5b2f-4a0d-9e7c-3f4b5a6c7d8e");
    public static final UUID SLOT_2_UUID = UUID.fromString("7b4e2d9f-6c30-5b1e-0f8d-4a5b6c7d8e9f");

    public record AttributeEntry(Attribute attribute, String nbtKey) {}

    public static final List<AttributeEntry> ALL = List.of(
        new AttributeEntry(STAND_DAMAGE,            "StandDamage"),
        new AttributeEntry(STAND_RESISTANCE,        "StandResistance"),
        new AttributeEntry(STAND_GAUGE_MAX,         "StandGaugeMax"),
        new AttributeEntry(COOLDOWN_REDUCTION,      "CooldownReduction"),
        new AttributeEntry(DURATION_MULTIPLIER,     "DurationMultiplier"),
        new AttributeEntry(ARMOR_BONUS,             "ArmorBonus"),
        new AttributeEntry(LIFE_STEAL,              "LifeSteal"),
        new AttributeEntry(KNOCKBACK_MODIFIER,      "KnockbackModifier"),
        new AttributeEntry(BLOCK_STUN_REDUCTION,    "BlockStunReduction"),
        new AttributeEntry(ATTACK_RANGE_BONUS,      "AttackRangeBonus"),
        new AttributeEntry(WINDUP_REDUCTION,        "WindupReduction"),
        new AttributeEntry(TIME_STOP_DURATION,      "TimeStopDuration"),
        new AttributeEntry(ACCEL_DURATION,          "AccelDuration"),
        new AttributeEntry(ERASURE_DURATION,        "ErasureDuration"),
        new AttributeEntry(REWIND_REACH,            "RewindReach"),
        new AttributeEntry(ERASURE_REACH,           "ErasureReach"),
        new AttributeEntry(ERASURE_SIZE,            "ErasureSize")
    );

    private static Attribute deferredRegister(String name, Attribute attribute) {
        REGISTRY_TASKS.add(() -> Registry.register(BuiltInRegistries.ATTRIBUTE, new ResourceLocation(MOD_ID, name), attribute));
        return attribute;
    }

    public static void init() {
        REGISTRY_TASKS.forEach(java.util.function.Supplier::get);
    }
}
