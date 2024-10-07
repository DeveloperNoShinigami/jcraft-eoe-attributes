package net.arna.jcraft.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.ticks.TickPriority;

public class FoolishSandBlock extends FallingBlock {
    public static final int MAX_AGE = 250;
    public static final IntegerProperty AGE;

    public FoolishSandBlock(final Properties settings) {
        super(settings);
        registerDefaultState(this.stateDefinition.any().setValue(AGE, 0));
    }

    @Override
    public void tick(final BlockState state, final ServerLevel world, final BlockPos pos, final RandomSource random) {
        if (increaseAge(state, world, pos)) {
            world.scheduleTick(pos, this, 20, TickPriority.NORMAL);
            return;
        }

        super.tick(state, world, pos, random);
        final BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos();
        for (Direction direction : Direction.values()) {
            blockPos.setWithOffset(pos, direction);
            BlockState blockState = world.getBlockState(blockPos);
            if (blockState.is(this) && increaseAge(blockState, world, blockPos)) {
                world.scheduleTick(blockPos, this, 20);
            }
        }
    }

    @Override
    public int getDustColor(final BlockState state, final BlockGetter world, final BlockPos pos) {
        return 16777216;
    }

    /**
     * @return Whether the block should continue existing.
     */
    private boolean increaseAge(final BlockState state, final Level world, final BlockPos pos) {
        int i = state.getValue(AGE);
        if (i < MAX_AGE) {
            world.setBlock(pos, state.setValue(AGE, i + 1), 2);
            return true;
        }

        world.removeBlock(pos, false);
        return false;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }

    public ItemStack getCloneItemStack(final BlockGetter world, final BlockPos pos, final BlockState state) {
        return ItemStack.EMPTY;
    }

    static {
        AGE = IntegerProperty.create("age", 0, MAX_AGE);
    }
}
