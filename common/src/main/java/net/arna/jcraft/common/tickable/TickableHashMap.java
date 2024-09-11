package net.arna.jcraft.common.tickable;

import net.arna.jcraft.JCraft;
import net.minecraft.util.Tuple;

import java.util.*;
import java.util.function.Consumer;

/**
 * A {@link HashMap} wrapper that SHOULD provide safety against {@link java.util.ConcurrentModificationException}s.
 * Has a tick() method which follows the format all Tickables that use HashMaps should, providing read/write access to the map.
 * @param <K>
 * @param <V>
 */
public class TickableHashMap<K, V> {
    private final HashMap<K, V> map = new HashMap<>();
    private final Queue<Tuple<K, V>> queue = new LinkedList<>();
    private boolean ticking = false;

    public void add(K key, V value) {
        if (ticking) {
            queue.add(new Tuple<>(key, value));
            return;
        }
        map.put(key, value);
    }

    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    public V get(Object key) {
        return map.get(key);
    }

    public void tick(Consumer<Iterator<Map.Entry<K, V>>> consumer) {
        if (ticking) {
            JCraft.LOGGER.error("Tried to tick TickableHashMap while already ticking!");
            return;
        }

        while (!queue.isEmpty()) {
            Tuple<K,V> newEntry = queue.poll();
            map.put(newEntry.getA(), newEntry.getB());
        }
        ticking = true;
        Iterator<Map.Entry<K,V>> i = map.entrySet().iterator();
        while (i.hasNext()) consumer.accept(i);
        ticking = false;
    }
}
