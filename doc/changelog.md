# Changelog
## General
* made Stand data, Spec data and advancements translatable
* added a light-on-fire action
* added (partial) translations for
  * German
  * Spanish
  * Persian
  * French
  * Japanese
  * Korean
  * Dutch
  * Norwegian
  * Polish
  * Russian
  * Turkish
  * Ukrainian
  * Chinese
* overhauled the attack system
  * most attacks have lower or even no cooldown anymore
  * Cooldown Cancel is only accessible in creative now
  * lots of
* updated internal Azurelib version, which should fix some rendering errors
### Blocks & Items
* added Training Dummy with damage indicators
  * cannot be destroyed, can only be picked up with both hands empty
  * damage indicator colors can be changed in client config
  * damage indicator display range can be changed in server config
  * like armor stand, can be equipped with a lobotomized Stand
* added Vampire Spawn Egg
### NPCs & Stands
* updated JCraft AI
  * now supports Specs on mobs that can wield them
  * approaches combat differently, doing far less inhumanly quick blocking
* added the Vampire, an example Spec user mob
  * currently not findable in Survival, but will be added shortly
  * can have its spec changed
* added attack icons for Horus
### Configs
* Cream can be disabled to void the items it destroys in server config
* added "Base AI Level" config option, which defaults to 15 (competitive level - knows all the game mechanics)
### Commands
* added `/cooldown cancel`, `/cooldown reset`, `/cdc` - all of which serve to reset the JCraft cooldowns of an entity
### Bug Fixes
* fixed game freezing issue and ree-nabled Stand user mobs in the nether
* fixed using Spec discs not updating HUD bars
* fixed crafting for Diego's Hat
* fixed the exclusive stands bug
  * you should now be able to change your Stand skin when the option is enabled
* fixed infinite flight bug with Cream
* fixed the Iron's Spellbooks incompatibility
* fixed recipe for Valentine's pants
* fixed TW rotation bug
* fixed minor Road Roller UV issue
* fixed LifeGiver taking items from creative hotbar
* fixed HG tentacles activating on creative or spectator player
* fixed KC being able to create permanent Time Erase clones
* fixed permanent client Timestop bug
### Known Bugs
* Enemies can attack through dimensions (hit something -> D4C ult -> attacked by out-of-world ent)

## TODO (SOME UPDATE) :D
* Spin
* Throwing
* MR barrage fire :)
* Timestop should stop stand anims
* CRAZY DIAMOND, Hermit Purple, Yellow Temperance
* Stand NBT serialization
* Actually use effect keyframes in animations
