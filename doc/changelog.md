# Changelog
### General
* Upgraded the underlying system for Data-driven movesets
### NPCs & Stands
* Metallica
  * Removed Summon Forks, and replaced it with Create Magnetic Field
  * Internal Attack reworked to be an AOE which hits everything inside the user's Magnetic Fields
  * Hypoxia doesn't immediately drown, but also applies other effects before drowning:
    * I. -20% attack strength reduction
    * II. -20% movement speed reduction
    * III. -20% attack speed reduction + drowning
### Commands

### Bug Fixes
* ?


### Known Bugs
this is probably due to:
```java
    /// SplatterEffectRenderer
    matrices.translate(-camPos.x, -camPos.y, -camPos.z);

    /// PoseStack
    public void translate(double x, double y, double z) {
        this.translate((float)x, (float)y, (float)z);
    }
```
* Managed to get the "right" key to register as constantly pressed, screwing with dashes
  * i assume this works for any direction
* Enemies can attack through dimensions (hit something -> D4C ult -> attacked by out-of-world ent)
  this bug is EVIL and has an unclear source >:(
  in the future, try looking at what sets the enemies target to null
  its obfuscated because of how goals are wrapped, but it's probably possible to figure out
  To add to this, the loading of chunks in D4Cs dimension should be done with tickets instead of a mass forceload
## Forge
* The client doesn't register The Sun as it's stand if moved out of and into render distance
  in Fabric, it simply unloads when out of sight. This behavior is preferable
* When connected to Forge dedicated server, JCraft commands do not autocomplete correctly


#### todo (THIS UPDATE) :)
* my cawk
* MR barrage fire :)
* Fix AI stand users having clientside head rotation that completely throws off players
* Timestop should stop stand anims
* CRAZY DIAMOND, THE HAND, Hermit Purple, Yellow Temperance
* Stand NBT serialization
* Internationalization
* Actually use effect keyframes in animations
* Fix a ton of shit that gets broken by server restarts


