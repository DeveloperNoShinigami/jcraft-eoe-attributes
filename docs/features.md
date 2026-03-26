# Features

[← Home](../README.md)

---

## Combat System

### Stand Damage (`jcraft_attributes:stand_damage`)
Flat bonus added to every hit dealt by a Stand, applied before armor reduction.
Supports ADDITION, MULTIPLY_BASE, and MULTIPLY_TOTAL modifier stacking via the standard Minecraft attribute system.
Negative values are supported (damage penalty).

### Stand Resistance (`jcraft_attributes:stand_resistance`)
Reduces incoming Stand damage via diminishing returns.
Higher values give progressively less reduction — prevents full immunity.

### Life Steal (`jcraft_attributes:life_steal`)
Heals the user for a fraction of damage dealt by Stand moves.
`1.0` = 100% life steal. Decimals recommended (e.g., `0.1` = 10%).

### Armor Bonus (`jcraft_attributes:armor_bonus`)
Flat damage reduction applied to all incoming damage.
Floors at `0.1` to prevent complete damage negation. Negative values increase damage taken.

### Stand Gauge Max (`jcraft_attributes:stand_gauge_max`)
Extends the maximum Stand Gauge pool.
Flat bonus on top of the base max defined by JCraft. Negative values shrink the pool.

---

## General Move Statistics

These attributes apply to all Stand moves via Mixin injection on `AbstractMove`.

### Cooldown Reduction (`jcraft_attributes:cooldown_reduction`)
Ratio-based reduction of all move cooldowns.
`0.5` = 50% shorter cooldowns. Hard cap recommended via server config.

### Duration Multiplier (`jcraft_attributes:duration_multiplier`)
Scales the duration of all moves that have a duration field.
Default base is `1.0` (no change). `1.5` = 50% longer durations.

### Knockback Modifier (`jcraft_attributes:knockback_modifier`)
Flat bonus (or reduction) to knockback applied to victims on hit.
Negative values reduce knockback.

### Block Stun Reduction (`jcraft_attributes:block_stun_reduction`)
Reduces the stun duration inflicted on the user when their attack is blocked.
Flat subtraction from the base stun value. Negative values increase stun.

### Attack Range Bonus (`jcraft_attributes:attack_range_bonus`)
Extends the melee hitbox reach of moves.
Flat addition to the `moveDistance` field used for hit detection.

### Windup Reduction (`jcraft_attributes:windup_reduction`)
*Current Placeholder.* Intended to reduce the "startup" time of Stand moves. 
Maintained in the registry to ensure technical stability with existing player data.


---

## Specialized Ability Stats

These attributes affect only specific Stands and have no effect on others.

### Time Stop Duration (`jcraft_attributes:time_stop_duration`)
Flat bonus ticks added to the Time Stop duration after `duration_multiplier` is applied.
`20` ticks = 1 extra second. Applies to The World, DIO, and any Time Stop Stand.

### Acceleration Duration (`jcraft_attributes:accel_duration`)
Flat bonus ticks added to Made in Heaven's Time Acceleration move.
Same timing as `time_stop_duration` — applied after base is set.

### Erasure Duration (`jcraft_attributes:erasure_duration`)
Flat bonus ticks added to King Crimson's Time Erase and Cream's Void moves.

### Erasure Reach (`jcraft_attributes:erasure_reach`)
Extends the horizontal and vertical reach of The Hand's Erasure Space attack.
Added to the `16.0` base range constant.

### Erasure Size (`jcraft_attributes:erasure_size`)
Increases the diameter of Cream's Void Sphere hitbox.
Flat addition to `hitboxSize` for the duration of the perform call only (save/restore pattern).

### Rewind Reach (`jcraft_attributes:rewind_reach`)
Extends the range at which Mandom's Time Rewind can target entities.
Applied via distance rescaling — does not mutate the `final int reach` field.

---

## `/stand about` Tooltip Integration

When a player has non-zero attributes, the `/stand about` command appends calculated stat lines to each move's description:

| Stat shown | Attribute required |
|---|---|
| Total Cooldown | `cooldown_reduction != 0` and move has cooldown |
| Total Duration | `duration_multiplier != 1.0` or duration bonus != 0 |
| Total Reach | `attack_range_bonus != 0` and move has reach |
| Total Damage | `stand_damage != 0` and move has damage field |

All values are pre-armor baselines.

---

## Per-Stand Attribute Storage

Attributes are saved and loaded **per Stand**. Each Stand type maintains its own attribute state. Switching Stands automatically saves the current Stand's attributes and restores the new Stand's saved state. All values persist across server restarts via NBT.

---

[← Previous: Home](../README.md) · [Home](../README.md) · [Next: Attributes →](attributes.md)
