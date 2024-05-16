package net.arna.jcraft.client.renderer.entity.stands;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexMultiConsumer;
import net.arna.jcraft.client.model.entity.TheSunModel;
import net.arna.jcraft.client.renderer.entity.layer.SunGlowLayer;
import net.arna.jcraft.common.entity.stand.TheSunEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Pose;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.Color;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class SunRenderer extends GeoEntityRenderer<TheSunEntity> {
    public SunRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new TheSunModel());
        addRenderLayer(new SunGlowLayer(this));
    }

    @Override
    protected int getBlockLight(TheSunEntity entity, BlockPos pos) {
        return 15;
    }

    @Override
    protected int getSkyLight(TheSunEntity entity, BlockPos pos) {
        return 15;
    }

    //TODO: translucent layer that isn't layered over and has no shading

    @Override
    public RenderType getRenderType(TheSunEntity animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.dragonExplosionAlpha(texture);
    }

    // Not inlined for sake of debugging
    private static float lerpScale(TheSunEntity animatable, float partialTick) {
        float scale = Mth.lerp(partialTick, animatable.prevScale, animatable.getScale());
        return scale;
    }

    @Override
    public void actuallyRender(PoseStack poseStack, TheSunEntity animatable, BakedGeoModel model, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        packedLight = 255;

        poseStack.pushPose();

        float scale = lerpScale(animatable, partialTick);
        poseStack.scale(scale, scale, scale);

        this.modelRenderTranslations = new Matrix4f(poseStack.last().pose());

        float lerpBodyRot = Mth.rotLerp(partialTick, animatable.yBodyRotO, animatable.yBodyRot);
        float lerpHeadRot = Mth.rotLerp(partialTick, animatable.yHeadRotO, animatable.yHeadRot);
        float netHeadYaw = lerpHeadRot - lerpBodyRot;

        if (animatable.getPose() == Pose.SLEEPING) {
            Direction bedDirection = animatable.getBedOrientation();

            if (bedDirection != null) {
                float eyePosOffset = animatable.getEyeHeight(Pose.STANDING) - 0.1F;

                poseStack.translate(-bedDirection.getStepX() * eyePosOffset, 0, -bedDirection.getStepZ() * eyePosOffset);
            }
        }

        float ageInTicks = animatable.tickCount + partialTick;
        float limbSwingAmount = 0;
        float limbSwing = 0;

        applyRotations(animatable, poseStack, ageInTicks, lerpBodyRot, partialTick);

        AnimationState<TheSunEntity> predicate = new AnimationState<TheSunEntity>(animatable, limbSwing, limbSwingAmount, partialTick, false);


        this.model.setCustomAnimations(animatable, getInstanceId(animatable), predicate);

        //poseStack.translate(0, 0.01f, 0);
        RenderSystem.setShaderTexture(0, getTextureLocation(animatable));

        var renderColor = getRenderColor(animatable, partialTick, packedLight);


        if (!animatable.isInvisibleTo(Minecraft.getInstance().player)) {
            VertexConsumer glintBuffer = bufferSource.getBuffer(RenderType.entityGlintDirect());
            VertexConsumer translucentBuffer = bufferSource
                    .getBuffer(RenderType.entityTranslucentCull(getTextureLocation(animatable)));

            super.actuallyRender(
                    poseStack,
                    animatable,
                    model,
                    renderType,
                    bufferSource,
                    glintBuffer != translucentBuffer ? VertexMultiConsumer.create(glintBuffer, translucentBuffer) : null,
                    isReRender,
                    partialTick,
                    packedLight,
                    getPackedOverlay(animatable, 0), renderColor.getRed() / 255f,
                    renderColor.getGreen() / 255f, renderColor.getBlue() / 255f,
                    renderColor.getAlpha() / 255f
            );
        }

        poseStack.popPose();

    }
}
