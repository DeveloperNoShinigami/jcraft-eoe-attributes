package net.arna.jcraft.api.pose;

import com.mojang.serialization.Codec;
import net.arna.jcraft.common.util.JCodecUtils;
import net.minecraft.client.model.geom.ModelPart;

public enum ModelPartProperty {
    X(p -> p.x, (p, v) -> p.x = v),
    Y(p -> p.y, (p, v) -> p.y = v),
    Z(p -> p.z, (p, z) -> p.z = z),
    X_ROT(p -> p.xRot, (p, v) -> p.xRot = v),
    Y_ROT(p -> p.yRot, (p, v) -> p.yRot = v),
    Z_ROT(p -> p.zRot, (p, v) -> p.zRot = v),
    X_SCALE(p -> p.xScale, (p, v) -> p.xScale = v),
    Y_SCALE(p -> p.yScale, (p, v) -> p.yScale = v),
    Z_SCALE(p -> p.zScale, (p, v) -> p.zScale = v);

    public static final Codec<ModelPartProperty> CODEC = JCodecUtils.createEnumCodec(ModelPartProperty.class);

    private final Getter getter;
    private final Setter setter;

    ModelPartProperty(Getter getter, Setter setter) {
        this.getter = getter;
        this.setter = setter;
    }

    public float get(ModelPart part) {
        return getter.get(part);
    }

    public void set(ModelPart part, float value) {
        setter.set(part, value);
    }

    @FunctionalInterface
    private interface Getter {
        float get(ModelPart part);
    }

    @FunctionalInterface
    private interface Setter {
        void set(ModelPart part, float value);
    }
}
