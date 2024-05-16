package net.arna.jcraft.client.util;

import mod.azure.azurelib.core.animatable.model.CoreGeoBone;
import mod.azure.azurelib.core.animation.AnimationProcessor;
import net.arna.jcraft.client.model.entity.StandEntityModel;
import net.arna.jcraft.common.component.living.CommonHitPropertyComponent;
import net.arna.jcraft.common.entity.stand.CreamEntity;
import net.arna.jcraft.common.entity.stand.D4CEntity;
import net.arna.jcraft.common.entity.stand.KingCrimsonEntity;
import net.arna.jcraft.common.entity.stand.StandEntity;
import net.arna.jcraft.common.util.DimensionData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static net.arna.jcraft.common.util.JUtils.RAD_TO_DEG;
import static net.arna.jcraft.common.util.JUtils.deltaPos;

public class JClientUtils {

    // Timestop tracking
    public static List<DimensionData> activeTimestops = new ArrayList<>();

    // Mustn't directly remove the DimensionData due to the possibility of a ConcurrentModificationException
    // Setting the timer to 0 will make the next tick remove it
    public static void removeTimestop(int timestopperId) {
        for (DimensionData timestop : activeTimestops) {
            Entity timestopper = timestop.user;
            if (timestopper.getId() != timestopperId) {
                continue;
            }
            timestop.timer = 0;
            return;
        }
    }

    public static boolean isInTSRange(Vec3 pos) {
        for (DimensionData timeStop : activeTimestops) {
            if (timeStop != null && timeStop.pos.distanceToSqr(pos.x(), pos.y(), pos.z()) <= 65536) {
                return true;
            }
        }
        return false;
    }

    public static boolean isInTSRange(BlockPos pos) {
        for (DimensionData timeStop : activeTimestops) {
            if (timeStop != null && timeStop.pos.distanceToSqr(pos.getX(), pos.getY(), pos.getZ()) <= 65536) {
                return true;
            }
        }
        return false;
    }

    public static int getTicksIfInTSRange(BlockPos pos) {
        for (DimensionData timeStop : activeTimestops) {
            if (timeStop != null && timeStop.pos.distanceToSqr(pos.getX(), pos.getY(), pos.getZ()) <= 65536) {
                return timeStop.timer;
            }
        }
        return 0;
    }

    // Torso/Head rotation for stands
    public static void animateGenericHumanoid(StandEntityModel<?> model, StandEntity<?, ?> entity, LivingEntity player, float partialTick) {
        animateGenericHumanoid(model, entity, player, partialTick, false, false);
    }

    public static void animateGenericHumanoid(StandEntityModel<?> model, StandEntity<?, ?> entity, LivingEntity player, float partialTick, boolean flipBody, boolean flipHead) {
        animateGenericHumanoid(model, entity, player, partialTick, flipBody, flipHead, 0, 0, 90f);
    }

    public static void animateGenericHumanoid(StandEntityModel<?> model, StandEntity<?, ?> entity, LivingEntity player, float partialTick, boolean flipBody, boolean flipHead, float tPO, float hPO) {
        animateGenericHumanoid(model, entity, player, partialTick, flipBody, flipHead, tPO, hPO, 90f);
    }

    public static void animateGenericHumanoid(StandEntityModel<?> model, StandEntity<?, ?> entity, LivingEntity player, float partialTick, boolean flipBody, boolean flipHead, float tPO, float hPO, float velInfluence) {
        float overVel = 0;

        AnimationProcessor<?> animationProcessor = model.getAnimationProcessor();

        if (entity.getMoveStun() < 1) {
            Vec3 playerVel = (entity.isRemote() && !entity.remoteControllable()) ? entity.getDeltaMovement() : deltaPos(player);
            overVel = Mth.clamp((float) playerVel.horizontalDistance() - 0.05f, -1f, 1f);

            // If going backwards
            if (playerVel.normalize().add(entity.getLookAngle()).horizontalDistanceSqr() < playerVel.normalize().horizontalDistanceSqr())
                velInfluence *= -1;

            CoreGeoBone torso = animationProcessor.getBone("torso");
            if (torso != null) {
                float pitch = (180f + overVel * velInfluence) * 3.1415f / 180f;
                if (flipBody) {
                    pitch += 3.1415f;
                    pitch = -pitch;
                }
                torso.setRotX(pitch + tPO);
            }
        }

        if (entity.isBlocking() || entity.isIdle()) { // if in/going to idle, or blocking
            CoreGeoBone head = animationProcessor.getBone("head");
            if (head != null) {
                float headPitch = (player.getXRot() - overVel * velInfluence) * 3.1415f / 180f;
                if (flipHead) headPitch = -headPitch;
                head.setRotX(headPitch + hPO);
            }
        } else if (entity.getMoveStun() > 0) {
            CoreGeoBone torso = animationProcessor.getBone("base");
            if (torso != null) {
                //float torsoPitch = (player.getPitch() * 0.9f) * 3.1415f / 180f;
                //torso.setRotX(torso.getRotX() - torsoPitch);
            }
        }
    }

