package net.arna.jcraft.mixin;

import net.arna.jcraft.common.tickable.Timestops;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LevelChunk.class)
public class TimestopBlockEntityMixin {
    @SuppressWarnings("CancellableInjectionUsage") // The warning is flat out wrong
    @Inject(method = "isTicking", at = @At("HEAD"), cancellable = true)
    void jcraft$canTickBlockEntity(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (Timestops.isInTSRange(pos)) {
            cir.cancel();
        }
    }
}