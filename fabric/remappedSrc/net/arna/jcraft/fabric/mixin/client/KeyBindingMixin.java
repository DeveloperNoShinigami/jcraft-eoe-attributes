package net.arna.jcraft.fabric.mixin.client;

import net.arna.jcraft.client.util.TrackedKeyBinding;
import net.minecraft.client.KeyMapping;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(KeyMapping.class)
public class KeyBindingMixin {

    @Inject(method = "setPressed", at = @At("HEAD"))
    private void queueKeyPressOrRelease(boolean pressed, CallbackInfo ci) {
        KeyMapping binding = (KeyMapping) (Object) (this);
        if (pressed == binding.isDown()) {
            return;
        }
        TrackedKeyBinding.onKeyPressSet(binding, pressed);
    }
}
