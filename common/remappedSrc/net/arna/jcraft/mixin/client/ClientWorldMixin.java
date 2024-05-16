package net.arna.jcraft.mixin.client;

import net.arna.jcraft.common.entity.stand.CreamEntity;
import net.arna.jcraft.common.util.IJSplatterManagerHolder;
import net.arna.jcraft.platform.JComponentPlatformUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

@Mixin(ClientLevel.class)
public abstract class ClientWorldMixin implements IJSplatterManagerHolder {

    // Clientside timestop handling
    @Inject(cancellable = true, at = @At("HEAD"), method = "tickEntity")
    private void timestopTick(Entity entity, CallbackInfo ci) {
        JComponentPlatformUtils.getTimeStopData(entity).tick(ci);
    }

    // Cream void deafness
    @Inject(method = "playSound(DDDLnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FFZ)V", at = @At("HEAD"), cancellable = true)
    private void playSound(double x, double y, double z, SoundEvent sound, SoundSource category, float volume, float pitch, boolean useDistance, CallbackInfo info) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            if (player.getFirstPassenger() instanceof CreamEntity cream) {
                if (cream.getVoidTime() > 0) {
                    info.cancel();
                }
            }
        }
    }

    @Inject(method = "tick", at = @At("RETURN"))
    private void tickSplatters(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        jcraft$getSplatterManager().tick();
    }
}
