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

## Adding an Item
1. Add the item to `JItemRegistry`.
2. Add an English translation of the block _and_ its item variant to `en_us.json`.
3. Add the model to `JModelProvider`.
4. If item tags apply, add them in `JTagProviders.JItemTags` as well. 
5. If the item is involved in recipes, add them to `JRecipeProvider`. 
6. If getting the item is worth an advancement, add it to `JAdvancementProvider`. 
7. Run datagen (on fabric). 
8. Add the item to `JCreativeMenuTabRegistry` to the JCraft tab and possibly to other tabs as well. 
9. Add the texture(s) for the block in `textures/item`. 
10. Test your addition.