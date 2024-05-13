# Adding stuff
## Adding a Block
1. Add the block to `JBlockRegistry`.
2. Add its item variant to `JItemRegistry` using the same ID.
3. Add an English translation of the block _and_ its item variant to `en_us.json`.
4. Add the model to `JModelProvider`.
5. If the block drops, add it to `JLootTableProviders.BlockLoot`.
6. If the block mining requires a specific tool or mining level, add these to `JTagProviders.JBlockTags`.
7. If additional block or item tags apply, add them in `JTagProviders` as well.
8. If the block is involved in recipes, add them to `JRecipeProvider`.
9. If getting the block is worth an advancement, add it to `JAdvancementProvider`.
10. Run datagen (on fabric).
11. Add the block to `JCreativeMenuTabRegistry` to the JCraft tab and possibly to other tabs as well.
12. Add the texture(s) for the block in `textures/block`.
13. Test your addition.

## Adding a Spawn Egg
1. Add the spawn egg with its two colors to `JItemRegistry`.
2. Add an English translation of the spawn egg to `en_us.json`.
3. Add the model to `JModelProvider`.
4. Run datagen.
5. Add the spawn egg to `JCreativeMenuTabRegistry` to the JCraft tab and the spawn egg tabs as well.
6. Test your addition.

## Adding an Item
1. Add the item to `JItemRegistry`.
2. Add an English translation of the item to `en_us.json`.
3. Add the model to `JModelProvider`.
4. If item tags apply, add them in `JTagProviders.JItemTags` as well. 
5. If the item is involved in recipes, add them to `JRecipeProvider`. 
6. If getting the item is worth an advancement, add it to `JAdvancementProvider`. 
7. Run datagen (on fabric). 
8. Add the item to `JCreativeMenuTabRegistry` to the JCraft tab and possibly to other tabs as well. 
9. Add the texture(s) for the block in `textures/item`. 
10. Test your addition.

## Adding an Entity (Type)
1. Create the class `MyEntity` (replacing `My` with its name of course), subclassing `Entity` or one of its subclasses (like `PathAwareEntity`).
2. Add the interface `GeoEntity` to `MyEntity`.
3. Add the line `private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);` to `MyEntity` and implement the interface getter accordingly.
4. Add the method `public static DefaultAttributeContainer.Builder createMyAttributes()` to `MyEntity` and return the stats for your entity, e.g. use `MobEntity.createMobAttributes()`.
5. Add a constructor that only takes a `World` as a parameter. Ignore the missing type for the `super` call right now.
6. Add the type of the entity to `JEntityTypeRegistry`. The dimension parameter is the hitbox of your entity in blocks.
7. Register your entity attributes in `JEntityTypeRegistry#registerAttributes`.
8. Use the newly created entity type in the `super` constructor of `MyEntity`.
9. Create the class `MyModel` extending `GeoModel`. Model resource is `JCraft.id("geo/my.geo.json")` and texture resource is `JCraft.id("textures/entity/my.png")`.
10. Create the class `MyRenderer` extending `GeoEntityRenderer`.
11. Add `MyRenderer` to `JEntityRendererRegister`.
12. Add `my.png`, `my.geo.json` and `my.animation.json` from the `my.bbmodel` file from our modelers.
13. Maybe add a spawn egg (see above how).
14. Add an English translation of the entity to `en_us.json`.
15. If the entity can have stands, add the line `JEnemies.add(this);` to the constructor of `MyEntity`.
16. Test your addition.