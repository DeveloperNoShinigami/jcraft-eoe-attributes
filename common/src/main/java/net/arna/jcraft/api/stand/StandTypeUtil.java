package net.arna.jcraft.api.stand;

import net.arna.jcraft.api.JRegistries;
import net.arna.jcraft.registry.JStandTypeRegistry;

import java.util.List;
import java.util.Random;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class StandTypeUtil {
    /**
     * Checks whether the given stand type is {@code null} or the {@code NONE} stand type.
     * @param type The type to check for
     * @return Whether the given type is equivalent to the {@code NONE} type.
     */
    public static boolean isNone(StandType type) {
        return type == null || type == JStandTypeRegistry.NONE.get();
    }

    /**
     * Creates a stream of all stand types, including unobtainable ones.
     * @return a stream of all stand types
     */
    public static Stream<StandType> streamAll() {
        return StreamSupport.stream(JRegistries.STAND_TYPE_REGISTRY.spliterator(), false);
    }

    /**
     * Like {@link #streamAll()}, but filters out unobtainable stands.
     * @return a stream of all obtainable stands.
     */
    public static Stream<StandType> streamAllObtainable() {
        return streamAll().filter(type -> type.getData().isObtainable());
    }

    /**
     * Gets a random regular (so no evolutions) stand type.
     * @return a random regular stand type.
     */
    public static StandType getRandomRegular() {
        List<StandType> types = streamAllObtainable()
                .filter(type -> !type.getData().isEvolution())
                .toList();
        return types.get(new Random().nextInt(types.size()));
    }

    /**
     * Gets a random stand type, may return an evolution type.
     * @return a random stand type.
     */
    public static StandType getRandom() {
        List<StandType> types = streamAllObtainable().toList();
        return types.get(new Random().nextInt(types.size()));
    }
}
