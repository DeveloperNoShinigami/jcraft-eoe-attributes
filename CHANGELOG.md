# Changelog - JCraft Attributes

## [1.0.1] - 2026-03-25

### Added
- **Per-Stand Persistence**: Each Stand now has its own isolated attribute progression. Swapping stands saves/loads attributes automatically via NBT (no more "global" player stats).
- **Stabilized Registry**: Implemented `DeferredRegister` for custom attributes to prevent race conditions during startup.
- **Placeholder Support**: Restored `windup_reduction` to the registry to reconcile legacy save data and prevent network misalignment.

### Fixed
- **AzureLib Animation Crash**: Identified and documented that `ID: 25185` (base_controller) crashes are resolved by updating to **AzureLib 3.1.4**.

### Removed
- **Rejected Positioning Attributes**: Flattened the registry by removing 6 unused/rejected stats (`idle_distance`, `idle_rotation`, `block_distance`, `move_distance_multiplier`, `engagement_distance`, and `alpha_override`).
