package net.arna.jcraft.forge.mixin;

import net.arna.jcraft.common.tickable.Timestops;
import net.minecraft.block.Block;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.tick.OrderedTick;
import net.minecraft.world.tick.QueryableTickScheduler;
import net.minecraft.world.tick.TickPriority;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldAccess.class)
public interface TimestopBlockMixin {

    @Shadow QueryableTickScheduler<Block> getBlockTickScheduler();

    @Shadow <T> OrderedTick<T> createOrderedTick(BlockPos pos, T type, int delay, TickPriority priority);

    @Shadow <T> OrderedTick<T> createOrderedTick(BlockPos pos, T type, int delay);

    @Shadow QueryableTickScheduler<Fluid> getFluidTickScheduler();

    /**
     * @author mrsterner
     * @reason interface inject
     */
    @Overwrite
    default void scheduleBlockTick(BlockPos pos, Block block, int delay, TickPriority priority) {

        int ticks = Timestops.getTicksIfInTSRange(pos);

        if (ticks > 0) {
            WorldAccess worldAccess = (WorldAccess) this;
            worldAccess.getBlockTickScheduler().scheduleTick(
                    new OrderedTick<>(block, pos, worldAccess.getLevelProperties().getTime() + (long) ticks + delay, priority, worldAccess.getTickOrder())
            );
            return;
        }

        this.getBlockTickScheduler().scheduleTick(this.createOrderedTick(pos, block, delay, priority));
    }

    /**
     * @author mrsterner
     * @reason interface inject
     */
    @Overwrite
    default void scheduleBlockTick(BlockPos pos, Block block, int delay) {

        int ticks = Timestops.getTicksIfInTSRange(pos);

        if (ticks > 0) {
            WorldAccess worldAccess = (WorldAccess) this;
            worldAccess.getBlockTickScheduler().scheduleTick(
                    new OrderedTick<>(block, pos, worldAccess.getLevelProperties().getTime() + (long) ticks + delay, worldAccess.getTickOrder())
            );

            return;
        }

        this.getBlockTickScheduler().scheduleTick(this.createOrderedTick(pos, block, delay));
    }

    /**
     * @author mrsterner
     * @reason interface inject
     */
    @Overwrite
    default void scheduleFluidTick(BlockPos pos, Fluid fluid, int delay, TickPriority priority) {

        int ticks = Timestops.getTicksIfInTSRange(pos);

        if (ticks > 0) {
            WorldAccess worldAccess = (WorldAccess) this;
            worldAccess.getFluidTickScheduler().scheduleTick(
                    new OrderedTick<>(fluid, pos, worldAccess.getLevelProperties().getTime() + (long) ticks + delay, priority, worldAccess.getTickOrder())
            );
            return;
        }

        this.getFluidTickScheduler().scheduleTick(this.createOrderedTick(pos, fluid, delay, priority));
    }

    /**
     * @author mrsterner
     * @reason interface inject
     */
    @Overwrite
    default void scheduleFluidTick(BlockPos pos, Fluid fluid, int delay) {

        int ticks = Timestops.getTicksIfInTSRange(pos);

        if (ticks > 0) {
            WorldAccess worldAccess = (WorldAccess) this;
            worldAccess.getFluidTickScheduler().scheduleTick(
                    new OrderedTick<>(fluid, pos, worldAccess.getLevelProperties().getTime() + (long) ticks + delay, worldAccess.getTickOrder())
            );
            return;
        }

        this.getFluidTickScheduler().scheduleTick(this.createOrderedTick(pos, fluid, delay));
    }
}