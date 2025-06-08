package net.arna.jcraft.api.pose;

import com.mojang.serialization.Codec;
import net.arna.jcraft.common.util.JCodecUtils;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;

import java.util.function.Function;

public enum HumanoidModelPart {
    HEAD(model -> model.head),
    HAT(model -> model.hat),
    BODY(model -> model.body),
    RIGHT_ARM(model -> model.rightArm),
    LEFT_ARM(model -> model.leftArm),
    RIGHT_LEG(model -> model.rightLeg),
    LEFT_LEG(model -> model.leftLeg);

    public static final Codec<HumanoidModelPart> CODEC = JCodecUtils.createEnumCodec(HumanoidModelPart.class);

    private final Function<HumanoidModel<?>, ModelPart> partGetter;

    HumanoidModelPart(Function<HumanoidModel<?>, ModelPart> partGetter) {
        this.partGetter = partGetter;
    }

    public ModelPart getPart(HumanoidModel<?> model) {
        return partGetter.apply(model);
    }
}
