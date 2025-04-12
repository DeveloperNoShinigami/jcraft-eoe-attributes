# Changelog
### General
* Players now always take JCraft damage as if wearing unenchanted Netherite
* Upgraded the underlying system for Data-driven movesets
* Added a config option to disable Mining Barrages
* Added `jcraft:ferrous_entities`, for entities that are ferromagnetic
* Added various statistics
### NPCs & Stands
* Star Platinum
  * Re-tuned Inhale's pulling power
* The World
  * Updated texture and model
* Metallica
  * Create Magnetic Field no longer fails when trying to place out of range
  * Invisibility drains iron slower, heavily nerfs Harvest while active, and surrounds the user in a Magnetic Field
  * Darkened all skin auras
* Horus
  * Has Frost Walker ability now
* The World: Over Heaven
  * Updated texture and model
* Gold Experience: Requiem
  * Updated texture and model
### Items
* Added the Road Roller, a craftable vehicle
* Updated visuals for armor items
### Bug Fixes
* Fixed small mistakes in moves caused by Data-driven moveset rewrite
* Fixed Sheer Heart Attack creating ghost blocks when it explodes

* small stars (stun -d efault off)

### Commands



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
* CRAZY DIAMOND, Hermit Purple, Yellow Temperance
* Stand NBT serialization
* Internationalization
* Actually use effect keyframes in animations
* Fix a ton of shit that gets broken by server restarts


