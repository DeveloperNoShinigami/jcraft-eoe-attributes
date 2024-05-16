package net.arna.jcraft.mixin.client;

import net.arna.jcraft.client.JClientConfig;
import net.minecraft.client.multiplayer.ClientPacketListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ClientPacketListener.class)
public class ClientPlayNetworkHandlerMixin {
    @ModifyArg(method = "handleTeleportEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;lerpTo(DDDFFIZ)V"), index = 5)
    private int modifyInterpolationSteps(int original) {
        if (JClientConfig.getInstance().isClientsidePrediction()) {
            return 2; // 3 -> 2
        }
        return original;
    }
}
