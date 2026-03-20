# Attribute Reference

[← Previous: Features](features.md) · [Home](../README.md) · [Next: Commands →](commands.md)

---

## Attribute Types

| Type | Symbol | How it works |
|---|---|---|
| **Flat** | `+N` | Adds a fixed value to the base stat; supports negative values |
| **Ratio** | `×(1−N)` | A 0.0–1.0 factor that scales a result down (e.g. CDR) |
| **Multiplier** | `×N` | Multiplies a value directly; default base is `1.0` |
| **Divisor** | `÷(1+N)` | Reduces a value via division; prevents full negation |

All attributes support the standard Minecraft three-slot modifier system:
`ADDITION` → `MULTIPLY_BASE` → `MULTIPLY_TOTAL` (applied in that order).

---

## Combat

| Attribute ID | Type | Range | Default | Formula |
|---|---|---|---|---|
| `jcraft_attributes:stand_damage` | Flat | −1024 – 1024 | `0.0` | `finalDmg = baseDmg + getValue()` |
| `jcraft_attributes:stand_resistance` | Divisor | −1024 – 1024 | `0.0` | `finalDmg = rawDmg ÷ (1 + getValue())` |
| `jcraft_attributes:stand_gauge_max` | Flat | −1024 – 1024 | `0.0` | `finalMax = baseMax + getValue()` |
| `jcraft_attributes:life_steal` | Ratio | 0.0 – 1.0 | `0.0` | `heal = totalDmg × getValue()` |
| `jcraft_attributes:armor_bonus` | Flat | −1024 – 1024 | `0.0` | `finalDmg = max(0.1, dmg − getValue())` |

---

## General Move Statistics

| Attribute ID | Type | Range | Default | Formula |
|---|---|---|---|---|
| `jcraft_attributes:cooldown_reduction` | Ratio | 0.0 – 1024 | `0.0` | `finalCD = baseCD × (1 − getValue())` |
| `jcraft_attributes:duration_multiplier` | Multiplier | 0.0 – 10.0 | `1.0` | `finalDur = base × getValue()` |
| `jcraft_attributes:knockback_modifier` | Flat | −10 – 10 | `0.0` | `finalKB = base + getValue()` |
| `jcraft_attributes:block_stun_reduction` | Flat | −1024 – 1024 | `0.0` | `finalStun = base − getValue()` |
| `jcraft_attributes:attack_range_bonus` | Flat | 0.0 – 32 | `0.0` | `finalRange = base + getValue()` |

---

## Specialized Ability Stats

| Attribute ID | Type | Range | Default | Applies To | Formula |
|---|---|---|---|---|---|
| `jcraft_attributes:time_stop_duration` | Flat (ticks) | −1024 – 1024 | `0.0` | TS Stands | `(base × durMult) + getValue()` |
| `jcraft_attributes:accel_duration` | Flat (ticks) | −1024 – 1024 | `0.0` | Made in Heaven | `(base × durMult) + getValue()` |
| `jcraft_attributes:erasure_duration` | Flat (ticks) | −1024 – 1024 | `0.0` | King Crimson, Cream | `(base × durMult) + getValue()` |
| `jcraft_attributes:rewind_reach` | Flat | −1024 – 1024 | `0.0` | Mandom | `baseReach + getValue()` |
| `jcraft_attributes:erasure_reach` | Flat | −1024 – 1024 | `0.0` | The Hand | `16.0 + getValue()` |
| `jcraft_attributes:erasure_size` | Flat | −32 – 32 | `0.0` | Cream | `hitboxSize + getValue()` |

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
