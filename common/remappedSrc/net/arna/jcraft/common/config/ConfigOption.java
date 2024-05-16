package net.arna.jcraft.common.config;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import lombok.Getter;
import lombok.NonNull;
import net.arna.jcraft.JCraft;
import net.minecraft.network.FriendlyByteBuf;
import java.util.*;

public abstract class ConfigOption {
    private static final Map<String, ConfigOption> options = new LinkedHashMap<>();
    @Getter(lazy = true)
    private static final Map<String, ConfigOption> immutableOptions = ImmutableMap.copyOf(options);

    @Getter
    private final Type type;
    @Getter
    private final String key;
    @Getter
    private final String category;

    protected ConfigOption(Type type, String key, String category) {
        this.type = type;
        this.key = key;
        this.category = category;

        if (options.put(key, this) != null) {
            throw new IllegalArgumentException("Option with the given key already exists: " + key);
        }
    }

    @NonNull
    public static FriendlyByteBuf writeOptions(@NonNull FriendlyByteBuf buf, Collection<ConfigOption> options) {
        for (ConfigOption option : options) {
            buf.writeUtf(option.getKey());
            option.write(buf);
        }

        return buf;
    }

    @NonNull
    public static Set<ConfigOption> readOptions(FriendlyByteBuf buf) {
        Set<ConfigOption> changedOptions = new HashSet<>();
        while (buf.readableBytes() > 0) {
            String key = buf.readUtf();
            ConfigOption option = getImmutableOptions().get(key);
            if (option == null) {
                JCraft.LOGGER.warn("Could not find option {}. Rest of the data ({} bytes) will be ignored.",
                        key, buf.readableBytes());

                buf.readerIndex(buf.readerIndex() + buf.readableBytes()); // Move cursor to end.
                break; // Rest will be invalid
            }

            option.read(buf);
            changedOptions.add(option);
        }
        return changedOptions;
    }

    public abstract void write(FriendlyByteBuf buf);

    public abstract void read(FriendlyByteBuf buf);

    public abstract JsonElement write();

    public abstract void read(JsonElement element);

    public enum Type {
        INTEGER, FLOAT, BOOLEAN, ENUM
    }
}
