package net.arna.jcraft.mixin;

import net.arna.jcraft.registry.JStatusRegistry;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Item.class, BowItem.class, CrossbowItem.class, TridentItem.class})
public class LongUseItemMixin {
    @Inject(cancellable = true, at = @At("HEAD"), method = "releaseUsing") // Inability to use items while stunned
    private void jcraft$onStoppedUsing(ItemStack stack, Level world, LivingEntity user, int remainingUseTicks, CallbackInfo ci) {
        if (user.hasEffect(JStatusRegistry.DAZED.get())) {
            ci.cancel();
        }
    }
}
