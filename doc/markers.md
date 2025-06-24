# How to use Markers
## MarkerType
MarkerTypes decide HOW something is saved and loaded. There are two kinds:
* BlockMarkerType: uses the BlockPos as an ID and BlockState as the thing to save, saved into a BlockMarker
* EntityMarkerType:
  * uses the UUID as an ID and the Entity as the thing to save, saved into an EntityMarker
  * takes a collection of ResourceLocations (see Identifiers) for WHAT to save of the entity and a collection of EntityDataHandlers to determine HOW it is saved
  * the HOW it is saved is important because Forge Capabilities are not NBT data I think

