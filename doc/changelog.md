# Changelog

### Bug Fixes
* Bug: Forge Dedicated Server crashing fixed
* Bug: Aya Tsuji not persisting fixed
* Bug: Stand Disc stand duplication fixed




### Known Bugs
#### Common
* Enemies can attack through dimensions (hit something -> D4C ult -> attacked by out-of-world ent)
  this bug is EVIL and has an unclear source >:(
  in the future, try looking at what sets the enemies target to null
  its obfuscated because of how goals are wrapped, but its probably possible to figure out
  To add to this, the loading of chunks in D4Cs dimension should be done with tickets instead of a mass forceload
#### Forge
* The client doesn't register The Sun as it's stand if moved out of and into render distance
  in Fabric, it simply unloads when out of sight. This behavior is preferable


#### todo (THIS UPDATE) :)
* Fix attacks not being able to trade - because ticking isn't done in parallel, the first entity to be ticked
    will have priority when it comes to attack calculations, even though both executed an attack at the same time.
    A possible solution to this is to just queue up all the move.tick() calls and run them all at the very end of a server tick.
* Fix AI stand users having clientside head rotation that completely throws off players
* Timestop should stop animated textures
* CRAZY DIAMOND, THE HAND (NEXT UPDATE), Hermit Purple, Yellow Temperance
* Stand NBT serialization
* Internationalization
* Actually use effect keyframes in animations
* Fix a ton of shit that gets broken by server restarts