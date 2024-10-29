# Changelog

### General
* Stand damage now only considers armor values up to Netherite level
* Stand Arrows can now be fired out of bows
* Coffins now drop themselves upon being broken
* Replaced Lingering Potion in Diary Page recipe with Eye of Ender
* Narrator can now be disabled in the client settings (due to potential conflicts with JCraft binds, and a Mojang not adding the option)
* Mining Barrages now have extended range
### Commands
* /movedata was merged into **/framedata**
### Structures
* Cinderella's parlor has floors, filling the previously empty space
* Cinderella's parlor is now about as rare as a village
### NPCs & Stands
* **Added The Hand**
* NPC Stand users now wander around
* Taught NPC Stand users to not try to block unblockable moves
* Taught NPC Metallica users to harvest iron
* Adjusted Aya Tsuji's mask prices
* Updated King Crimson's model
### Bug Fixes
* Fixed crash with MmmMmmMmmMmm Dummy Mod
* Fixed crash with Dave's Potioneering
* Resolved crash with Jaden's Nether Expansion
* Fixed Gravity changes crashing Forge multiplayer
* Fixed infinite creative flight achieved by swapping GE:R and Cream while flying

### Known Bugs
## Common
* Splatter rendering gets extremely fucked at higher coordinates (~1mil and above)
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


