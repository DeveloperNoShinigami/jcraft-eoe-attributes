# Commands

[← Previous: Attributes](attributes.md) · [Home](../README.md)

---

## JCraft Attributes Commands

### `/jcraft_attributes reset <player>`

Wipes all attribute NBT data saved on the player for every Stand they have loaded, resetting everything to registry defaults.

- **Permission**: Operator level 2
- **Use when**: A stand's stats are corrupted, or you need a clean slate for a player
- **Example**: `/jcraft_attributes reset Steve`

---

## Vanilla `/attribute` Commands

JCraft Attributes integrates with the standard Minecraft `/attribute` command.
All custom attributes use the `jcraft_attributes:` namespace.

---

### Set Base Value

Sets the attribute's base value directly. This is the simplest approach — no UUID needed.

```
/attribute <player> <attribute_id> base set <value>
```

**Example — give yourself +5 stand damage:**
```
/attribute @s jcraft_attributes:stand_damage base set 5.0
```

---

### Add a Modifier

Modifiers stack on top of the base without overwriting it. Useful for items, temporary buffs, and skill trees.

#### Flat Addition (`add_value`)

Adds a fixed amount to the base value.

```
/attribute @s <attribute_id> modifier add <uuid> <name> <amount> add_value
```

**Example:**
```
/attribute @s jcraft_attributes:stand_damage modifier add 00000001-0000-0000-0000-000000000001 "sword_bonus" 3 add_value
```

#### Multiply Base (`add_multiplied_base`)

Multiplies the base value by `(1 + amount)`. Applied after all ADDITION modifiers.

```
/attribute @s <attribute_id> modifier add <uuid> <name> <amount> add_multiplied_base
```

**Example — 20% bonus on top of base:**
```
/attribute @s jcraft_attributes:stand_damage modifier add 00000001-0000-0000-0000-000000000002 "ring_boost" 0.2 add_multiplied_base
```

#### Multiply Total (`add_multiplied_total`)

Multiplies the final computed value. Applied last; multiple MULTIPLY_TOTAL modifiers stack multiplicatively.

```
/attribute @s <attribute_id> modifier add <uuid> <name> <amount> add_multiplied_total
```

**Example — double all stand damage:**
```
/attribute @s jcraft_attributes:stand_damage modifier add 00000001-0000-0000-0000-000000000003 "power_double" 1.0 add_multiplied_total
```

---

### Remove a Modifier

```
/attribute @s <attribute_id> modifier remove <uuid>
```

**Example:**
```
/attribute @s jcraft_attributes:stand_damage modifier remove 00000001-0000-0000-0000-000000000001
```

---

### Query Current Value

```
/attribute @s <attribute_id> get
```

Returns the fully-computed final value including all active modifiers.

---

## All Attribute IDs

| Attribute | ID |
|---|---|
| Stand Damage | `jcraft_attributes:stand_damage` |
| Stand Resistance | `jcraft_attributes:stand_resistance` |
| Stand Gauge Max | `jcraft_attributes:stand_gauge_max` |
| Life Steal | `jcraft_attributes:life_steal` |
| Armor Bonus | `jcraft_attributes:armor_bonus` |
| Cooldown Reduction | `jcraft_attributes:cooldown_reduction` |
| Duration Multiplier | `jcraft_attributes:duration_multiplier` |
| Knockback Modifier | `jcraft_attributes:knockback_modifier` |
| Block Stun Reduction | `jcraft_attributes:block_stun_reduction` |
| Attack Range Bonus | `jcraft_attributes:attack_range_bonus` |
| Windup Reduction | `jcraft_attributes:windup_reduction` |

| Time Stop Duration | `jcraft_attributes:time_stop_duration` |
| Acceleration Duration | `jcraft_attributes:accel_duration` |
| Erasure Duration | `jcraft_attributes:erasure_duration` |
| Rewind Reach | `jcraft_attributes:rewind_reach` |
| Erasure Reach | `jcraft_attributes:erasure_reach` |
| Erasure Size | `jcraft_attributes:erasure_size` |

---

> Each modifier must have a **unique UUID** to avoid overwriting other modifiers on the same attribute.
> Generate UUIDs at [uuidgenerator.net](https://www.uuidgenerator.net/) or use sequential patterns like `00000001-0000-0000-0000-000000000001`.

---

[← Previous: Attributes](attributes.md) · [Home](../README.md)
