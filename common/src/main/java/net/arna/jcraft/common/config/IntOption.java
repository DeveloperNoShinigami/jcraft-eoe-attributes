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

    public IntOption(String key, String category, int value) {
        super(Type.INTEGER, key, category);
        this.value = this.defaultValue = value;
    }

    public IntOption(String key, String category, int value, int min) {
        super(Type.INTEGER, key, category);
        this.value = this.defaultValue = value;
        this.min = min;
    }

    public IntOption(String key, String category, int value, int min, int max) {
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
    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(value);
    }

    @Override
    public void read(FriendlyByteBuf buf) {
        value = buf.readVarInt();
    }

    @Override
    public JsonElement write() {
        return new JsonPrimitive(value);
    }

    @Override
    public void read(JsonElement element) {
        value = element.getAsInt();
    }
}
