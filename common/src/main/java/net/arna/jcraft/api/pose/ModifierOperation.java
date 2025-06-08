package net.arna.jcraft.api.pose;

import com.mojang.serialization.Codec;
import net.arna.jcraft.common.util.JCodecUtils;
import net.minecraft.client.model.geom.ModelPart;

public enum ModifierOperation {
    SET((base, value) -> value),
    ADD(Float::sum),
    MULTIPLY((base, value) -> base * value);

    public static final Codec<ModifierOperation> CODEC = JCodecUtils.createEnumCodec(ModifierOperation.class);

    private final FloatOp op;

    ModifierOperation(FloatOp op) {
        this.op = op;
    }

    public void apply(ModelPart part, ModelPartProperty property, float value) {
        property.set(part, op.get(property.get(part), value));
    }

    private interface FloatOp {
        float get(float baseValue, float value);
    }
}
