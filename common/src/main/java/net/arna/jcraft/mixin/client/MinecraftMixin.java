package net.arna.jcraft.mixin.client;

import com.mojang.blaze3d.platform.Window;
import net.arna.jcraft.client.rendering.api.callbacks.DisplayResizeCallback;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Shadow @Final private Window window;

    @Inject(method = "resizeDisplay", at = @At("RETURN"))
    private void onResizeDisplay(final CallbackInfo ci) {
        DisplayResizeCallback.EVENT.invoker().onResolutionChanged(window.getWidth(), window.getHeight());
    }
}
