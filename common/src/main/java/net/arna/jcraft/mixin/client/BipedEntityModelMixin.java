package net.arna.jcraft.mixin.client;

import net.arna.jcraft.client.util.JClientUtils;
import net.arna.jcraft.common.component.living.CommonHitPropertyComponent;
import net.arna.jcraft.common.entity.stand.StandEntity;
import net.arna.jcraft.common.util.JUtils;
import net.arna.jcraft.platform.JComponentPlatformUtils;
import net.arna.jcraft.registry.JItemRegistry;
import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.arna.jcraft.common.util.JUtils.DEG_TO_RAD;

@Mixin(HumanoidModel.class)
public abstract class BipedEntityModelMixin<T extends LivingEntity> {
    @Shadow
    @Final
    public ModelPart head;
    @Shadow
    @Final
    public ModelPart hat;
    @Shadow
    @Final
    public ModelPart body;
    @Shadow
    @Final
    public ModelPart rightArm;
    @Shadow
    @Final
    public ModelPart leftArm;
    @Shadow
    @Final
    public ModelPart rightLeg;
    @Shadow
    @Final
    public ModelPart leftLeg;
    @Shadow
    public
    HumanoidModel.ArmPose leftArmPose;
    @Shadow
    public
    HumanoidModel.ArmPose rightArmPose;

    @Inject(method = "setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/model/geom/ModelPart;copyFrom(Lnet/minecraft/client/model/geom/ModelPart;)V",
            shift = At.Shift.BEFORE))
    public void jcraft$setAngles(T livingEntity, float f, float g, float h, float i, float j, CallbackInfo info) {
        CommonHitPropertyComponent hitProperties = JComponentPlatformUtils.getHitProperties(livingEntity);
        long endHitAnimTime = hitProperties.endHitAnimTime();
        if (endHitAnimTime > 0) {
            JClientUtils.animateHit(hitProperties.getHitAnimation(), livingEntity.isAlive() ? endHitAnimTime : 0, hitProperties.getRandomRotation(), head, hat, body, rightArm, leftArm, rightLeg, leftLeg);
            return;
        }

        if (livingEntity.isHolding(JItemRegistry.FV_REVOLVER.get())) {
            AnimationUtils.animateCrossbowHold(rightArm, leftArm, head, livingEntity.getMainArm() == HumanoidArm.RIGHT);
        }

        if (livingEntity.getPose() == Pose.STANDING) {
            JClientUtils.resetPartAngles(body);

            if (livingEntity.getFirstPassenger() instanceof StandEntity<?, ?> stand) {
                switch (stand.getStandType()) {
                    case STAR_PLATINUM -> {
                        // RAD_TO_DEG = pi/180
                        if (leftArmPose == HumanoidModel.ArmPose.EMPTY) {
                            leftArm.xRot = 0;
                            leftArm.yRot = -15 * DEG_TO_RAD;
                            leftArm.zRot = 5 * DEG_TO_RAD;
                        }

                        if (rightArmPose == HumanoidModel.ArmPose.EMPTY) {
                            rightArm.zRot = 15 * DEG_TO_RAD;
                            rightArm.xRot *= 0.5F;
                        }
                    }

                    case THE_WORLD -> {
                        // Arms near hips, the DIO pose in HFTF
                        if (leftArmPose == HumanoidModel.ArmPose.EMPTY) {
                            leftArm.yRot = 15 * DEG_TO_RAD;
                            leftArm.zRot = 2 * DEG_TO_RAD;
                        }

                        if (rightArmPose == HumanoidModel.ArmPose.EMPTY) {
                            rightArm.yRot = -15 * DEG_TO_RAD;
                            rightArm.zRot = -2 * DEG_TO_RAD;
                        }

                        if (!livingEntity.isSprinting()) {
                            leftArm.xRot -= 10F * DEG_TO_RAD;
                            rightArm.xRot -= 10F * DEG_TO_RAD;
                            body.xRot -= 10F * DEG_TO_RAD;

                            leftLeg.z -= 2F;
                            rightLeg.z -= 2F;

                            leftArm.z += 0.25F;
                            rightArm.z += 0.25F;
                            leftArm.x += 0.5F;
                            rightArm.x -= 0.5F;
                        }
                    }

                    case KING_CRIMSON -> { // Back towards KC
                        if (JUtils.deltaPos(livingEntity).horizontalDistanceSqr() <= 0) {
                            body.yRot += 30 * DEG_TO_RAD;

                            if (leftArmPose == HumanoidModel.ArmPose.EMPTY) {
                                leftArm.yRot += 30 * DEG_TO_RAD;
                                leftArm.z -= 2.1F;
                            }

                            if (rightArmPose == HumanoidModel.ArmPose.EMPTY || rightArmPose == HumanoidModel.ArmPose.ITEM) {
                                rightArm.yRot += 30 * DEG_TO_RAD;
                                rightArm.z += 2.1F;
                            }

                            leftLeg.z -= 1F;
                            rightLeg.z += 1.5F;

                            rightLeg.yRot += 45 * DEG_TO_RAD;
                        }
                    }

                    case KILLER_QUEEN, KILLER_QUEEN_BITES_THE_DUST -> {
                        if (JUtils.deltaPos(livingEntity).horizontalDistanceSqr() <= 0) {
                            if (leftArmPose == HumanoidModel.ArmPose.EMPTY) {
                                leftArm.yRot += 15 * DEG_TO_RAD;
                                leftArm.xRot -= 15 * DEG_TO_RAD;
                                leftArm.zRot += 45 * DEG_TO_RAD;
                            }

                            if (rightArmPose == HumanoidModel.ArmPose.EMPTY) {
                                rightArm.yRot -= 15 * DEG_TO_RAD;
                                rightArm.xRot -= 15 * DEG_TO_RAD;
                                rightArm.zRot -= 45 * DEG_TO_RAD;
                            }
                        }

                        body.xRot -= 5F * DEG_TO_RAD;
                        leftLeg.z -= 1F;
                        rightLeg.z -= 1F;
                    }

                    case THE_WORLD_OVER_HEAVEN, GOLD_EXPERIENCE_REQUIEM -> {
                        // Floating
                        float heightOffset = 1.0f + Mth.sin(h / 10);
                        head.y -= heightOffset;
                        body.y -= heightOffset;

                        leftArm.y -= heightOffset;
                        rightArm.y -= heightOffset;

                        // Leaning while moving
                        float speedInfluence = (float) JUtils.deltaPos(livingEntity).horizontalDistance() * 45f * DEG_TO_RAD;

                        body.xRot += speedInfluence;

                        leftLeg.y -= heightOffset + 1f + Mth.sin(speedInfluence) * 6f;
                        leftLeg.z += Mth.sin(speedInfluence) * 12f - 2F;
                        rightLeg.y -= heightOffset + Mth.sin(speedInfluence) * 6f;
                        rightLeg.z += Mth.sin(speedInfluence) * 12f;

                        rightLeg.xRot = speedInfluence;
                        leftLeg.xRot = 15 * DEG_TO_RAD + speedInfluence;

                        leftArm.xRot *= 0.25f;
                        rightArm.xRot *= 0.25f;

                        // One arm stretched out
                        if (leftArmPose == HumanoidModel.ArmPose.EMPTY) {
                            leftArm.zRot = -45 * DEG_TO_RAD + Mth.sin(h / 10) / 8f;
                            leftArm.xRot = speedInfluence;
                        }

                        if (rightArmPose == HumanoidModel.ArmPose.EMPTY) {
                            rightArm.xRot = speedInfluence;
                        }
                    }
                }
            }
        }
    }
}
