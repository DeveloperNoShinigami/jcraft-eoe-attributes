# Changelog
## General
* made Stand data, Spec data and advancements translatable
* added a light-on-fire action
* added (partial) translations for
  * French
  * Japanese
  * Russian
### Blocks & Items
* added 29 music discs in their own creative tab
  * Taku Iwasaki - awake
  * Coda - Bloody Stream
  * batta - chase
  * THE DU - Crazy Noisy Bizarre Town
  * Army of Lovers - Crucified
  * Yugo Kanno - Dark Rebirth
  * JO☆STARS - Sono Chi no Kioku ~end of THE WORLD~
  * Coda - Fighting Gold
  * Jodeci - Freek'n You
  * Yugo Kanno - il vento d'oro 
  * Karen Aoki & Daisuke Hasegawa - Great Days
  * George Frideric Handel - Hallelujah Chorus
  * sana (Sajou no Hana) - Heaven's falling down
  * Savage Garden - I Want You
  * Yugo Kanno - Theme of Stone Ocean
  * Hayato Matsuo - Fukutsu Mushinno Sakebi
  * Yugo Kanno - Stardust Crusaders
  * Yugo Kanno - Kira's Theme
  * Enigma - Modern Crusaders
  * Oingo & Boingo - Akuyaku◇Kyōsōkyoku
  * TOMMY - Sono Chi no Sadame
  * Jin Hoshimoto - STAND PROUD
  * ichigo - Stone Ocean
  * Yes - To Be Continued
  * Yugo Kanno - canzoni preferite
  * Daisuke Hasegawa - Traitors' Requiem
  * Yugo Kanno - un'altra persona
  * The Bangles - Walk Like an Egyptian
  * Elvis Presley - Wonder of You
* added Training Dummy with damage indicators
  * cannot be destroyed, can only be picked up with both hands empty
  * damage indicator colors can be changed in client config
  * damage indicator display range can be changed in server config
### NPCs & Stands
* …
### Configs
* …
### Commands
* …
### Bug Fixes
* fixed the exclusive stands bug
  * you should now be able to change your Stand skin when the option is enabled
* fixed the Iron's Spellbooks incompatibility
* fixed recipe for Valentine's pants
* fixed TW rotation bug
* fixed minor Road Roller UV issue
* fixed LifeGiver taking items from creative hotbar
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
