package net.arna.jcraft.mixin;

import net.arna.jcraft.common.item.BloodBottleItem;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Villager.class)
public class VillagerEntityMixin {
    @Inject(at = @At("HEAD"), method = "mobInteract", cancellable = true)
    private void interactMob(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        if (player.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof BloodBottleItem) {
            cir.setReturnValue(InteractionResult.PASS);
        }
    }
}
