package net.arna.jcraft.mixin;

import net.arna.jcraft.registry.JItemRegistry;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AnvilMenu.class)
public class AnvilScreenHandlerMixin {

    @Redirect(method = "updateResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isDamageable()Z", ordinal = 1))
    private boolean allowMasks(ItemStack stack) {
        return stack.getItem() == JItemRegistry.CINDERELLA_MASK || stack.isDamageableItem();
    }
}
