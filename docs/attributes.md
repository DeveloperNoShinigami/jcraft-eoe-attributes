# Attribute Reference

[← Previous: Features](features.md) · [Home](../README.md) · [Next: Commands →](commands.md)

---

## Attribute Types

| Type | Symbol | How it works |
|---|---|---|
| **Flat** | `+N` | Adds a fixed value to the base stat |
| **Ratio** | `×(1−N)` | A 0.0–1.0 factor that scales a result down (e.g. CDR) |
| **Multiplier** | `×N` | Multiplies a value directly; default base is `1.0` |
| **Divisor** | `÷(1+N)` | Reduces a value via division; prevents full negation |
| **Special** | — | Unique behavior described per-attribute |

All attributes support the standard Minecraft three-slot modifier system:
`ADDITION` → `MULTIPLY_BASE` → `MULTIPLY_TOTAL` (applied in that order).

---

## Combat

| Attribute ID | Type | Default | Formula | Notes |
|---|---|---|---|---|
| `jcraft:stand_damage` | Flat | `0.0` | `finalDmg = baseDmg + getValue()` | Applied before VS-standless ×1.5 |
| `jcraft:stand_resistance` | Divisor | `0.0` | `finalDmg = rawDmg ÷ (1 + getValue())` | Diminishing returns by design |
| `jcraft:stand_gauge_max` | Flat | `0.0` | `finalMax = baseMax + getValue()` | Extends Stand Gauge pool |
| `jcraft:life_steal` | Ratio | `0.0` | `heal = totalDmg × getValue()` | `1.0` = 100%; decimals recommended |
| `jcraft:armor_bonus` | Flat | `0.0` | `finalDmg = max(0.1, dmg − getValue())` | Floors at 0.1 to prevent immunity |

---

## Positioning & Rendering

| Attribute ID | Type | Default | Formula | Notes |
|---|---|---|---|---|
| `jcraft:idle_distance` | Flat | `0.0` | `offset = baseOffset + getValue()` | Distance in blocks from user |
| `jcraft:idle_rotation` | Flat | `0.0` | `rotation = baseRotation + getValue()` | Degrees; full circle = 360 |
| `jcraft:block_distance` | Flat | `0.0` | `offset = (base + idle) + getValue()` | Extra offset while blocking |
| `jcraft:engagement_distance` | Flat | `0.0` | `dist = 6.0 + getValue()` | Auto-engage radius in blocks |
| `jcraft:alpha_override` | Special | `−1.0` | `alpha = getValue()` if `!= −1.0` | `−1.0` = disabled; `0.0–1.0` = fixed alpha |

---

## General Move Statistics

| Attribute ID | Type | Default | Formula | Notes |
|---|---|---|---|---|
| `jcraft:cooldown_reduction` | Ratio | `0.0` | `finalCD = baseCD × (1 − getValue())` | `0.5` = 50% CDR |
| `jcraft:windup_reduction` | Ratio | `0.0` | `finalWindup = base × (1 − getValue())` | `0.5` = 50% faster startup |
| `jcraft:duration_multiplier` | Multiplier | `1.0` | `finalDur = base × getValue()` | `1.5` = 50% longer |
| `jcraft:move_dist_multiplier` | Multiplier | `1.0` | `finalDist = base × getValue()` | Scales `moveDistance` field |
| `jcraft:charge_dist_multiplier` | Multiplier | `1.0` | `finalDist = base × getValue()` | Charge moves only |
| `jcraft:knockback_modifier` | Flat | `0.0` | `finalKB = base + getValue()` | Negative values reduce knockback |
| `jcraft:block_stun_reduction` | Flat | `0.0` | `finalStun = base − getValue()` | Reduces stun on blocked hits |
| `jcraft:attack_range_bonus` | Flat | `0.0` | `finalRange = base + getValue()` | Extends melee hit detection reach |

---

## Specialized Ability Stats

| Attribute ID | Type | Default | Applies To | Formula | Notes |
|---|---|---|---|---|---|
| `jcraft:time_stop_duration` | Flat (ticks) | `0.0` | TS Stands | `(base × durMult) + getValue()` | 20 ticks = 1 second |
| `jcraft:accel_duration` | Flat (ticks) | `0.0` | Made in Heaven | `(base × durMult) + getValue()` | Applied at RETURN after base is set |
| `jcraft:erasure_duration` | Flat (ticks) | `0.0` | King Crimson, Cream | `(base × durMult) + getValue()` | Covers both Time Erase and Void |
| `jcraft:erasure_reach` | Flat | `0.0` | The Hand | `16.0 + getValue()` | Added to 16.0 base constant |
| `jcraft:erasure_size` | Flat | `0.0` | Cream | `hitboxSize + getValue()` | Restored after perform(); no permanent mutation |
| `jcraft:rewind_reach` | Flat | `0.0` | Mandom | `baseReach + getValue()` | Applied via distance rescaling, not field mutation |

---

## Notes on Stacking

When multiple modifiers are applied to the same attribute they are evaluated in order:

1. **ADDITION** — all flat additions are summed first
2. **MULTIPLY_BASE** — each multiplies the result of step 1
3. **MULTIPLY_TOTAL** — each multiplies the result of step 2 cumulatively

Example with `stand_damage`, base `0.0`:
- ADDITION `+5` → value is `5.0`
- MULTIPLY_BASE `+0.2` → `5.0 × 1.2 = 6.0`
- MULTIPLY_TOTAL `+0.5` → `6.0 × 1.5 = 9.0`

---

[← Previous: Features](features.md) · [Home](../README.md) · [Next: Commands →](commands.md)
