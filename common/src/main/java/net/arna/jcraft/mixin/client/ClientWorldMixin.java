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
    @Inject(cancellable = true, at = @At("HEAD"), method = "tickNonPassenger")
    private void timestopTick(Entity entity, CallbackInfo ci) {
        if (JComponentPlatformUtils.getTimeStopData(entity).isPresent()) {
            JComponentPlatformUtils.getTimeStopData(entity).get().tick(ci);
        }
    }

    // Cream void deafness
    @Inject(method = "playSound", at = @At("HEAD"), cancellable = true)
    private void playSound(double x, double y, double z, SoundEvent soundEvent, SoundSource source, float volume, float pitch, boolean distanceDelay, long seed, CallbackInfo ci) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            if (player.getFirstPassenger() instanceof CreamEntity cream) {
                if (cream.getVoidTime() > 0) {
                    ci.cancel();
                }
            }
        }
    }

    @Inject(method = "tick", at = @At("RETURN"))
    private void tickSplatters(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        jcraft$getSplatterManager().tick();
    }
}
