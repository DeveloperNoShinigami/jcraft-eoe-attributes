package net.arna.jcraft.forge.mixin;

import net.arna.jcraft.common.tickable.Timestops;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.ticks.LevelTickAccess;
import net.minecraft.world.ticks.ScheduledTick;
import net.minecraft.world.ticks.TickPriority;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LevelAccessor.class, remap = false)
public interface TimestopBlockMixin {


    @Shadow LevelTickAccess<Block> getBlockTicks();

    @Shadow <T> ScheduledTick<T> createTick(BlockPos pos, T type, int delay, TickPriority priority);

    @Shadow <T> ScheduledTick<T> createTick(BlockPos pos, T type, int delay);

    @Shadow LevelTickAccess<Fluid> getFluidTicks();

    /**
     * @author mrsterner
     * @reason interface inject
     */
    @Overwrite
    default void scheduleTick(BlockPos pos, Block block, int delay, TickPriority priority) {

        int ticks = Timestops.getTicksIfInTSRange(pos);

        if (ticks > 0) {
            LevelAccessor worldAccess = (LevelAccessor) this;
            worldAccess.getBlockTicks().schedule(
                    new ScheduledTick<>(block, pos, worldAccess.getLevelData().getGameTime() + (long) ticks + delay, priority, worldAccess.nextSubTickCount())
            );
            return;
        }

        this.getBlockTicks().schedule(this.createTick(pos, block, delay, priority));
    }

    /**
     * @author mrsterner
     * @reason interface inject
     */
    @Overwrite
    default void scheduleTick(BlockPos pos, Block block, int delay) {

        int ticks = Timestops.getTicksIfInTSRange(pos);

        if (ticks > 0) {
            LevelAccessor worldAccess = (LevelAccessor) this;
            worldAccess.getBlockTicks().schedule(
                    new ScheduledTick<>(block, pos, worldAccess.getLevelData().getGameTime() + (long) ticks + delay, worldAccess.nextSubTickCount())
            );

            return;
        }

        this.getBlockTicks().schedule(this.createTick(pos, block, delay));
    }

    /**
     * @author mrsterner
     * @reason interface inject
     */
    @Overwrite
    default void scheduleTick(BlockPos pos, Fluid fluid, int delay, TickPriority priority) {

        int ticks = Timestops.getTicksIfInTSRange(pos);

        if (ticks > 0) {
            LevelAccessor worldAccess = (LevelAccessor) this;
            worldAccess.getFluidTicks().schedule(
                    new ScheduledTick<>(fluid, pos, worldAccess.getLevelData().getGameTime() + (long) ticks + delay, priority, worldAccess.nextSubTickCount())
            );
            return;
        }

        this.getFluidTicks().schedule(this.createTick(pos, fluid, delay, priority));
    }

    /**
     * @author mrsterner
     * @reason interface inject
     */
    @Overwrite
    default void scheduleTick(BlockPos pos, Fluid fluid, int delay) {

        int ticks = Timestops.getTicksIfInTSRange(pos);

        if (ticks > 0) {
            LevelAccessor worldAccess = (LevelAccessor) this;
            worldAccess.getFluidTicks().schedule(
                    new ScheduledTick<>(fluid, pos, worldAccess.getLevelData().getGameTime() + (long) ticks + delay, worldAccess.nextSubTickCount())
            );
            return;
        }

        this.getFluidTicks().schedule(this.createTick(pos, fluid, delay));
    }
}