package net.arna.jcraft.common.recipes;

import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.enchantments.CinderellasKissEnchantment;
import net.arna.jcraft.common.entity.stand.StandType;
import net.arna.jcraft.common.item.StandDiscItem;
import net.arna.jcraft.registry.JItemRegistry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.level.Level;

public class StandSkinSmithingRecipe implements SmithingRecipe {
    public static final ResourceLocation ID = JCraft.id("stand_skin");
    public static final StandSkinSmithingRecipe INSTANCE = new StandSkinSmithingRecipe();

    private StandSkinSmithingRecipe() {
    }

    @Override
    public boolean matches(Container inventory, Level world) {
        ItemStack discStack = inventory.getItem(0);
        ItemStack maskStack = inventory.getItem(1);

        StandType standType = StandDiscItem.getStandType(discStack);
        if (standType == null) {
            return false;
        }

        return discStack.getItem() == JItemRegistry.STAND_DISC &&
                maskStack.getItem() == JItemRegistry.CINDERELLA_MASK &&
                CinderellasKissEnchantment.getCKLevel(maskStack) <= standType.getSkinCount();
    }

    @Override
    public ItemStack assemble(Container inventory, RegistryAccess registryManager) {
        ItemStack discStack = inventory.getItem(0);
        ItemStack maskStack = inventory.getItem(1);

        ItemStack res = discStack.copy();
        res.setCount(1);
        int skin = CinderellasKissEnchantment.getCKLevel(maskStack); // 0 is default skin
        StandDiscItem.setSkin(res, skin);
        return res;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryManager) {
        return ItemStack.EMPTY;
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return null;//TODO
    }

    @Override
    public boolean isTemplateIngredient(ItemStack stack) {
        return false;
    }

    @Override
    public boolean isBaseIngredient(ItemStack stack) {
        return false;
    }

    @Override
    public boolean isAdditionIngredient(ItemStack stack) {
        return false;
    }
}
