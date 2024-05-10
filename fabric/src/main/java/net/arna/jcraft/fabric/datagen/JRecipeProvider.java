package net.arna.jcraft.fabric.datagen;

import net.arna.jcraft.JCraft;
import net.arna.jcraft.registry.JBlockRegistry;
import net.arna.jcraft.registry.JItemRegistry;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.data.server.recipe.CookingRecipeJsonBuilder;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.tag.ItemTags;

import java.util.function.Consumer;

public class JRecipeProvider extends FabricRecipeProvider {

    public JRecipeProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generate(Consumer<RecipeJsonProvider> exporter) {
        // stellar iron ingot from smelting
        CookingRecipeJsonBuilder.createSmelting(
                        Ingredient.ofItems(JBlockRegistry.METEORITE_IRON_ORE_BLOCK.get()),
                        RecipeCategory.MISC,
                        JItemRegistry.STELLAR_IRON_INGOT.get(),
                        2f,
                        200)
                .criterion("has_ore", InventoryChangedCriterion.Conditions.items(JBlockRegistry.METEORITE_IRON_ORE_BLOCK.get()))
                .offerTo(exporter, JCraft.MOD_ID + ":stellar_iron_ingot_from_smelting");
        // stellar iron ingot from blasting
        CookingRecipeJsonBuilder.createBlasting(
                        Ingredient.ofItems(JBlockRegistry.METEORITE_IRON_ORE_BLOCK.get()),
                        RecipeCategory.MISC,
                        JItemRegistry.STELLAR_IRON_INGOT.get(),
                        2f,
                        100)
                .criterion("has_ore", InventoryChangedCriterion.Conditions.items(JBlockRegistry.METEORITE_IRON_ORE_BLOCK.get()))
                .offerTo(exporter, JCraft.MOD_ID + ":stellar_iron_ingot_from_blasting");
        // stand arrowhead
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, JItemRegistry.STAND_ARROWHEAD.get(), 3)
                .pattern("NGI")
                .pattern("GIG")
                .pattern(" GN")
                .input('G', Items.GOLD_INGOT)
                .input('I', JItemRegistry.STELLAR_IRON_INGOT.get())
                .input('N', Items.GOLD_NUGGET)
                .criterion("has_ingot", InventoryChangedCriterion.Conditions.items(JItemRegistry.STELLAR_IRON_INGOT.get()))
                .offerTo(exporter);
        // stand arrow
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, JItemRegistry.STAND_ARROW.get())
                .pattern("  A")
                .pattern(" S ")
                .pattern("F  ")
                .input('A', JItemRegistry.STAND_ARROWHEAD.get())
                .input('F', Items.FEATHER)
                .input('S', Items.STICK)
                .criterion("has_arrowhead", InventoryChangedCriterion.Conditions.items(JItemRegistry.STAND_ARROWHEAD.get()))
                .offerTo(exporter);
        // stand disk
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, JItemRegistry.STAND_DISC.get())
                .pattern("FFF")
                .pattern("FAF")
                .pattern("FFF")
                .input('A', JItemRegistry.STAND_ARROW.get())
                .input('F', Items.DISC_FRAGMENT_5)
                .criterion("has_arrow", InventoryChangedCriterion.Conditions.items(JItemRegistry.STAND_ARROW.get()))
                .offerTo(exporter);
        // sinner's soul
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, JItemRegistry.SINNERS_SOUL.get())
                .pattern("SSS")
                .pattern("SFS")
                .pattern("SSS")
                .input('F', Items.FERMENTED_SPIDER_EYE)
                .input('S', Items.SOUL_SAND)
                .criterion("has_soul_sand", InventoryChangedCriterion.Conditions.items(Items.SOUL_SAND))
                .offerTo(exporter);
        // living arrow
        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, JItemRegistry.LIVING_ARROW.get())
                .input(JItemRegistry.STAND_ARROW.get())
                .input(JItemRegistry.SINNERS_SOUL.get())
                .criterion("has_arrow", InventoryChangedCriterion.Conditions.items(JItemRegistry.STAND_ARROW.get()))
                .criterion("has_sinners_soul", InventoryChangedCriterion.Conditions.items(JItemRegistry.SINNERS_SOUL.get()))
                .offerTo(exporter);
        // soul block
        ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, JBlockRegistry.SOUL_BLOCK.get())
                .input(JItemRegistry.SINNERS_SOUL.get())
                .input(JItemRegistry.SINNERS_SOUL.get())
                .input(JItemRegistry.SINNERS_SOUL.get())
                .input(JItemRegistry.SINNERS_SOUL.get())
                .input(JItemRegistry.SINNERS_SOUL.get())
                .input(JItemRegistry.SINNERS_SOUL.get())
                .input(JItemRegistry.SINNERS_SOUL.get())
                .input(JItemRegistry.SINNERS_SOUL.get())
                .input(JItemRegistry.SINNERS_SOUL.get())
                .criterion("has_sinners_soul", InventoryChangedCriterion.Conditions.items(JItemRegistry.SINNERS_SOUL.get()))
                .offerTo(exporter);
        // requiem ruby
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, JItemRegistry.REQUIEM_RUBY.get())
                .pattern("RDR")
                .pattern("ENE")
                .pattern("RDR")
                .input('D', Items.DIAMOND_BLOCK)
                .input('E', Items.EMERALD_BLOCK)
                .input('N', Items.NETHER_STAR)
                .input('R', Items.REDSTONE_BLOCK)
                .criterion("has_nether_star", InventoryChangedCriterion.Conditions.items(Items.NETHER_STAR))
                .criterion("has_redstone_block", InventoryChangedCriterion.Conditions.items(Items.REDSTONE_BLOCK))
                .offerTo(exporter);
        // requiem arrow
        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, JItemRegistry.REQUIEM_ARROW.get())
                .input(JItemRegistry.STAND_ARROW.get())
                .input(JItemRegistry.REQUIEM_RUBY.get())
                .input(Items.TIPPED_ARROW)
                .criterion("has_arrow", InventoryChangedCriterion.Conditions.items(JItemRegistry.STAND_ARROW.get()))
                .criterion("has_ruby", InventoryChangedCriterion.Conditions.items(JItemRegistry.REQUIEM_RUBY.get()))
                .offerTo(exporter);
        // coffin
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, JItemRegistry.COFFIN_BLOCK.get())
                .pattern("SSS")
                .pattern("SBS")
                .input('B', ItemTags.BEDS)
                .input('S', ItemTags.WOODEN_SLABS)
                .criterion("has_black_bed", InventoryChangedCriterion.Conditions.items(Items.BLACK_BED))
                .criterion("has_blue_bed", InventoryChangedCriterion.Conditions.items(Items.BLUE_BED))
                .criterion("has_brown_bed", InventoryChangedCriterion.Conditions.items(Items.BROWN_BED))
                .criterion("has_cyan_bed", InventoryChangedCriterion.Conditions.items(Items.CYAN_BED))
                .criterion("has_gray_bed", InventoryChangedCriterion.Conditions.items(Items.GRAY_BED))
                .criterion("has_green_bed", InventoryChangedCriterion.Conditions.items(Items.GREEN_BED))
                .criterion("has_light_blue_bed", InventoryChangedCriterion.Conditions.items(Items.LIGHT_BLUE_BED))
                .criterion("has_light_grey_bed", InventoryChangedCriterion.Conditions.items(Items.LIGHT_GRAY_BED))
                .criterion("has_lime_bed", InventoryChangedCriterion.Conditions.items(Items.LIME_BED))
                .criterion("has_magenta_bed", InventoryChangedCriterion.Conditions.items(Items.MAGENTA_BED))
                .criterion("has_orange_bed", InventoryChangedCriterion.Conditions.items(Items.ORANGE_BED))
                .criterion("has_pink_bed", InventoryChangedCriterion.Conditions.items(Items.PINK_BED))
                .criterion("has_purple_bed", InventoryChangedCriterion.Conditions.items(Items.PURPLE_BED))
                .criterion("has_red_bed", InventoryChangedCriterion.Conditions.items(Items.RED_BED))
                .criterion("has_white_bed", InventoryChangedCriterion.Conditions.items(Items.WHITE_BED))
                .criterion("has_yellow_bed", InventoryChangedCriterion.Conditions.items(Items.YELLOW_BED))
                .offerTo(exporter);
        // Kars' headwrap
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, JItemRegistry.KARS_HEADWRAP.get())
                .pattern(" C ")
                .pattern("L L")
                .pattern(" B ")
                .input('B', Items.BLACK_DYE)
                .input('C', Items.LEATHER_HELMET)
                .input('L', Items.LEATHER)
                .criterion("has_leather_helmet", InventoryChangedCriterion.Conditions.items(Items.LEATHER_HELMET))
                .offerTo(exporter);
        // red hat
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, JItemRegistry.RED_HAT.get())
                .pattern(" R ")
                .pattern("LCL")
                .input('C', Items.LEATHER_HELMET)
                .input('L', Items.LEATHER)
                .input('R', Items.RED_DYE)
                .criterion("has_leather_helmet", InventoryChangedCriterion.Conditions.items(Items.LEATHER_HELMET))
                .offerTo(exporter);
        // blood bottle
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, JItemRegistry.BLOOD_BOTTLE.get())
                .pattern(" B ")
                .pattern(" G ")
                .pattern("GGG")
                .input('B', ItemTags.BUTTONS)
                .input('G', Items.GLASS)
                .criterion("has_glass", InventoryChangedCriterion.Conditions.items(Items.GLASS))
                .offerTo(exporter);
        // Jotaro's cap
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, JItemRegistry.JOTARO_CAP.get())
                .pattern("BYB")
                .pattern("BHB")
                .input('B', Items.BLACK_DYE)
                .input('H', Items.NETHERITE_HELMET)
                .input('Y', Items.YELLOW_DYE)
                .criterion("has_netherite_helmet", InventoryChangedCriterion.Conditions.items(Items.NETHERITE_HELMET))
                .offerTo(exporter);
        // Jotaro's jacket
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, JItemRegistry.JOTARO_JACKET.get())
                .pattern("B B")
                .pattern("BCB")
                .pattern("BBB")
                .input('B', Items.BLACK_DYE)
                .input('C', Items.NETHERITE_CHESTPLATE)
                .criterion("has_netherite_chestplate", InventoryChangedCriterion.Conditions.items(Items.NETHERITE_CHESTPLATE))
                .offerTo(exporter);
        // Jotaro's pants
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, JItemRegistry.JOTARO_PANTS.get())
                .pattern("YYY")
                .pattern("BLB")
                .pattern("B B")
                .input('B', Items.BLACK_DYE)
                .input('L', Items.NETHERITE_LEGGINGS)
                .input('Y', Items.YELLOW_DYE)
                .criterion("has_netherite_leggings", InventoryChangedCriterion.Conditions.items(Items.NETHERITE_LEGGINGS))
                .offerTo(exporter);
        // Jotaro's boots
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, JItemRegistry.JOTARO_BOOTS.get())
                .pattern("BNB")
                .pattern("B B")
                .input('B', Items.BLACK_DYE)
                .input('N', Items.NETHERITE_BOOTS)
                .criterion("has_netherite_boots", InventoryChangedCriterion.Conditions.items(Items.NETHERITE_BOOTS))
                .offerTo(exporter);
        // Dio's headband
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, JItemRegistry.DIO_HEADBAND.get())
                .pattern("GHG")
                .input('G', Items.GREEN_DYE)
                .input('H', Items.NETHERITE_HELMET)
                .criterion("has_netherite_helmet", InventoryChangedCriterion.Conditions.items(Items.NETHERITE_HELMET))
                .offerTo(exporter);
        // Dio's jacket
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, JItemRegistry.DIO_JACKET.get())
                .pattern("Y Y")
                .pattern("YCY")
                .pattern("YBY")
                .input('B', Items.BLACK_DYE)
                .input('C', Items.NETHERITE_CHESTPLATE)
                .input('Y', Items.YELLOW_DYE)
                .criterion("has_netherite_chestplate", InventoryChangedCriterion.Conditions.items(Items.NETHERITE_CHESTPLATE))
                .offerTo(exporter);
        // Dio's cape
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, JItemRegistry.DIO_CAPE.get())
                .pattern("RLR")
                .pattern("LCL")
                .pattern("LLL")
                .input('C', Items.NETHERITE_CHESTPLATE)
                .input('L', Items.LEATHER)
                .input('R', Items.RED_DYE)
                .criterion("has_netherite_chestplate", InventoryChangedCriterion.Conditions.items(Items.NETHERITE_CHESTPLATE))
                .offerTo(exporter);
        // Dio's pants
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, JItemRegistry.DIO_PANTS.get())
                .pattern("GGG")
                .pattern("YLY")
                .pattern("Y Y")
                .input('G', Items.GREEN_DYE)
                .input('L', Items.NETHERITE_LEGGINGS)
                .input('Y', Items.YELLOW_DYE)
                .criterion("has_netherite_leggings", InventoryChangedCriterion.Conditions.items(Items.NETHERITE_LEGGINGS))
                .offerTo(exporter);
        // Dio's boots
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, JItemRegistry.DIO_BOOTS.get())
                .pattern("YBY")
                .pattern("Y Y")
                .input('B', Items.NETHERITE_BOOTS)
                .input('Y', Items.YELLOW_DYE)
                .criterion("has_netherite_boots", InventoryChangedCriterion.Conditions.items(Items.NETHERITE_BOOTS))
                .offerTo(exporter);
        // Dio's diary
        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, JItemRegistry.DIOS_DIARY.get())
                .input(Items.WAXED_OXIDIZED_COPPER)
                .input(Items.GOLD_BLOCK)
                .input(Items.NETHERITE_BLOCK)
                .input(Items.EXPERIENCE_BOTTLE)
                .input(Items.NETHER_STAR)
                .input(Items.ELYTRA)
                .input(Items.LINGERING_POTION)
                .input(Items.WRITABLE_BOOK)
                .input(Items.EMERALD_BLOCK)
                .criterion("has_nether_star", InventoryChangedCriterion.Conditions.items(Items.NETHER_STAR))
                .criterion("has_book", InventoryChangedCriterion.Conditions.items(Items.WRITABLE_BOOK))
                .offerTo(exporter);
        // hot sand
        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, JBlockRegistry.HOT_SAND_BLOCK.get(), 8)
                .pattern("SSS")
                .pattern("SMS")
                .pattern("SSS")
                .input('M', Items.MAGMA_BLOCK)
                .input('S', Items.SAND)
                .criterion("has_sand", InventoryChangedCriterion.Conditions.items(Items.SAND))
                .criterion("has_magma_block", InventoryChangedCriterion.Conditions.items(Items.MAGMA_BLOCK))
                .offerTo(exporter);
        // Anubis sheathed
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, JItemRegistry.ANUBIS_SHEATHED.get())
                .pattern("LSI")
                .pattern("SDS")
                .pattern("GSL")
                .input('D', Items.DIAMOND)
                .input('G', Items.GOLD_BLOCK)
                .input('L', Items.LEATHER)
                .input('I', Items.IRON_BLOCK)
                .input('S', JBlockRegistry.SOUL_BLOCK.get())
                .criterion("has_diamond", InventoryChangedCriterion.Conditions.items(Items.DIAMOND))
                .criterion("has_soul_block", InventoryChangedCriterion.Conditions.items(JBlockRegistry.SOUL_BLOCK.get()))
                .offerTo(exporter);
        // boxing gloves
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, JItemRegistry.BOXING_GLOVES.get())
                .pattern("LLR")
                .pattern("SLL")
                .pattern(" SL")
                .input('L', Items.LEATHER)
                .input('R', Items.RED_DYE)
                .input('S', Items.STRING)
                .criterion("has_leather", InventoryChangedCriterion.Conditions.items(Items.LEATHER))
                .offerTo(exporter);
        // coin to nuggets
        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, Items.GOLD_NUGGET, 2)
                .input(JItemRegistry.KQ_COIN.get())
                .criterion("has_coin", InventoryChangedCriterion.Conditions.items(JItemRegistry.KQ_COIN.get()))
                .offerTo(exporter);
        // nuggets to coin
        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, JItemRegistry.KQ_COIN.get())
                .input(Items.GOLD_NUGGET)
                .input(Items.GOLD_NUGGET)
                .criterion("has_nugget", InventoryChangedCriterion.Conditions.items(Items.GOLD_NUGGET))
                .offerTo(exporter);
        // green baby
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, JItemRegistry.GREEN_BABY.get())
                .pattern("GBG")
                .pattern("SMS")
                .pattern("GBG")
                .input('B', Items.BONE_BLOCK)
                .input('G', Items.GREEN_DYE)
                .input('M', Items.TOTEM_OF_UNDYING)
                .input('S', JBlockRegistry.SOUL_BLOCK.get())
                .criterion("has_totem", InventoryChangedCriterion.Conditions.items(Items.TOTEM_OF_UNDYING))
                .offerTo(exporter);
        // knife
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, JItemRegistry.KNIFE.get())
                .pattern("  N")
                .pattern(" I ")
                .pattern("S  ")
                .input('I', Items.IRON_INGOT)
                .input('N', Items.IRON_NUGGET)
                .input('S', Items.STICK)
                .criterion("has_ingot", InventoryChangedCriterion.Conditions.items(Items.IRON_INGOT))
                .offerTo(exporter);
        // knife bundle
        ShapelessRecipeJsonBuilder.create(RecipeCategory.COMBAT, JItemRegistry.KNIFEBUNDLE.get())
                .input(JItemRegistry.KNIFE.get())
                .input(JItemRegistry.KNIFE.get())
                .input(JItemRegistry.KNIFE.get())
                .input(JItemRegistry.KNIFE.get())
                .input(JItemRegistry.KNIFE.get())
                .input(JItemRegistry.KNIFE.get())
                .input(JItemRegistry.KNIFE.get())
                .input(JItemRegistry.KNIFE.get())
                .input(JItemRegistry.KNIFE.get())
                .criterion("has_knife", InventoryChangedCriterion.Conditions.items(JItemRegistry.KNIFE.get()))
                .offerTo(exporter);
    }
}