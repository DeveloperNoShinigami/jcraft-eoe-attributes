package net.arna.jcraft.common.recipes;

import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.enchantments.CinderellasKissEnchantment;
import net.arna.jcraft.common.entity.stand.StandType;
import net.arna.jcraft.common.item.StandDiscItem;
import net.arna.jcraft.registry.JItemRegistry;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SmithingRecipe;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class StandSkinSmithingRecipe implements SmithingRecipe {
    public static final Identifier ID = JCraft.id("stand_skin");
    public static final StandSkinSmithingRecipe INSTANCE = new StandSkinSmithingRecipe();

    private StandSkinSmithingRecipe() {
    }

    @Override
    public boolean matches(Inventory inventory, World world) {
        ItemStack discStack = inventory.getStack(0);
        ItemStack maskStack = inventory.getStack(1);

        StandType standType = StandDiscItem.getStandType(discStack);
        if (standType == null) {
            return false;
        }

        return discStack.getItem() == JItemRegistry.STAND_DISC &&
                maskStack.getItem() == JItemRegistry.CINDERELLA_MASK &&
                CinderellasKissEnchantment.getCKLevel(maskStack) <= standType.getSkinCount();
    }

    @Override
    public ItemStack craft(Inventory inventory, DynamicRegistryManager registryManager) {
        ItemStack discStack = inventory.getStack(0);
        ItemStack maskStack = inventory.getStack(1);

        ItemStack res = discStack.copy();
        res.setCount(1);
        int skin = CinderellasKissEnchantment.getCKLevel(maskStack); // 0 is default skin
        StandDiscItem.setSkin(res, skin);
        return res;
    }

    @Override
    public ItemStack getOutput(DynamicRegistryManager registryManager) {
        return ItemStack.EMPTY;
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return null;//TODO
    }

    @Override
    public boolean testTemplate(ItemStack stack) {
        return false;
    }

    @Override
    public boolean testBase(ItemStack stack) {
        return false;
    }

    @Override
    public boolean testAddition(ItemStack stack) {
        return false;
    }
}
