package net.arna.jcraft.mixin;

import net.arna.jcraft.common.events.JBlockEvents;
import net.arna.jcraft.common.events.JEntityEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Level.class)
public class LevelMixin {

    @Inject(method = "setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z", at = @At("HEAD"), cancellable = true)
    public void jcraft$setBlock(BlockPos pos, BlockState newState, int flags, CallbackInfoReturnable<Boolean> cir) {
        final Level level = (Level)(Object)this;
        if (!level.isClientSide() && JBlockEvents.BEFORE_SET.invoker().setBlock(pos, level.getBlockState(pos), newState).interruptsFurtherEvaluation()) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }

}
