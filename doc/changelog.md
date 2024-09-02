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
* added Green Cindarella Block (based on Green Terracotta)
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
#### Generation
* meteorites can now also be found in Ice Spikes Biome
* added Devil's Palm Biome
#### NPCs & Stands
* added Petshop, Aya Tsuji and the D'Arby brothers as NPCs (currently unobtainable except via spawn eggs) and added their stands (Horus, Cinderella, Osiris and Atum) as well, with only Horus being obtainable
* added Silver Chariot Requiem (unobtainable)
* added custom icons for Hierophant Green and Purple Haze
##### Magician's Red
* Crossfire cooldown lowered (12 -> 5s)
#### Miscellaneous
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
#### General
* Timestop doesnt stop animated textures


* Water transparency seemingly changes when the player hovers over a block (appears to have disappeared)


* KC epitaph models have fucked rendering


* Enemies can attack through dimensions (hit something -> D4C ult -> attacked by invisible ent)
* To add to this, the loading of chunks in D4Cs dimension should be done with tickets instead of a mass forceload
* Also, there should be a semi-active check for people inside the D4C dimension that arent logged in PastDimensions


* Stand User Enemies dont walk towards you (due to being ridden by stands?)


* GravityLib rendering breaks base-game animations
  * MR's Red Bind particles dont render in the right orientation when under different gravities


* Horus is not fully implemented


* Getting stand skins is no longer possible due to Smithing Table rework


* Sound Keyframe found for SilverChariotEntity -> controller, but no keyframe handler registered
  Particle Keyframe found for TheWorldEntity -> controller, but no keyframe handler registered
#### Forge
* Camera is fucked in custom gravity


* Stand users rapidly resummon, OCCASIONALLY
* Stand users have their stand type set to null sometimes (bandaid fix added but cause should be found)


* KC epitaph overlay doesn't render
`  [16:27:24] [Netty Local Client IO #0/ERROR] [de.ar.ne.fo.NetworkManagerImpl/]: Unknown message ID: jcraft:epth`
* KC Time Erase renders better than fabric, though it doesn't play the exit noise


* changing the velocity of a projectile doesn't mark it as dirty which causes teleports for shit like Magician's Special 1 into crouching m1 (redirect)

#### todo :)
* Internationalization
