package net.arna.jcraft.common.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import lombok.Setter;
import net.minecraft.network.FriendlyByteBuf;

public class BooleanOption extends ConfigOption {
    @Setter
    private boolean value;
    private final boolean defaultValue;

    protected BooleanOption(final String key, final String category, final boolean value) {
        super(Type.BOOLEAN, key, category);
        this.value = this.defaultValue = value;
    }

    // Lombok names it isValue which makes no sense here.
    public boolean getValue() {
        return value;
    }

    public boolean getDefaultValue() {
        return defaultValue;
    }

    @Override
    public void write(final FriendlyByteBuf buf) {
        buf.writeBoolean(value);
    }

    @Override
    public void read(final FriendlyByteBuf buf) {
        value = buf.readBoolean();
    }

    @Override
    public JsonElement write() {
        return new JsonPrimitive(value);
    }

    @Override
    public void read(final JsonElement element) {
        value = element.getAsBoolean();
    }
}
