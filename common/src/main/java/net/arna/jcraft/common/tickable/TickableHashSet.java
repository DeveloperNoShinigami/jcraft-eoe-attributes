package net.arna.jcraft.common.tickable;

import net.arna.jcraft.JCraft;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Consumer;

public class TickableHashSet<T> {
    private final HashSet<T> set = new HashSet<>();
    private final Queue<T> queue = new LinkedList<>();
    private boolean ticking = false;

    public void add(T key) {
        if (ticking) {
            queue.add(key);
            return;
        }
        set.add(key);
    }

    public boolean contains(Object key) {
        return set.contains(key);
    }

    public int size() {
        return set.size();
    }

    public void tick(Consumer<Iterator<T>> consumer) {
        if (ticking) {
            JCraft.LOGGER.error("Tried to tick TickableHashSet while already ticking!");
            return;
        }

        while (!queue.isEmpty()) {
            T newItem = queue.poll();
            set.add(newItem);
        }
        ticking = true;
        Iterator<T> i = set.iterator();
        while (i.hasNext()) consumer.accept(i);
        ticking = false;
    }
}
