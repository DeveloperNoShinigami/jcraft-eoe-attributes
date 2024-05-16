package net.arna.jcraft.common.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public record BlockInfo(BlockState state, BlockPos pos) {

}
