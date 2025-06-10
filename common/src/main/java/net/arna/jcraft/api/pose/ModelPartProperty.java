package net.arna.jcraft.api.pose;

import com.mojang.serialization.Codec;
import net.arna.jcraft.common.util.JCodecUtils;
import net.minecraft.client.model.geom.ModelPart;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

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

    private final Function<ModelPart, Float> getter;
    private final BiConsumer<ModelPart, Float> setter;

    ModelPartProperty(final Function<ModelPart, Float> getter, final BiConsumer<ModelPart, Float> setter) {
        this.getter = getter;
        this.setter = setter;
    }

    public float get(final ModelPart part) {
        return getter.apply(part);
    }

    public void set(final ModelPart part, final float value) {
        setter.accept(part, value);
    }
}
