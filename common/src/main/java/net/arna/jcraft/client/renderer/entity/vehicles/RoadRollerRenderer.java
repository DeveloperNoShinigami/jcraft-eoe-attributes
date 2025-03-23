package net.arna.jcraft.client.renderer.entity.vehicles;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import lombok.NonNull;
import mod.azure.azurelib.renderer.GeoEntityRenderer;
import net.arna.jcraft.client.model.entity.RoadRollerModel;
import net.arna.jcraft.common.entity.vehicle.RoadRollerEntity;
import net.arna.jcraft.common.gravity.api.GravityChangerAPI;
import net.arna.jcraft.common.gravity.util.RotationUtil;
import net.arna.jcraft.common.util.JUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;

import java.util.Random;

/**
 * The {@link GeoEntityRenderer} for {@link RoadRollerEntity}.
 * @see RoadRollerModel
 */
public class RoadRollerRenderer extends GeoEntityRenderer<RoadRollerEntity> {

    public RoadRollerRenderer(final EntityRendererProvider.Context context) {
        super(context, new RoadRollerModel());
    }

    @Override
    public RenderType getRenderType(final RoadRollerEntity animatable, final ResourceLocation texture, final @Nullable MultiBufferSource bufferSource, final float partialTick) {
        return RenderType.entityTranslucent(getTextureLocation(animatable));
    }

    private final Random random = new Random();
    @Override
    public void render(final RoadRollerEntity entity, final float yaw, final float partialTick, final PoseStack matrixStack, final @NonNull MultiBufferSource vertexConsumerProvider, final int i) {
        matrixStack.pushPose();

        float lerpYaw = Mth.rotLerp(partialTick, entity.yRotO, entity.getYRot());
        matrixStack.mulPose(Axis.YN.rotationDegrees(lerpYaw));

        if (entity.isVehicle()) {
            if (!Minecraft.getInstance().isPaused()) {
                model.getBone("exhaust").ifPresent(bone -> {
                    final Direction gravity = GravityChangerAPI.getGravityDirection(entity);

                    Vector3d localPos = bone.getLocalPosition();
                    localPos = localPos.rotateAxis(lerpYaw * JUtils.DEG_TO_RAD, gravity.getStepX(), gravity.getStepY(), gravity.getStepZ());

                    Vector3d localRot = bone.getRotationVector().mul(0.1d);
                    localRot = localRot.rotateAxis(lerpYaw * JUtils.DEG_TO_RAD, gravity.getStepX(), gravity.getStepY(), gravity.getStepZ());

                    final Vec3 worldPos = RotationUtil.vecWorldToPlayer(
                                    localPos.x, localPos.y, localPos.z,
                                    gravity)
                            .add(entity.position()
                            );

                    entity.getCommandSenderWorld().addParticle(ParticleTypes.SMOKE,
                            worldPos.x, worldPos.y, worldPos.z,
                            localRot.x + random.nextDouble() * 0.05d - 0.025d,
                            localRot.y + random.nextDouble() * 0.05d - 0.025d,
                            localRot.z + random.nextDouble() * 0.05d - 0.025d
                    );
                });
            }
        }

        super.render(entity, yaw, partialTick, matrixStack, vertexConsumerProvider, i);
        matrixStack.popPose();
    }
}