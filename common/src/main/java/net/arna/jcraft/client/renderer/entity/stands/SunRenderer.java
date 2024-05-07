package net.arna.jcraft.client.renderer.entity.stands;

import com.mojang.blaze3d.systems.RenderSystem;
import net.arna.jcraft.client.model.entity.TheSunModel;
import net.arna.jcraft.client.renderer.entity.layer.SunGlowLayer;
import net.arna.jcraft.common.entity.stand.TheSunEntity;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexConsumers;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityPose;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

import java.awt.*;
import java.util.Collections;

public class SunRenderer extends GeoEntityRenderer<TheSunEntity> {
    public SunRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new TheSunModel());
        addRenderLayer(new SunGlowLayer(this));
    }

    @Override
    protected int getBlockLight(TheSunEntity entity, BlockPos pos) { return 15; }

    @Override
    protected int getSkyLight(TheSunEntity entity, BlockPos pos) { return 15; }

    //TODO: translucent layer that isn't layered over and has no shading

    @Override
    public RenderLayer getRenderType(TheSunEntity animatable, Identifier texture, @Nullable VertexConsumerProvider bufferSource, float partialTick) {
        return RenderLayer.getEntityAlpha(texture);
    }

    // Not inlined for sake of debugging
    private static float lerpScale(TheSunEntity animatable, float partialTick) {
        float scale = MathHelper.lerp(partialTick, animatable.prevScale, animatable.getScale());
        return scale;
    }

    @Override
    public void actuallyRender(MatrixStack poseStack, TheSunEntity animatable, BakedGeoModel model, RenderLayer renderType, VertexConsumerProvider bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        packedLight = 255;

        poseStack.push();

        float scale = lerpScale(animatable, partialTick);
        poseStack.scale(scale, scale, scale);

        this.modelRenderTranslations = new Matrix4f(poseStack.peek().getPositionMatrix());

        float lerpBodyRot = MathHelper.lerpAngleDegrees(partialTick, animatable.prevBodyYaw, animatable.bodyYaw);
        float lerpHeadRot = MathHelper.lerpAngleDegrees(partialTick, animatable.prevHeadYaw, animatable.headYaw);
        float netHeadYaw = lerpHeadRot - lerpBodyRot;

        if (animatable.getPose() == EntityPose.SLEEPING) {
            Direction bedDirection = animatable.getSleepingDirection();

            if (bedDirection != null) {
                float eyePosOffset = animatable.getEyeHeight(EntityPose.STANDING) - 0.1F;

                poseStack.translate(-bedDirection.getOffsetX() * eyePosOffset, 0, -bedDirection.getOffsetZ() * eyePosOffset);
            }
        }

        float ageInTicks = animatable.age + partialTick;
        float limbSwingAmount = 0;
        float limbSwing = 0;

        applyRotations(animatable, poseStack, ageInTicks, lerpBodyRot, partialTick);

        AnimationState<TheSunEntity> predicate = new AnimationState<TheSunEntity>(animatable, limbSwing, limbSwingAmount, partialTick, false);


        this.model.setCustomAnimations(animatable, getInstanceId(animatable), predicate);

        //poseStack.translate(0, 0.01f, 0);
        RenderSystem.setShaderTexture(0, getTextureLocation(animatable));

        var renderColor = getRenderColor(animatable, partialTick, packedLight);


        if (!animatable.isInvisibleTo(MinecraftClient.getInstance().player)) {
            VertexConsumer glintBuffer = bufferSource.getBuffer(RenderLayer.getDirectEntityGlint());
            VertexConsumer translucentBuffer = bufferSource
                    .getBuffer(RenderLayer.getEntityTranslucentCull(getTextureLocation(animatable)));

            super.actuallyRender(
                    poseStack,
                    animatable,
                    model,
                    renderType,
                    bufferSource,
                    glintBuffer != translucentBuffer ? VertexConsumers.union(glintBuffer, translucentBuffer) : null,
                    isReRender,
                    partialTick,
                    packedLight,
                    getPackedOverlay(animatable, 0), renderColor.getRed() / 255f,
                    renderColor.getGreen() / 255f, renderColor.getBlue() / 255f,
                    renderColor.getAlpha() / 255f
            );
        }

        poseStack.pop();

    }
}
