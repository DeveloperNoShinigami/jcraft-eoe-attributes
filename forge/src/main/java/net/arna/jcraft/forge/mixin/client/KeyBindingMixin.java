package net.arna.jcraft.forge.mixin.client;

import net.arna.jcraft.client.util.TrackedKeyBinding;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraftforge.client.settings.KeyMappingLookup;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(KeyBinding.class)
public class KeyBindingMixin {

    @Inject(method = "setPressed", at = @At("HEAD"))
    private void queueKeyPressOrRelease(boolean pressed, CallbackInfo ci) {
        KeyBinding binding = (KeyBinding) (Object) (this);
        if (pressed == binding.isPressed()) {
            return;
        }
        TrackedKeyBinding.onKeyPressSet(binding, pressed);
    }
}
