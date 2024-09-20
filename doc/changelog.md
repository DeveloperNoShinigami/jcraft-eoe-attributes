# Changelog

## Newest Version
* updated to MC 1.20.1
* <u>_Forge is now supported_</u>
### Dependencies
* added Architectury to support Forge as well as Fabric
* added Terrablender to support custom biomes
* changed Geckolib to Azurelib
* added Trimmed as included (i.e. you don't need to download it) to support custom armor trims
### General Changes
#### Blocks & Items
* added Polished Meteorite Block
* added Hot Sand (like Sand and Magma combined)
* added Stellar Iron Block (crafted from Stellar Iron Ingot)
* added Green Cinderella Block (based on Green Terracotta)
* added Soul Wood (currently unobtainable)
* added Pucci's hat (protects from the sun)
* added Prison Key, Plankton Vial, Shiv (currently unobtainable)
* Sinner's Block can now be crafted back into Sinner's Souls
* Sinner's Block now activates Soul Speed and supports Soul Fire
* Stellar Iron Ingot can now be used as beacon payment
* Stellar Iron Ingot can now be used as armor trim
* using the stand arrow now damages the user based on difficulty level (damage amount is a Game Rule)
* improved texture and recipe for Green Baby
* DIO's Diary can now be put into chiseled bookshelves
* Skins now transfer between evolutions
* Cinderella's Masks are now applied in the anvil
#### Generation
* meteorites can now also be found in Ice Spikes Biome
* added Devil's Palm Biome
### NPCs & Stands
* **added Horus, Shadow The World, and Metallica**
* added Petshop, Aya Tsuji and the D'Arby brothers as NPCs (currently unobtainable except via spawn eggs) and added their stands (Horus, Cinderella, Osiris and Atum) as well, with only Horus being obtainable
* added Silver Chariot Requiem (unobtainable)
* added custom icons for Hierophant Green and Purple Haze (& Distortion)
* Updated models for Whitesnake, C-Moon, Made in Heaven, Cream
#### Magician's Red
* Crossfire cooldown lowered (12 -> 5s)
### Miscellaneous
* added /jcraft help
* added /framedata [stand/spec]
* added client config options for precisely moving the JCraft HUD
* improved Prediction client config
* reordered Creative Inventory Tab
* additionally sorted JCraft items into Vanilla Creative Inventory Tabs
* Recipe Advancements added for (almost) all recipes
### Technical Changes
* updated to MC 1.20.1
* Forge is now supported
* mobs with stands are now given by the `jcraft:can_have_stand` tag
* Warden cannot be stunned by stands anymore via `jcraft:cannot_be_stunned` tag
* Sun protection helmet items are now recognized via `jcraft:protects_from_sun` tag
* lots of assets/data have been moved to datagen
* lots of refactoring regarding stands
* made more strings translatable
* added docs for future technical changes
### Bug Fixes
* Bug: Inventory disappeared instead of dropped when killed while being stunned




### Known Bugs
#### Common
* Timestop doesn't stop animated textures


* Enemies can attack through dimensions (hit something -> D4C ult -> attacked by out-of-world ent)
* this bug is EVIL and has an unclear source >:(
* in the future, try looking at what sets the enemies target to null
* its obfuscated because of how goals are wrapped, but its probably possible to figure out
* To add to this, the loading of chunks in D4Cs dimension should be done with tickets instead of a mass forceload
#### Prod (i.e. fuck your devenv)
* The Sun scales up too much
  (PLANET)
# Bug List
#### Forge
* The client doesn't register The Sun as it's stand if moved out of and into render distance
* in Fabric, it simply unloads when out of sight. This behavior is preferable.


#### todo (NOT THIS UPDATE) :)
* Fix attacks not being able to trade - because ticking isn't done in parallel, the first entity to be ticked
    will have priority when it comes to attack calculations, even though both executed an attack at the same time.
    A possible solution to this is to just queue up all the move.tick() calls and run them all at the very end of a server tick.

* Hermit Purple, Yellow Temperance
* Internationalization
* Actually use effect keyframes in animations