package net.arna.jcraft.common.enchantments;

import net.arna.jcraft.registry.JItemRegistry;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public class CinderellasKissEnchantment extends Enchantment {
    public static final CinderellasKissEnchantment INSTANCE = new CinderellasKissEnchantment();

    private CinderellasKissEnchantment() {
        super(Rarity.RARE, EnchantmentCategory.WEARABLE, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public boolean canEnchant(final ItemStack stack) {
        return stack.getItem() == JItemRegistry.CINDERELLA_MASK.get();
    }

    public static int getCKLevel(final ItemStack stack) {
        return EnchantmentHelper.getItemEnchantmentLevel(INSTANCE, stack);
    }
}
