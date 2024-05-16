package net.arna.jcraft.mixin.client;

import net.arna.jcraft.common.util.JUtils;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LocalPlayer.class)
public class ClientPlayerEntityMixin {
    @Inject(method = "drop", at = @At(value = "HEAD"), cancellable = true)
    private void jcraft$dropSelectedItem(boolean entireStack, CallbackInfoReturnable<Boolean> cir) {
        if (!JUtils.canAct((LocalPlayer) (Object) this)) {
            cir.cancel();
        }
    }
}
