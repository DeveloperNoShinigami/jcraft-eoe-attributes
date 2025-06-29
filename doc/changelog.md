# Changelog: The Cosplay Update
## General
* Road Roller now explodes into its ingredients when destroyed
* overhauled the internal action and Stand Pose system, making development and datapacks easier
* Stands, Specs and Moves now have their own registry, enabling the creation of Add-Ons
* evolution items are now data driven as well
* added a general item throwing mechanic
  * experimental and not accessible yet
* extended documentation with UML
* added Stand/Spec obtainment trigger, enabling new advancements
* Stand user mob follow range is now based on local difficulty
* Stand user mobs no longer drop diamond or netherite armor
* added (partial) translations for
  * Chinese
  * Dutch
  * English
  * German
  * Norwegian
  * Persian
  * Polish
  * Spanish
  * Turkish
### Blocks & Items
* added craftable, non-functional steel ball
  * functionality will come with Spin in the future
* added Compact Disc (burn a light blue glass pane)
* added Spec Discs (Compact Disc + Experience Bottle)
* made Stand Disc recipe much cheaper (Compact Disc + Stand Arrowhead + 7 polished meteorite blocks)
* Discs as well as Cosplay now have their own tab in Creative Mode
* updated model of DIO and Jotaro Cosplay
* added Cosplay for
  * Johnny Joestar
  * Gyro Zeppeli
  * Pucci
  * Jotaro (from Part 4)
  * Heaven Attained DIO
  * Risotto
  * Kakyoin
  * Doppio
  * Diavolo
  * Giorno
  * Straizo
  * Funny Valentine
  * Diego
  * Ringo
  * Dio (from Part 1)
  * Kira (3 variants)
* Cosplay is now fire-resistant if made from netherite
* added animation for Stone Mask activation
### NPCs & Stands
* generalized bones of all stand models for future animation purposes
* C-Moon
  * Gravitational Hop move now configurable via datapacks
* D4C
  * updated model
* Horus
  * ice branches now turn much slower, but have consistent damage on hit
* __Mandom added__ (thanks to Xirc)
  * only has two moves: countdown and rewind
  * player can use spec moves while Mandom is out
  * countdown can last between 6 and 30 seconds
  * rewind can only be activated when countdown has been active for at least 6 seconds
  * countdown saves entities within a 64 block radius and rewind resets them to their state they had when countdown activated
  * mobs who move 200 blocks away from their countdown position escape rewind
* Metallica
  * given a unique, much quieter stand summoning sound
  * Invisibility move changed from crouch to aerial
* Purple Haze + Distortion
  * updated model
* Shadow The World
  * now has its own model
* Star Platinum
  * updated model
* Star Platinum The World
  * updated textures and animations
### Configs
* added exclusive stands
  * if `exclusiveStands` in the server config is set to `true`, no two players can have the same stand
  * OFF by default
* added stand user sight
  * if `standUserSight` in the server config is set to `true`, only players with stands can see other stands
  * OFF by default
* added health to damage scaling
  * if `healthToDamageScaling` in the server config to `true`, JCraft damage scale with the opponent's maximum health
  * ON by default
* added damage scaling factor against non stand users
  * `vsStandlessDamageMultiplier` in the server config which multiplies JCraft damage against standless __non-player__ opponents
  * 2x by default
* added stand spawner option
  * if `spawnerStands` in the server config is set to `true`, mobs spawned by spawners can have stands
  * ON by default
### Commands
* selection of Stands/Specs now are not of the form `HORUS` anymore, but `horus` or `jcraft:horus`
  * this is due to the use of the new registries
* added `jpose` command (e.g. `jpose help`)
* removed `jcraft:none` from `/spec set`, matching `/stand set`; use `/spec clear` instead
### Bug Fixes
* fixed being able to jump while stunned
* fixed Metallica animations
* fixed stands passively being immune to arrows
* fixed Coffin being in the wrong Creative Tab
* Cinderella's Kiss enchantment is now undiscoverable
* rain now freezes in Timestop
* fixed Stands hitting creative/spectator players
* spectators can no longer have stands out
* fixed D4C's Dimension Hop return in other dimensions teleporting you below bedrock
* fixed use of Stand arrows on some entities, including remotely active stands and Sheer Heart Attack
  * use tag `can_never_have_stand` to add living entities that shouldn't be able to have stands EVER
* fixed problems with mods AdAstra and CarryOn via new `stands` tag
### Known Bugs
* if exclusive stands are active, you can't change your stand skin via command
* Enemies can attack through dimensions (hit something -> D4C ult -> attacked by out-of-world ent)
  this bug is EVIL and has an unclear source >:(
  in the future, try looking at what sets the enemies target to null
  its obfuscated because of how goals are wrapped, but it's probably possible to figure out
  To add to this, the loading of chunks in D4Cs dimension should be done with tickets instead of a mass forceload
* Mobs don't spawn with stands in the Nether or End anymore; this is a stopgap solution to a bigger bug freezing the server
* using a SpecDisc on Vampire doesn't remove the blood bar
* using a SpecDisc on Anubis doesn't remove the bloodlust bar
## Forge
### Bug Fixes
* fixed commands not autocompleting correctly

## TODO (SOME UPDATE) :D
* Spin
* Throwing
* MR barrage fire :)
* Timestop should stop stand anims
* CRAZY DIAMOND, Hermit Purple, Yellow Temperance
* Stand NBT serialization
* Actually use effect keyframes in animations
