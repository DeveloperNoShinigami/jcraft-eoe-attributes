package net.arna.jcraft.fabric.mixin;

import net.arna.jcraft.common.tickable.Timestops;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.ticks.ScheduledTick;
import net.minecraft.world.ticks.TickPriority;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelAccessor.class)
public interface TimestopBlockMixin {
    @Inject(method = "scheduleBlockTick(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/Block;ILnet/minecraft/world/tick/TickPriority;)V", at = @At("HEAD"), cancellable = true)
    private void jcraft$createAndScheduleBlockTick(BlockPos pos, Block block, int delay, TickPriority priority, CallbackInfo info) {
        int ticks = Timestops.getTicksIfInTSRange(pos);

        if (ticks > 0) {
            LevelAccessor worldAccess = (LevelAccessor) this;
            worldAccess.getBlockTicks().schedule(
                    new ScheduledTick<>(block, pos, worldAccess.getLevelData().getGameTime() + (long) ticks + delay, priority, worldAccess.nextSubTickCount())
            );
            info.cancel();
        }
    }

    @Inject(method = "scheduleBlockTick(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/Block;I)V", at = @At("HEAD"), cancellable = true)
    private void jcraft$createAndScheduleBlockTick(BlockPos pos, Block block, int delay, CallbackInfo info) {
        int ticks = Timestops.getTicksIfInTSRange(pos);

        if (ticks > 0) {
            LevelAccessor worldAccess = (LevelAccessor) this;
            worldAccess.getBlockTicks().schedule(
                    new ScheduledTick<>(block, pos, worldAccess.getLevelData().getGameTime() + (long) ticks + delay, worldAccess.nextSubTickCount())
            );
            info.cancel();
        }
    }

    @Inject(method = "scheduleFluidTick(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/fluid/Fluid;ILnet/minecraft/world/tick/TickPriority;)V", at = @At("HEAD"), cancellable = true)
    private void jcraft$createAndScheduleFluidTick(BlockPos pos, Fluid fluid, int delay, TickPriority priority, CallbackInfo info) {
        int ticks = Timestops.getTicksIfInTSRange(pos);

        if (ticks > 0) {
            LevelAccessor worldAccess = (LevelAccessor) this;
            worldAccess.getFluidTicks().schedule(
                    new ScheduledTick<>(fluid, pos, worldAccess.getLevelData().getGameTime() + (long) ticks + delay, priority, worldAccess.nextSubTickCount())
            );
            info.cancel();
        }
    }

    @Inject(method = "scheduleFluidTick(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/fluid/Fluid;I)V", at = @At("HEAD"), cancellable = true)
    private void jcraft$createAndScheduleFluidTick(BlockPos pos, Fluid fluid, int delay, CallbackInfo info) {
        int ticks = Timestops.getTicksIfInTSRange(pos);

        if (ticks > 0) {
            LevelAccessor worldAccess = (LevelAccessor) this;
            worldAccess.getFluidTicks().schedule(
                    new ScheduledTick<>(fluid, pos, worldAccess.getLevelData().getGameTime() + (long) ticks + delay, worldAccess.nextSubTickCount())
            );
            info.cancel();
        }
    }
}