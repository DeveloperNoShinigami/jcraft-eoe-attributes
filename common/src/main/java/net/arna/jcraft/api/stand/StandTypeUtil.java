package net.arna.jcraft.api.stand;

import net.arna.jcraft.api.JRegistries;
import net.arna.jcraft.registry.JStandTypeRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
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
     * Gets a random obtainable (including evolutions) stand type.
     * @return a random obtainable stand type.
     */
    public static StandType getRandomObtainable(RandomSource random) {
        List<StandType> types = streamAllObtainable().toList();
        return types.get(random.nextInt(types.size()));
    }

    /**
     * Gets a random regular (so no evolutions) stand type.
     * @return a random regular stand type.
     */
    public static StandType getRandomRegular(RandomSource random) {
        List<StandType> types = streamAllObtainable()
                .filter(type -> !type.getData().isEvolution())
                .toList();
        return types.get(random.nextInt(types.size()));
    }

    /**
     * Gets a random stand type, may return an evolution type.
     * @return a random stand type.
     */
    public static StandType getRandom(RandomSource random) {
        List<StandType> types = streamAllObtainable().toList();
        return types.get(random.nextInt(types.size()));
    }

    /**
     * Reads a stand type from the given NBT compound with the given key.
     * First attempts to read a legacy integer ordinal, then a string ID.
     * @param nbt The NBT compound to read from
     * @param key The key to read the stand type from
     * @return the stand type, or {@code null} if not found or an invalid type was found.
     */
    @Nullable
    public static StandType readFromNBT(CompoundTag nbt, String key) {
        if (nbt.contains(key, Tag.TAG_INT)) {
            int ordinal = nbt.getInt(key);
            return Optional.ofNullable(JStandTypeRegistry.LEGACY_ORDINALS.get(ordinal))
                    .map(Supplier::get)
                    .orElse(null);
        } else if (nbt.contains(key, Tag.TAG_STRING)) {
            String id = nbt.getString(key);
            return JRegistries.STAND_TYPE_REGISTRY.get(new ResourceLocation(id));
        } else {
            return null;
        }
    }
}
