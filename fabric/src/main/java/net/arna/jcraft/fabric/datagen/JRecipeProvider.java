package net.arna.jcraft.fabric.datagen;

import net.arna.jcraft.JCraft;
import net.arna.jcraft.registry.JBlockRegistry;
import net.arna.jcraft.registry.JItemRegistry;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import java.util.function.Consumer;

public class JRecipeProvider extends FabricRecipeProvider {

    public JRecipeProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void buildRecipes(Consumer<FinishedRecipe> exporter) {
        // stellar iron ingot from smelting
        SimpleCookingRecipeBuilder.smelting(
                        Ingredient.of(JBlockRegistry.METEORITE_IRON_ORE_BLOCK.get()),
                        RecipeCategory.MISC,
                        JItemRegistry.STELLAR_IRON_INGOT.get(),
                        2f,
                        200)
                .unlockedBy("has_ore", InventoryChangeTrigger.TriggerInstance.hasItems(JBlockRegistry.METEORITE_IRON_ORE_BLOCK.get()))
                .save(exporter, JCraft.id("stellar_iron_ingot_from_smelting"));
        // stellar iron ingot from blasting
        SimpleCookingRecipeBuilder.blasting(
                        Ingredient.of(JBlockRegistry.METEORITE_IRON_ORE_BLOCK.get()),
                        RecipeCategory.MISC,
                        JItemRegistry.STELLAR_IRON_INGOT.get(),
                        2f,
                        100)
                .unlockedBy("has_ore", InventoryChangeTrigger.TriggerInstance.hasItems(JBlockRegistry.METEORITE_IRON_ORE_BLOCK.get()))
                .save(exporter, JCraft.id("stellar_iron_ingot_from_blasting"));
        // stellar iron ingot from block
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, JItemRegistry.STELLAR_IRON_INGOT.get(), 9)
                .requires(JBlockRegistry.STELLAR_IRON_BLOCK.get())
                .unlockedBy("has_block", InventoryChangeTrigger.TriggerInstance.hasItems(JBlockRegistry.STELLAR_IRON_BLOCK.get()))
                .save(exporter, JCraft.id("stellar_iron_ingot_from_block"));
        // stellar iron block from ingot
        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, JBlockRegistry.STELLAR_IRON_BLOCK.get())
                .requires(JItemRegistry.STELLAR_IRON_INGOT.get())
                .requires(JItemRegistry.STELLAR_IRON_INGOT.get())
                .requires(JItemRegistry.STELLAR_IRON_INGOT.get())
                .requires(JItemRegistry.STELLAR_IRON_INGOT.get())
                .requires(JItemRegistry.STELLAR_IRON_INGOT.get())
                .requires(JItemRegistry.STELLAR_IRON_INGOT.get())
                .requires(JItemRegistry.STELLAR_IRON_INGOT.get())
                .requires(JItemRegistry.STELLAR_IRON_INGOT.get())
                .requires(JItemRegistry.STELLAR_IRON_INGOT.get())
                .unlockedBy("has_ingot", InventoryChangeTrigger.TriggerInstance.hasItems(JItemRegistry.STELLAR_IRON_INGOT.get()))
                .save(exporter);
        // cinderella green from smelting
        SimpleCookingRecipeBuilder.smelting(
                        Ingredient.of(Items.VERDANT_FROGLIGHT),
                        RecipeCategory.BUILDING_BLOCKS,
                        JItemRegistry.CINDERELLA_GREEN_BLOCK.get(),
                        0.3f,
                        200)
                .unlockedBy("has_froglight", InventoryChangeTrigger.TriggerInstance.hasItems(Items.VERDANT_FROGLIGHT))
                .save(exporter, JCraft.id("cinderella_green_from_smelting"));
        // stand arrowhead
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, JItemRegistry.STAND_ARROWHEAD.get(), 3)
                .pattern("NGI")
                .pattern("GIG")
                .pattern(" GN")
                .define('G', Items.GOLD_INGOT)
                .define('I', JItemRegistry.STELLAR_IRON_INGOT.get())
                .define('N', Items.GOLD_NUGGET)
                .unlockedBy("has_ingot", InventoryChangeTrigger.TriggerInstance.hasItems(JItemRegistry.STELLAR_IRON_INGOT.get()))
                .save(exporter);
        // stand arrow
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, JItemRegistry.STAND_ARROW.get())
                .pattern("  A")
                .pattern(" S ")
                .pattern("F  ")
                .define('A', JItemRegistry.STAND_ARROWHEAD.get())
                .define('F', Items.FEATHER)
                .define('S', Items.STICK)
                .unlockedBy("has_arrowhead", InventoryChangeTrigger.TriggerInstance.hasItems(JItemRegistry.STAND_ARROWHEAD.get()))
                .save(exporter);
        // stand disk
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, JItemRegistry.STAND_DISC.get())
                .pattern("FFF")
                .pattern("FAF")
                .pattern("FFF")
                .define('A', JItemRegistry.STAND_ARROW.get())
                .define('F', Items.DISC_FRAGMENT_5)
                .unlockedBy("has_arrow", InventoryChangeTrigger.TriggerInstance.hasItems(JItemRegistry.STAND_ARROW.get()))
                .save(exporter);
        // sinner's soul
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, JItemRegistry.SINNERS_SOUL.get())
                .pattern("SSS")
                .pattern("SFS")
                .pattern("SSS")
                .define('F', Items.FERMENTED_SPIDER_EYE)
                .define('S', Items.SOUL_SAND)
                .unlockedBy("has_soul_sand", InventoryChangeTrigger.TriggerInstance.hasItems(Items.SOUL_SAND))
                .save(exporter);
        // sinner's soul from soul block
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, JItemRegistry.SINNERS_SOUL.get(), 9)
                .requires(JBlockRegistry.SOUL_BLOCK.get())
                .unlockedBy("has_soul_block", InventoryChangeTrigger.TriggerInstance.hasItems(JBlockRegistry.SOUL_BLOCK.get()))
                .save(exporter, JCraft.id("sinners_soul_from_soul_block"));
        // living arrow
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, JItemRegistry.LIVING_ARROW.get())
                .requires(JItemRegistry.STAND_ARROW.get())
                .requires(JItemRegistry.SINNERS_SOUL.get())
                .unlockedBy("has_arrow", InventoryChangeTrigger.TriggerInstance.hasItems(JItemRegistry.STAND_ARROW.get()))
                .unlockedBy("has_sinners_soul", InventoryChangeTrigger.TriggerInstance.hasItems(JItemRegistry.SINNERS_SOUL.get()))
                .save(exporter);
        // soul block
        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, JBlockRegistry.SOUL_BLOCK.get())
                .requires(JItemRegistry.SINNERS_SOUL.get())
                .requires(JItemRegistry.SINNERS_SOUL.get())
                .requires(JItemRegistry.SINNERS_SOUL.get())
                .requires(JItemRegistry.SINNERS_SOUL.get())
                .requires(JItemRegistry.SINNERS_SOUL.get())
                .requires(JItemRegistry.SINNERS_SOUL.get())
                .requires(JItemRegistry.SINNERS_SOUL.get())
                .requires(JItemRegistry.SINNERS_SOUL.get())
                .requires(JItemRegistry.SINNERS_SOUL.get())
                .unlockedBy("has_sinners_soul", InventoryChangeTrigger.TriggerInstance.hasItems(JItemRegistry.SINNERS_SOUL.get()))
                .save(exporter);
        // requiem ruby
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, JItemRegistry.REQUIEM_RUBY.get())
                .pattern("RDR")
                .pattern("ENE")
                .pattern("RDR")
                .define('D', Items.DIAMOND_BLOCK)
                .define('E', Items.EMERALD_BLOCK)
                .define('N', Items.NETHER_STAR)
                .define('R', Items.REDSTONE_BLOCK)
                .unlockedBy("has_nether_star", InventoryChangeTrigger.TriggerInstance.hasItems(Items.NETHER_STAR))
                .unlockedBy("has_redstone_block", InventoryChangeTrigger.TriggerInstance.hasItems(Items.REDSTONE_BLOCK))
                .save(exporter);
        // requiem arrow
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, JItemRegistry.REQUIEM_ARROW.get())
                .requires(JItemRegistry.STAND_ARROW.get())
                .requires(JItemRegistry.REQUIEM_RUBY.get())
                .requires(Items.TIPPED_ARROW)
                .unlockedBy("has_arrow", InventoryChangeTrigger.TriggerInstance.hasItems(JItemRegistry.STAND_ARROW.get()))
                .unlockedBy("has_ruby", InventoryChangeTrigger.TriggerInstance.hasItems(JItemRegistry.REQUIEM_RUBY.get()))
                .save(exporter);
        // coffin
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, JItemRegistry.COFFIN_BLOCK.get())
                .pattern("SSS")
                .pattern("SBS")
                .define('B', ItemTags.BEDS)
                .define('S', ItemTags.WOODEN_SLABS)
                .unlockedBy("has_black_bed", InventoryChangeTrigger.TriggerInstance.hasItems(Items.BLACK_BED))
                .unlockedBy("has_blue_bed", InventoryChangeTrigger.TriggerInstance.hasItems(Items.BLUE_BED))
                .unlockedBy("has_brown_bed", InventoryChangeTrigger.TriggerInstance.hasItems(Items.BROWN_BED))
                .unlockedBy("has_cyan_bed", InventoryChangeTrigger.TriggerInstance.hasItems(Items.CYAN_BED))
                .unlockedBy("has_gray_bed", InventoryChangeTrigger.TriggerInstance.hasItems(Items.GRAY_BED))
                .unlockedBy("has_green_bed", InventoryChangeTrigger.TriggerInstance.hasItems(Items.GREEN_BED))
                .unlockedBy("has_light_blue_bed", InventoryChangeTrigger.TriggerInstance.hasItems(Items.LIGHT_BLUE_BED))
                .unlockedBy("has_light_grey_bed", InventoryChangeTrigger.TriggerInstance.hasItems(Items.LIGHT_GRAY_BED))
                .unlockedBy("has_lime_bed", InventoryChangeTrigger.TriggerInstance.hasItems(Items.LIME_BED))
                .unlockedBy("has_magenta_bed", InventoryChangeTrigger.TriggerInstance.hasItems(Items.MAGENTA_BED))
                .unlockedBy("has_orange_bed", InventoryChangeTrigger.TriggerInstance.hasItems(Items.ORANGE_BED))
                .unlockedBy("has_pink_bed", InventoryChangeTrigger.TriggerInstance.hasItems(Items.PINK_BED))
                .unlockedBy("has_purple_bed", InventoryChangeTrigger.TriggerInstance.hasItems(Items.PURPLE_BED))
                .unlockedBy("has_red_bed", InventoryChangeTrigger.TriggerInstance.hasItems(Items.RED_BED))
                .unlockedBy("has_white_bed", InventoryChangeTrigger.TriggerInstance.hasItems(Items.WHITE_BED))
                .unlockedBy("has_yellow_bed", InventoryChangeTrigger.TriggerInstance.hasItems(Items.YELLOW_BED))
                .save(exporter);
        // Kars' headwrap
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, JItemRegistry.KARS_HEADWRAP.get())
                .pattern(" C ")
                .pattern("L L")
                .pattern(" B ")
                .define('B', Items.BLACK_DYE)
                .define('C', Items.LEATHER_HELMET)
                .define('L', Items.LEATHER)
                .unlockedBy("has_leather_helmet", InventoryChangeTrigger.TriggerInstance.hasItems(Items.LEATHER_HELMET))
                .save(exporter);
        // red hat
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, JItemRegistry.RED_HAT.get())
                .pattern(" R ")
                .pattern("LCL")
                .define('C', Items.LEATHER_HELMET)
                .define('L', Items.LEATHER)
                .define('R', Items.RED_DYE)
                .unlockedBy("has_leather_helmet", InventoryChangeTrigger.TriggerInstance.hasItems(Items.LEATHER_HELMET))
                .save(exporter);
        // blood bottle
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, JItemRegistry.BLOOD_BOTTLE.get())
                .pattern(" B ")
                .pattern(" G ")
                .pattern("GGG")
                .define('B', ItemTags.BUTTONS)
                .define('G', Items.GLASS)
                .unlockedBy("has_glass", InventoryChangeTrigger.TriggerInstance.hasItems(Items.GLASS))
                .save(exporter);
        // Jotaro's cap
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, JItemRegistry.JOTARO_CAP.get())
                .pattern("BYB")
                .pattern("BHB")
                .define('B', Items.BLACK_DYE)
                .define('H', Items.NETHERITE_HELMET)
                .define('Y', Items.YELLOW_DYE)
                .unlockedBy("has_netherite_helmet", InventoryChangeTrigger.TriggerInstance.hasItems(Items.NETHERITE_HELMET))
                .save(exporter);
        // Jotaro's jacket
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, JItemRegistry.JOTARO_JACKET.get())
                .pattern("B B")
                .pattern("BCB")
                .pattern("BBB")
                .define('B', Items.BLACK_DYE)
                .define('C', Items.NETHERITE_CHESTPLATE)
                .unlockedBy("has_netherite_chestplate", InventoryChangeTrigger.TriggerInstance.hasItems(Items.NETHERITE_CHESTPLATE))
                .save(exporter);
        // Jotaro's pants
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, JItemRegistry.JOTARO_PANTS.get())
                .pattern("YYY")
                .pattern("BLB")
                .pattern("B B")
                .define('B', Items.BLACK_DYE)
                .define('L', Items.NETHERITE_LEGGINGS)
                .define('Y', Items.YELLOW_DYE)
                .unlockedBy("has_netherite_leggings", InventoryChangeTrigger.TriggerInstance.hasItems(Items.NETHERITE_LEGGINGS))
                .save(exporter);
        // Jotaro's boots
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, JItemRegistry.JOTARO_BOOTS.get())
                .pattern("BNB")
                .pattern("B B")
                .define('B', Items.BLACK_DYE)
                .define('N', Items.NETHERITE_BOOTS)
                .unlockedBy("has_netherite_boots", InventoryChangeTrigger.TriggerInstance.hasItems(Items.NETHERITE_BOOTS))
                .save(exporter);
        // Dio's headband
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, JItemRegistry.DIO_HEADBAND.get())
                .pattern("GHG")
                .define('G', Items.GREEN_DYE)
                .define('H', Items.NETHERITE_HELMET)
                .unlockedBy("has_netherite_helmet", InventoryChangeTrigger.TriggerInstance.hasItems(Items.NETHERITE_HELMET))
                .save(exporter);
        // Dio's jacket
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, JItemRegistry.DIO_JACKET.get())
                .pattern("Y Y")
                .pattern("YCY")
                .pattern("YBY")
                .define('B', Items.BLACK_DYE)
                .define('C', Items.NETHERITE_CHESTPLATE)
                .define('Y', Items.YELLOW_DYE)
                .unlockedBy("has_netherite_chestplate", InventoryChangeTrigger.TriggerInstance.hasItems(Items.NETHERITE_CHESTPLATE))
                .save(exporter);
        // Dio's cape
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, JItemRegistry.DIO_CAPE.get())
                .pattern("RLR")
                .pattern("LCL")
                .pattern("LLL")
                .define('C', Items.NETHERITE_CHESTPLATE)
                .define('L', Items.LEATHER)
                .define('R', Items.RED_DYE)
                .unlockedBy("has_netherite_chestplate", InventoryChangeTrigger.TriggerInstance.hasItems(Items.NETHERITE_CHESTPLATE))
                .save(exporter);
        // Dio's pants
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, JItemRegistry.DIO_PANTS.get())
                .pattern("GGG")
                .pattern("YLY")
                .pattern("Y Y")
                .define('G', Items.GREEN_DYE)
                .define('L', Items.NETHERITE_LEGGINGS)
                .define('Y', Items.YELLOW_DYE)
                .unlockedBy("has_netherite_leggings", InventoryChangeTrigger.TriggerInstance.hasItems(Items.NETHERITE_LEGGINGS))
                .save(exporter);
        // Dio's boots
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, JItemRegistry.DIO_BOOTS.get())
                .pattern("YBY")
                .pattern("Y Y")
                .define('B', Items.NETHERITE_BOOTS)
                .define('Y', Items.YELLOW_DYE)
                .unlockedBy("has_netherite_boots", InventoryChangeTrigger.TriggerInstance.hasItems(Items.NETHERITE_BOOTS))
                .save(exporter);
        // Dio's diary
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, JItemRegistry.DIOS_DIARY.get())
                .requires(Items.WAXED_OXIDIZED_COPPER)
                .requires(Items.GOLD_BLOCK)
                .requires(Items.NETHERITE_BLOCK)
                .requires(Items.EXPERIENCE_BOTTLE)
                .requires(Items.NETHER_STAR)
                .requires(Items.ELYTRA)
                .requires(Items.LINGERING_POTION)
                .requires(Items.WRITABLE_BOOK)
                .requires(Items.EMERALD_BLOCK)
                .unlockedBy("has_nether_star", InventoryChangeTrigger.TriggerInstance.hasItems(Items.NETHER_STAR))
                .unlockedBy("has_book", InventoryChangeTrigger.TriggerInstance.hasItems(Items.WRITABLE_BOOK))
                .save(exporter);
        // hot sand
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, JBlockRegistry.HOT_SAND_BLOCK.get(), 8)
                .pattern("SSS")
                .pattern("SMS")
                .pattern("SSS")
                .define('M', Items.MAGMA_BLOCK)
                .define('S', Items.SAND)
                .unlockedBy("has_sand", InventoryChangeTrigger.TriggerInstance.hasItems(Items.SAND))
                .unlockedBy("has_magma_block", InventoryChangeTrigger.TriggerInstance.hasItems(Items.MAGMA_BLOCK))
                .save(exporter);
        // Anubis sheathed
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, JItemRegistry.ANUBIS_SHEATHED.get())
                .pattern("LSI")
                .pattern("SDS")
                .pattern("GSL")
                .define('D', Items.DIAMOND)
                .define('G', Items.GOLD_BLOCK)
                .define('L', Items.LEATHER)
                .define('I', Items.IRON_BLOCK)
                .define('S', JBlockRegistry.SOUL_BLOCK.get())
                .unlockedBy("has_diamond", InventoryChangeTrigger.TriggerInstance.hasItems(Items.DIAMOND))
                .unlockedBy("has_soul_block", InventoryChangeTrigger.TriggerInstance.hasItems(JBlockRegistry.SOUL_BLOCK.get()))
                .save(exporter);
        // boxing gloves
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, JItemRegistry.BOXING_GLOVES.get())
                .pattern("LLR")
                .pattern("SLL")
                .pattern(" SL")
                .define('L', Items.LEATHER)
                .define('R', Items.RED_DYE)
                .define('S', Items.STRING)
                .unlockedBy("has_leather", InventoryChangeTrigger.TriggerInstance.hasItems(Items.LEATHER))
                .save(exporter);
        // coin to nuggets
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.GOLD_NUGGET, 2)
                .requires(JItemRegistry.KQ_COIN.get())
                .unlockedBy("has_coin", InventoryChangeTrigger.TriggerInstance.hasItems(JItemRegistry.KQ_COIN.get()))
                .save(exporter);
        // nuggets to coin
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, JItemRegistry.KQ_COIN.get())
                .requires(Items.GOLD_NUGGET)
                .requires(Items.GOLD_NUGGET)
                .unlockedBy("has_nugget", InventoryChangeTrigger.TriggerInstance.hasItems(Items.GOLD_NUGGET))
                .save(exporter);
        // green baby
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, JItemRegistry.GREEN_BABY.get())
                .pattern("GBG")
                .pattern("SMS")
                .pattern("GBG")
                .define('B', Items.BONE_BLOCK)
                .define('G', Items.GREEN_DYE)
                .define('M', Items.TOTEM_OF_UNDYING)
                .define('S', JBlockRegistry.SOUL_BLOCK.get())
                .unlockedBy("has_totem", InventoryChangeTrigger.TriggerInstance.hasItems(Items.TOTEM_OF_UNDYING))
                .save(exporter);
        // knife
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, JItemRegistry.KNIFE.get())
                .pattern("  N")
                .pattern(" I ")
                .pattern("S  ")
                .define('I', Items.IRON_INGOT)
                .define('N', Items.IRON_NUGGET)
                .define('S', Items.STICK)
                .unlockedBy("has_ingot", InventoryChangeTrigger.TriggerInstance.hasItems(Items.IRON_INGOT))
                .save(exporter);
        // knife bundle
        ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, JItemRegistry.KNIFEBUNDLE.get())
                .requires(JItemRegistry.KNIFE.get())
                .requires(JItemRegistry.KNIFE.get())
                .requires(JItemRegistry.KNIFE.get())
                .requires(JItemRegistry.KNIFE.get())
                .requires(JItemRegistry.KNIFE.get())
                .requires(JItemRegistry.KNIFE.get())
                .requires(JItemRegistry.KNIFE.get())
                .requires(JItemRegistry.KNIFE.get())
                .requires(JItemRegistry.KNIFE.get())
                .unlockedBy("has_knife", InventoryChangeTrigger.TriggerInstance.hasItems(JItemRegistry.KNIFE.get()))
                .save(exporter);
    }
}