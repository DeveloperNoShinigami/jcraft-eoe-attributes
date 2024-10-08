package net.arna.jcraft.client.renderer.entity.stands;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import lombok.NonNull;
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
import org.jetbrains.annotations.Nullable;

/**
 * The {@link GeoEntityRenderer} for {@link TheSunEntity}.
 * @see TheSunModel
 */
public class SunRenderer extends GeoEntityRenderer<TheSunEntity> {
    public SunRenderer(final EntityRendererProvider.Context renderManager) {
        super(renderManager, new TheSunModel());
        addRenderLayer(new SunGlowLayer(this));
    }

    @Override
    protected int getBlockLightLevel(final @NonNull TheSunEntity entity, final @NonNull BlockPos pos) {
        return 15;
    }

    @Override
    protected int getSkyLightLevel(final @NonNull TheSunEntity entity, final @NonNull BlockPos pos) {
        return 15;
    }

    //TODO: translucent layer that isn't layered over and has no shading
    @Override
    public RenderType getRenderType(final TheSunEntity animatable, final ResourceLocation texture, final @Nullable MultiBufferSource bufferSource, final float partialTick) {
        return RenderType.dragonExplosionAlpha(texture);
    }

    @Override
    public void preRender(final PoseStack poseStack, final TheSunEntity animatable, final BakedGeoModel model, final MultiBufferSource bufferSource, final VertexConsumer buffer, final boolean isReRender, final float partialTick, final int packedLight, final int packedOverlay, final float red, final float green, final float blue, final float alpha) {
        if (!isReRender) {
            final float scale = animatable.getScale(partialTick);
            poseStack.scale(scale, scale, scale);
        }

        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
