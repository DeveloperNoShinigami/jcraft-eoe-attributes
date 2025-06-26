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

    /**
     * Maybe saves a block to be restored.
     * @param pos the position of the block
     * @param state the (old) block state of that position
     * @return <code>true</code> if the block has been saved, <code>false</code> otherwise.
     */
    boolean addBlock(final @NonNull BlockPos pos, final @NonNull BlockState state);

}
