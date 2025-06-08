package net.arna.jcraft.api.pose.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.*;
import net.arna.jcraft.common.util.JUtils;
import net.minecraft.client.model.HumanoidModel;
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
    public void apply(HumanoidModel<?> model, LivingEntity user, float age) {
        // Bobbing up and down while floating
        float wave = Mth.sin(age * getMultiplier() - user.getId());
        float heightOffset = verticalShift + wave * amplitude;
        model.head.y -= heightOffset;
        model.body.y -= heightOffset;

        model.leftArm.y -= heightOffset;
        model.rightArm.y -= heightOffset;

        model.leftLeg.y -= heightOffset;
        model.rightLeg.y -= heightOffset;

        // Leaning while moving
        double velocity = JUtils.deltaPos(user).horizontalDistance();
        float speedInfluence = (float) (Math.tanh(velocity) * 90f * DEG_TO_RAD);

        model.body.xRot += speedInfluence;

        model.leftLeg.y -= 1f + Mth.sin(speedInfluence) * 6f;
        model.leftLeg.z += Mth.sin(speedInfluence) * 12f - 2f;
        model.rightLeg.y -= Mth.sin(speedInfluence) * 6f;
        model.rightLeg.z += Mth.sin(speedInfluence) * 12f;

        model.rightLeg.xRot = speedInfluence;
        model.leftLeg.xRot = 15 * DEG_TO_RAD + speedInfluence;

        model.leftArm.xRot *= 0.25f;
        model.rightArm.xRot *= 0.25f;

        // One arm stretched out
        if (model.leftArmPose == HumanoidModel.ArmPose.EMPTY) {
            model.leftArm.zRot = -45 * DEG_TO_RAD + wave * amplitude / 8f;
            model.leftArm.xRot = speedInfluence;
        }

        if (model.rightArmPose == HumanoidModel.ArmPose.EMPTY) {
            model.rightArm.xRot = speedInfluence;
        }
    }
}
