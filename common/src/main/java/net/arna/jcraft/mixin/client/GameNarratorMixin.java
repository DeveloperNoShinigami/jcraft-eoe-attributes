package net.arna.jcraft.mixin.client;

import net.arna.jcraft.client.JClientConfig;
import net.minecraft.client.GameNarrator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = GameNarrator.class, priority = 2000)
public class GameNarratorMixin {
    @Inject(
            method = "isActive",
            at = @At("HEAD"),
            cancellable = true
    )
    public void jcraft$disableNarrator(final CallbackInfoReturnable<Boolean> cir) {
        final JClientConfig config = JClientConfig.getInstance();
        if (config == null) return;
        if (config.isDisableNarrator()) cir.setReturnValue(false);
    }
}
