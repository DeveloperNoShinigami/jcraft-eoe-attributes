package net.arna.jcraft.common.marker;

import net.arna.jcraft.api.attack.moves.BlockMarkerMove;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

public class BlockMarkerMoves {
    private static boolean iterating = false;
    private static final Queue<BlockMarkerMove> QUEUE = new LinkedBlockingQueue<>();
    private static final Collection<BlockMarkerMove> MOVES = new LinkedList<>(); // TODO: Map<StandEntity, BlockMarkerMove> + ticking for cleanup

    public static void mergeQueue() {
        MOVES.addAll(QUEUE);
    }

    public static void forEach(Consumer<BlockMarkerMove> consumer) {
        iterating = true;
        MOVES.forEach(consumer);
        iterating = false;
    }

    public static boolean add(BlockMarkerMove move) {
        if (iterating) {
            return QUEUE.add(move);
        }

        return MOVES.add(move);
    }
}
