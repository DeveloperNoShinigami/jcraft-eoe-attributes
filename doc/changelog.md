# Changelog
## General
* made Stand data, Spec data and advancements translatable
* added a light-on-fire action
* added (partial) translations for
  * Russian
### Blocks & Items
* …
### NPCs & Stands
* …
### Configs
* …
### Commands
* …
### Bug Fixes
* fixed the exclusive stands bug
  * you should now be able to change your Stand skin when the option is enabled
* (maybe) fixed the Iron's Spellbooks incompatibility 
### Known Bugs
* Enemies can attack through dimensions (hit something -> D4C ult -> attacked by out-of-world ent)
  this bug is EVIL and has an unclear source >:(
  in the future, try looking at what sets the enemies target to null
  its obfuscated because of how goals are wrapped, but it's probably possible to figure out
  To add to this, the loading of chunks in D4Cs dimension should be done with tickets instead of a mass forceload
* Mobs don't spawn with stands in the Nether or End anymore; this is a stopgap solution to a bigger bug freezing the server
* using a SpecDisc on Vampire doesn't remove the blood bar
* using a SpecDisc on Anubis doesn't remove the bloodlust bar

## TODO (SOME UPDATE) :D
* Spin
* Throwing
* MR barrage fire :)
* Timestop should stop stand anims
* CRAZY DIAMOND, Hermit Purple, Yellow Temperance
* Stand NBT serialization
* Actually use effect keyframes in animations
