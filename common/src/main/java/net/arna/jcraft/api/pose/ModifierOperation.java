package net.arna.jcraft.api.pose;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.floats.FloatBinaryOperator;
import net.arna.jcraft.common.util.JCodecUtils;
import net.minecraft.client.model.geom.ModelPart;

public enum ModifierOperation {
    SET((base, value) -> value),
    ADD(Float::sum),
    MULTIPLY((base, value) -> base * value);

    public static final Codec<ModifierOperation> CODEC = JCodecUtils.createEnumCodec(ModifierOperation.class);

    private final FloatBinaryOperator op;

    ModifierOperation(final FloatBinaryOperator op) {
        this.op = op;
    }

    public void apply(final ModelPart part, final ModelPartProperty property, final float value) {
        property.set(part, op.apply(property.get(part), value));
    }
}
