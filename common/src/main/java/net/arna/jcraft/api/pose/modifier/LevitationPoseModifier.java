package net.arna.jcraft.api.pose.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.*;
import net.arna.jcraft.api.pose.ModelType;
import net.arna.jcraft.common.util.JUtils;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

import static net.minecraft.util.Mth.DEG_TO_RAD;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class LevitationPoseModifier implements IPoseModifier {
    public static final String ID = "levitation";
    public static final Codec<LevitationPoseModifier> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.optionalFieldOf("amplitude", 1.0f).forGetter(LevitationPoseModifier::getAmplitude),
            Codec.FLOAT.optionalFieldOf("verticalShift", 1.0f).forGetter(LevitationPoseModifier::getVerticalShift),
            Codec.FLOAT.optionalFieldOf("period", 20 * Mth.PI).forGetter(LevitationPoseModifier::getPeriod)
    ).apply(instance, LevitationPoseModifier::new));
    @Builder.Default
    private float amplitude = 1.0f;
    @Builder.Default
    private float verticalShift = 1.0f;
    @Builder.Default
    private float period = 20 * Mth.PI;
    @Getter(lazy = true, value = AccessLevel.PRIVATE)
    private final float multiplier = Mth.TWO_PI / period;

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public boolean isModelSupported(final ModelType<?> modelType) {
        return modelType == ModelType.HUMANOID;
    }

    @Override
    public void apply(final Model model, final LivingEntity user, final float age) {
        HumanoidModel<?> hModel = (HumanoidModel<?>) model;

        // Bobbing up and down while floating
        float wave = Mth.sin(age * getMultiplier() - user.getId());
        float heightOffset = verticalShift + wave * amplitude;
        hModel.head.y -= heightOffset;
        hModel.body.y -= heightOffset;

        hModel.leftArm.y -= heightOffset;
        hModel.rightArm.y -= heightOffset;

        hModel.leftLeg.y -= heightOffset;
        hModel.rightLeg.y -= heightOffset;

        // Leaning while moving
        double velocity = JUtils.deltaPos(user).horizontalDistance();
        float speedInfluence = (float) (Math.tanh(velocity) * 90f * DEG_TO_RAD);

        hModel.body.xRot += speedInfluence;

        hModel.leftLeg.y -= 1f + Mth.sin(speedInfluence) * 6f;
        hModel.leftLeg.z += Mth.sin(speedInfluence) * 12f - 2f;
        hModel.rightLeg.y -= Mth.sin(speedInfluence) * 6f;
        hModel.rightLeg.z += Mth.sin(speedInfluence) * 12f;

        hModel.rightLeg.xRot = speedInfluence;
        hModel.leftLeg.xRot = 15 * DEG_TO_RAD + speedInfluence;

        hModel.leftArm.xRot *= 0.25f;
        hModel.rightArm.xRot *= 0.25f;

        // One arm stretched out
        if (hModel.leftArmPose == HumanoidModel.ArmPose.EMPTY) {
            hModel.leftArm.zRot = -45 * DEG_TO_RAD + wave * amplitude / 8f;
            hModel.leftArm.xRot = speedInfluence;
        }

        if (hModel.rightArmPose == HumanoidModel.ArmPose.EMPTY) {
            hModel.rightArm.xRot = speedInfluence;
        }
    }
}
