package net.arna.jcraft.common.tickable;

import dev.architectury.event.events.common.TickEvent;
import net.arna.jcraft.common.attack.core.IAttacker;
import net.arna.jcraft.common.attack.moves.base.AbstractMove;
import net.minecraft.server.MinecraftServer;

import java.util.*;

// This class is used to queue move ticks to be executed after all entities have been ticked.
public class MoveTickQueue {
    private static final Queue<MoveTick<?>> queue = new LinkedList<>();

    public static void registerMoveTickQueue() {
        TickEvent.SERVER_POST.register(MoveTickQueue::tick);
    }

    private static void tick(final MinecraftServer server) {
        synchronized (queue) {
            while (!queue.isEmpty()) {
                /*
                MoveTick<?> moveTick = queue.poll();
                IAttacker<?, ?> attacker = moveTick.attacker();
                int moveStun = attacker.getMoveStun();
                attacker.setMoveStun(moveTick.moveStun());
                 */
                queue.poll().tick();
                // attacker.setMoveStun(moveStun);
            }
        }
    }

    public static <A extends IAttacker<? extends A, ?>> void queueTick(A attacker, AbstractMove<?, ? super A> move, int moveStun) {
        //queue.add(new MoveTick<>(attacker, move, moveStun));
        attacker.setPerformedThisTick(false);
        move.activeTick(attacker, moveStun);
    }

    private record MoveTick<A extends IAttacker<? extends A, ?>>(A attacker, AbstractMove<?, ? super A> move, int moveStun) {
        public void tick() {
            move.activeTick(attacker, moveStun);
        }
    }
}
