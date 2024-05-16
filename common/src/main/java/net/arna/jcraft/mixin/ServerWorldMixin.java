package net.arna.jcraft.mixin;

import net.arna.jcraft.common.util.IJSplatterManagerHolder;
import net.arna.jcraft.platform.JComponentPlatformUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

@Mixin(ServerLevel.class)
public abstract class ServerWorldMixin implements IJSplatterManagerHolder {
    // Serverside timestop handling
    @Inject(cancellable = true, at = @At("HEAD"), method = "tickNonPassenger")
    private void timestopTick(Entity entity, CallbackInfo ci) {
        if (JComponentPlatformUtils.getTimeStopData(entity).isPresent()) {
            JComponentPlatformUtils.getTimeStopData(entity).get().tick(ci);
        }

    }

    @Inject(method = "tick", at = @At("RETURN"))
    private void tickSplatters(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        jcraft$getSplatterManager().tick();
    }
}
