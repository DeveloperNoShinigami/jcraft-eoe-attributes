package net.arna.jcraft.common.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import lombok.Getter;
import net.minecraft.network.FriendlyByteBuf;

public class FloatOption extends ConfigOption {
    @Getter
    private float value;
    @Getter
    private final float defaultValue;
    @Getter
    private Float min, max;

    public FloatOption(final String key, final String category, final float value) {
        super(Type.FLOAT, key, category);
        this.value = this.defaultValue = value;
    }

    public FloatOption(final String key, final String category, final float value, final float min) {
        super(Type.FLOAT, key, category);
        this.value = this.defaultValue = value;
        this.min = min;
    }

    public FloatOption(final String key, final String category, final float value, final float min, final float max) {
        super(Type.FLOAT, key, category);
        this.value = this.defaultValue = value;
        this.min = min;
        this.max = max;
    }

    public void setValue(float value) {
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
        buf.writeFloat(value);
    }

    @Override
    public void read(final FriendlyByteBuf buf) {
        value = buf.readFloat();
    }

    @Override
    public JsonElement write() {
        return new JsonPrimitive(value);
    }

    @Override
    public void read(final JsonElement element) {
        value = element.getAsFloat();
    }
}
