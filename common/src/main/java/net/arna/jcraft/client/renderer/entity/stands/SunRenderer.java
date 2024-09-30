package net.arna.jcraft.client.renderer.entity.stands;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mod.azure.azurelib.cache.object.BakedGeoModel;
import mod.azure.azurelib.renderer.GeoEntityRenderer;
import net.arna.jcraft.client.model.entity.stand.TheSunModel;
import net.arna.jcraft.client.renderer.entity.layer.SunGlowLayer;
import net.arna.jcraft.common.entity.stand.TheSunEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SunRenderer extends GeoEntityRenderer<TheSunEntity> {
    public SunRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new TheSunModel());
        addRenderLayer(new SunGlowLayer(this));
    }

    @Override
    protected int getBlockLightLevel(@NotNull TheSunEntity entity, @NotNull BlockPos pos) {
        return 15;
    }

    @Override
    protected int getSkyLightLevel(@NotNull TheSunEntity entity, @NotNull BlockPos pos) {
        return 15;
    }

    //TODO: translucent layer that isn't layered over and has no shading
    @Override
    public RenderType getRenderType(TheSunEntity animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.dragonExplosionAlpha(texture);
    }

    private static float lerpScale(TheSunEntity animatable, float partialTick) {
        return Mth.lerp(partialTick, animatable.prevScale, animatable.curScale);
    }

    @Override
    public void preRender(PoseStack poseStack, TheSunEntity animatable, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        if (!isReRender) {
            float scale = lerpScale(animatable, partialTick);
            poseStack.scale(scale, scale, scale);
        }

        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
