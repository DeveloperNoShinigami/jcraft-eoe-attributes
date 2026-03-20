# Features

[← Home](../README.md)

---

## Combat System

### Stand Damage (`jcraft:stand_damage`)
Flat bonus added to every hit dealt by a Stand, before the VS-standless ×1.5 multiplier is applied.
Supports ADDITION, MULTIPLY_BASE, and MULTIPLY_TOTAL modifier stacking via the standard Minecraft attribute system.

### Stand Resistance (`jcraft:stand_resistance`)
Reduces incoming damage while actively using a Stand.
Scales as a divisor — higher values give diminishing returns, preventing one-shot immunity.

### Life Steal (`jcraft:life_steal`)
Heals the user for a fraction of damage dealt by Stand moves.
`1.0` = 100% life steal. Decimals recommended (e.g., `0.1` = 10%).

### Armor Bonus (`jcraft:armor_bonus`)
Flat damage reduction applied before the game's armor calculation.
Floors at `0.1` to prevent complete damage negation.

### Stand Gauge Max (`jcraft:stand_gauge_max`)
Extends the maximum Stand Gauge pool.
Flat bonus on top of the base max defined by JCraft.

---

## Positioning & Rendering

### Idle Distance (`jcraft:idle_distance`)
Controls how far the Stand floats from the user while idle.
Added to the base offset vector during `StandEntity` tick.

### Idle Rotation (`jcraft:idle_rotation`)
Rotates the Stand's idle orbit angle around the user in degrees.
Useful for positioning Stands at custom angles (e.g., directly in front or to the side).

### Block Distance (`jcraft:block_distance`)
Additional distance offset applied on top of idle distance while the user is blocking.
Stacks with `idle_distance`: `finalOffset = (base + idle) + block_distance`.

### Engagement Distance (`jcraft:engagement_distance`)
Extends the radius at which a Stand will automatically engage a target.
Baseline is `6.0` blocks; this attribute adds to that value.

### Alpha Override (`jcraft:alpha_override`)
Forces the Stand's render transparency to a fixed value.
`-1.0` disables the override (default). `0.0` = fully transparent, `1.0` = fully opaque.

---

## General Move Statistics

These attributes apply to all Stand moves via Mixin injection on `AbstractMove`.

### Cooldown Reduction (`jcraft:cooldown_reduction`)
Ratio-based reduction of all move cooldowns.
`0.5` = 50% shorter cooldowns. Hard cap recommended via server config.

### Windup Reduction (`jcraft:windup_reduction`)
Ratio-based reduction of move windup time.
`0.5` = 50% faster startup frames.

### Duration Multiplier (`jcraft:duration_multiplier`)
Scales the duration of all moves that have a duration field.
Default base is `1.0` (no change). `1.5` = 50% longer durations.

### Move Distance Multiplier (`jcraft:move_dist_multiplier`)
Scales how far standard moves travel.
Default base is `1.0`. Applied to `moveDistance` field.

### Charge Distance Multiplier (`jcraft:charge_dist_multiplier`)
Scales travel distance specifically for charge-type moves.
Default base is `1.0`. Applied separately from `move_dist_multiplier`.

### Knockback Modifier (`jcraft:knockback_modifier`)
Flat bonus (or reduction) to knockback applied to victims on hit.
Negative values reduce knockback.

### Block Stun Reduction (`jcraft:block_stun_reduction`)
Reduces the stun duration inflicted on the user when their attack is blocked.
Flat subtraction from the base stun value.

### Attack Range Bonus (`jcraft:attack_range_bonus`)
Extends the melee hitbox reach of moves.
Flat addition to the `moveDistance` field used for hit detection.

---

## Specialized Ability Stats

These attributes affect only specific Stands and have no effect on others.

### Time Stop Duration (`jcraft:time_stop_duration`)
Flat bonus ticks added to the Time Stop duration after `duration_multiplier` is applied.
`20` ticks = 1 extra second. Applies to The World, DIO, and any Time Stop Stand.

### Acceleration Duration (`jcraft:accel_duration`)
Flat bonus ticks added to Made in Heaven's Time Acceleration move.
Same timing as `time_stop_duration` — applied at RETURN after base is set.

### Erasure Duration (`jcraft:erasure_duration`)
Flat bonus ticks added to King Crimson's Time Erase and Cream's Void moves.

### Erasure Reach (`jcraft:erasure_reach`)
Extends the horizontal and vertical reach of The Hand's Erasure Space attack.
Added to the `16.0` base range constant.

### Erasure Size (`jcraft:erasure_size`)
Increases the diameter of Cream's Void Sphere hitbox.
Flat addition to `hitboxSize` for the duration of the perform call only (save/restore pattern).

### Rewind Reach (`jcraft:rewind_reach`)
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

All values are pre-armor, pre-standless-multiplier baselines.

---

[← Previous: Home](../README.md) · [Home](../README.md) · [Next: Attributes →](attributes.md)
