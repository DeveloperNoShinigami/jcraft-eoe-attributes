package net.arna.jcraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import lombok.NonNull;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.client.model.entity.TrainingDummyModel;
import net.arna.jcraft.common.entity.TrainingDummyEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

/**
 * Renderer for the TrainingDummyEntity using AzureLib
 */
@Environment(EnvType.CLIENT)
public class TrainingDummyRenderer extends AbstractEntityRenderer<TrainingDummyEntity> {

    public static final String ID = "training_dummy";

    public TrainingDummyRenderer(final @NonNull EntityRendererProvider.Context context) {
        super(context, () -> new EntityAnimator<>(ID), ID);
    }

    /*
    @Override
    public void preRender(PoseStack poseStack, TrainingDummyEntity animatable, BakedGeoModel model,
                          MultiBufferSource bufferSource, VertexConsumer buffer,
                          boolean isReRender, float partialTick, int packedLight, int packedOverlay,
                          float red, float green, float blue, float alpha) {

        // Remove wobble effect - let animations handle it
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick,
                packedLight, packedOverlay, red, green, blue, alpha);
    }*/

    @Override
    public void render(TrainingDummyEntity entity, float entityYaw, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight) {
        // Don't render if invisible
        if (entity.isInvisible()) {
            return;
        }

        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    /*
    @Override
    protected void applyRotations(TrainingDummyEntity animatable, PoseStack poseStack, float ageInTicks,
                                  float rotationYaw, float partialTick, float nativeScale) {
        // Just apply basic rotation
        poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(180.0F - rotationYaw));
    }

    @Override
    protected float getDeathMaxRotation(TrainingDummyEntity animatable) {
        // Training dummies don't die, so no death rotation
        return 0.0F;
    }*/

    @Override
    public boolean shouldRender(TrainingDummyEntity entity, Frustum frustum,
                                double camX, double camY, double camZ) {
        // Only render the model, skip any overlays
        return !entity.isInvisible() && super.shouldRender(entity, frustum, camX, camY, camZ);
    }

    @Override
    public boolean shouldShowName(TrainingDummyEntity entity) {
        return false;  // This might be the method name in GeoEntityRenderer
    }
}