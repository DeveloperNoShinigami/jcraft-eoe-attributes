package net.arna.jcraft.common.marker;

import com.google.common.collect.Multimap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimaps;
import lombok.experimental.UtilityClass;
import net.arna.jcraft.api.attack.moves.BlockMarkerMove;
import net.arna.jcraft.api.stand.StandEntity;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

@UtilityClass
public class BlockMarkerMoves {
    private boolean iterating = false;
    private final Multimap<StandEntity<?,?>, BlockMarkerMove> ADD_QUEUE = Multimaps.synchronizedMultimap(HashMultimap.create());
    private final Queue<StandEntity<?,?>> REMOVE_QUEUE = new LinkedBlockingQueue<>();
    private final Multimap<StandEntity<?,?>, BlockMarkerMove> MOVES = HashMultimap.create();

    public void mergeQueues() {
        MOVES.putAll(ADD_QUEUE);
        ADD_QUEUE.clear();
        for (final StandEntity<?,?> stand : REMOVE_QUEUE) {
            MOVES.removeAll(stand);
        }
        REMOVE_QUEUE.clear();
    }

    public void forEach(Consumer<BlockMarkerMove> consumer) {
        iterating = true;
        MOVES.forEach((stand, move) -> consumer.accept(move));
        iterating = false;
    }

    public void add(StandEntity<?,?> stand, BlockMarkerMove move) {
        if (!MOVES.containsEntry(stand, move) && !ADD_QUEUE.containsEntry(stand, move)) {
            if (iterating) {
                ADD_QUEUE.put(stand, move);
            } else {
                MOVES.put(stand, move);
            }
        }
    }

    public void remove(StandEntity<?,?> stand) {
        if (iterating) {
            REMOVE_QUEUE.add(stand);
        }
        else {
            MOVES.removeAll(stand);
        }
    }
}
