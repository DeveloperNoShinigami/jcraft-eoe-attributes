package net.arna.jcraft.api.spec;

import net.arna.jcraft.api.JRegistries;
import net.arna.jcraft.registry.JSpecTypeRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class SpecTypeUtil {
    public static Stream<SpecType> streamAll() {
        return StreamSupport.stream(JRegistries.SPEC_TYPE_REGISTRY.spliterator(), false);
    }

    /**
     * Reads a spec type from the given NBT compound with the given key.
     * First attempts to read a legacy integer ordinal, then a string ID.
     * @param nbt The NBT compound to read from
     * @param key The key to read the stand type from
     * @return the stand type, or {@code null} if not found or an invalid type was found.
     */
    @Nullable
    public static SpecType readFromNBT(final CompoundTag nbt, final String key) {
        if (nbt.contains(key, Tag.TAG_INT)) {
            final int ordinal = nbt.getInt(key);
            return Optional.ofNullable(JSpecTypeRegistry.LEGACY_ORDINALS.get(ordinal))
                    .map(Supplier::get)
                    .orElse(null);
        } else if (nbt.contains(key, Tag.TAG_STRING)) {
            final String id = nbt.getString(key);
            return JRegistries.SPEC_TYPE_REGISTRY.get(new ResourceLocation(id));
        } else {
            return null;
        }
    }
}
