package net.arna.jcraft.common.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import lombok.Getter;
import net.minecraft.network.FriendlyByteBuf;
import java.util.Arrays;

@Getter
public class EnumOption<E extends Enum<?>> extends ConfigOption {
    private final Class<E> clazz;
    private E value;
    private final E defaultValue;

    public EnumOption(final String key, final String category, final Class<E> clazz, final E value) {
        super(Type.ENUM, key, category);
        this.clazz = clazz;
        this.value = this.defaultValue = value;
    }

    public void setValue(final int ordinal) {
        setValue(clazz.getEnumConstants()[ordinal]);
    }

    public void setValue(final E value) {
        this.value = value;
    }

    @Override
    public void write(final FriendlyByteBuf buf) {
        buf.writeVarInt(value.ordinal());
    }

    @Override
    public void read(final FriendlyByteBuf buf) {
        setValue(buf.readVarInt());
    }

    @Override
    public JsonElement write() {
        return new JsonPrimitive(value.name());
    }

    @Override
    public void read(final JsonElement element) {
        String name = element.getAsString();
        value = Arrays.stream(clazz.getEnumConstants())
                .filter(e -> e.name().equals(name))
                .findFirst()
                .orElseThrow();
    }
}
