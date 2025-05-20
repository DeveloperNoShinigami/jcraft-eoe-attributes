# Changelog
### General
* Stand User Ownable entities (like pets) no longer aggro onto their owner upon being hit
* Road Roller is now immune to damage-over-time potion effects
* Road Roller now explodes into its ingredients when killed
* Added a config to have JCraft damage scale with the opponent's maximum health. ON by default.
* Added a config which multiplies JCraft damage against standless non-player opponents. 2x by default.
### Bug Fixes
* Fixed being able to jump while stunned
* Fixed broken Barrage animation for GE:R
* Fixed Mining Barrages originating at the Stand User when in Remote mode
### NPCs & Stands
* Metallica
  * Given a unique, much quieter stand summoning sound

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
* stands shouldnt block arrows 24/7
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