    public static boolean shouldForceRender(Entity entity) {
        if (entity instanceof D4CEntity d4c && d4c.getState() == D4CEntity.State.FLAG ||
                entity instanceof KingCrimsonEntity kc && kc.getTETime() > 0 && kc.getUser() == Minecraft.getInstance().player) {
            return true;
        }
        return entity instanceof CreamEntity cream && cream.isHalfBall();
    }

    public static boolean shouldNotRender(Entity entity) {
        Entity passenger = entity.getFirstPassenger();
        return passenger instanceof KingCrimsonEntity kc && kc.getTETime() > 0 ||
                passenger instanceof D4CEntity d4c && d4c.getState() == D4CEntity.State.FLAG ||
                passenger instanceof CreamEntity cream && cream.isHalfBall();
    }

    public static void resetPartAngles(ModelPart part) {
        PartPose defaultTransform = part.getInitialPose();
        part.xRot = defaultTransform.xRot;
        part.yRot = defaultTransform.yRot;
        part.zRot = defaultTransform.zRot;
    }

    public static void animateHit(CommonHitPropertyComponent.HitAnimation hitAnimation, long endHitAnimTime, Vec3 randomRotation, ModelPart head, @Nullable ModelPart hat, ModelPart body, ModelPart rightArm, ModelPart leftArm, ModelPart rightLeg, ModelPart leftLeg) {
        if (endHitAnimTime > 20L) {
            endHitAnimTime = 20L;
        }
        float angDegrees = endHitAnimTime * RAD_TO_DEG;

        if (endHitAnimTime <= 1) {
            leftLeg.resetPose();
            rightLeg.resetPose();
            resetPartAngles(body);
        } else {
            body.yRot = (float) (randomRotation.x * angDegrees * 0.35);
            body.zRot = (float) (randomRotation.z * angDegrees * 0.35);
        }

        if (endHitAnimTime == 0) // If dead
        {
            return;
        }

        switch (hitAnimation) {
            case HIGH -> {
                angDegrees *= 1.5F;

                head.xRot += angDegrees;

                body.xRot -= angDegrees;

                leftLeg.z -= endHitAnimTime * 0.25F;
                rightLeg.z -= endHitAnimTime * 0.25F;

                rightArm.zRot += angDegrees;
                leftArm.zRot -= angDegrees;
            }
            case MID -> {
                angDegrees *= 1.5F;

                head.xRot += angDegrees;

                body.xRot += angDegrees;

                leftLeg.z += endHitAnimTime * 0.25F;
                rightLeg.z += endHitAnimTime * 0.25F;
                leftLeg.y -= endHitAnimTime * 0.175F;
                rightLeg.y -= endHitAnimTime * 0.175F;

                rightLeg.xRot -= angDegrees;
                leftLeg.xRot -= angDegrees;
            }
            case LOW -> {
                angDegrees *= 1.5F;

                head.xRot += angDegrees;

                body.xRot += angDegrees;

                leftLeg.z += endHitAnimTime * 0.175F;
                rightLeg.z += endHitAnimTime * 0.175F;
                leftLeg.y -= endHitAnimTime * 0.0875F;
                rightLeg.y -= endHitAnimTime * 0.0875F;

                rightLeg.xRot += angDegrees;
                leftLeg.xRot += angDegrees;

                rightArm.zRot += angDegrees;
                leftArm.zRot -= angDegrees;
            }
            case CRUSH -> {
                body.xRot += angDegrees;

                leftLeg.z += endHitAnimTime * 0.25F;
                rightLeg.z += endHitAnimTime * 0.25F;
                leftLeg.y -= endHitAnimTime * 0.175F;
                rightLeg.y -= endHitAnimTime * 0.175F;

                angDegrees *= 1.75F;

                head.xRot += Mth.sin(endHitAnimTime * 0.1F);

                rightArm.zRot += angDegrees;
                leftArm.zRot -= angDegrees;
                rightLeg.xRot -= angDegrees;
                leftLeg.xRot -= angDegrees;
            }
            case LAUNCH -> {
                //angDegrees *= 4.0F;

                head.xRot += angDegrees;

                body.xRot += angDegrees;

                leftLeg.z += endHitAnimTime * 0.125F;
                rightLeg.z += endHitAnimTime * 0.125F;
                leftLeg.y -= endHitAnimTime * 0.125F;
                rightLeg.y -= endHitAnimTime * 0.125F;

                rightArm.zRot += angDegrees;
                leftArm.zRot -= angDegrees;
                rightLeg.xRot -= angDegrees;
                leftLeg.xRot -= angDegrees;
            }
            case ROLL -> {

            }
        }
    }
}
