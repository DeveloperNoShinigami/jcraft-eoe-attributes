package net.arna.jcraft.common.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import lombok.Getter;
import net.minecraft.network.FriendlyByteBuf;

public class IntOption extends ConfigOption {
    @Getter
    private int value;
    @Getter
    private final int defaultValue;
    @Getter
    private Integer min, max;

    public IntOption(final String key, final String category, final int value) {
        super(Type.INTEGER, key, category);
        this.value = this.defaultValue = value;
    }

    public IntOption(final String key, final String category, final int value, final int min) {
        super(Type.INTEGER, key, category);
        this.value = this.defaultValue = value;
        this.min = min;
    }

    public IntOption(final String key, final String category, final int value, final int min, final int max) {
        super(Type.INTEGER, key, category);
        this.value = this.defaultValue = value;
        this.min = min;
        this.max = max;
    }

    public void setValue(int value) {
        if (min != null && value < min) {
            value = min;
        }
        if (max != null && value > max) {
            value = max;
        }
        this.value = value;
    }

    @Override
    public void write(final FriendlyByteBuf buf) {
        buf.writeVarInt(value);
    }

    @Override
    public void read(final FriendlyByteBuf buf) {
        value = buf.readVarInt();
    }

    @Override
    public JsonElement write() {
        return new JsonPrimitive(value);
    }

    @Override
    public void read(final JsonElement element) {
        value = element.getAsInt();
    }
}
