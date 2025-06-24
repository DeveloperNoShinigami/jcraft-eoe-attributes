package net.arna.jcraft.api.attack.moves;

import lombok.NonNull;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import java.util.LinkedList;
import java.util.List;

public interface BlockMarkerMove {

    // FIXME: this can be done better, right Planet?
    // saves all BlockMarkerMoves
    List<BlockMarkerMove> MOVES = new LinkedList<>();

    boolean isResolving();

    void setResolving(final boolean resolving);

    boolean addBlock(final @NonNull BlockPos pos, final @NonNull BlockState state);

}
