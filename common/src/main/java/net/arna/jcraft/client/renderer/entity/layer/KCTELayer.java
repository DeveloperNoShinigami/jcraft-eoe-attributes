package net.arna.jcraft.client.renderer.entity.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mod.azure.azurelib.cache.object.BakedGeoModel;
import mod.azure.azurelib.core.animatable.GeoAnimatable;
import mod.azure.azurelib.renderer.GeoRenderer;
import mod.azure.azurelib.renderer.layer.GeoRenderLayer;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.entity.stand.KingCrimsonEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

@SuppressWarnings("rawtypes")
public class KCTELayer extends GeoRenderLayer {
    private static final ResourceLocation MODEL = JCraft.id("geo/box.geo.json");

    @SuppressWarnings("unchecked")
    public KCTELayer(GeoRenderer<?> entityRendererIn) {
        super(entityRendererIn);
    }

    @Override
    public void render(PoseStack matrixStackIn, GeoAnimatable animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTicks, int packedLightIn, int packedOverlay) {
        if (((KingCrimsonEntity) animatable).getTETime() < 1) {
            return;
        }
        //RenderLayer cameo = JCraftClient.TIMEERASE_RENDER_LAYER;
        RenderType cameo = RenderType.cutout();

        matrixStackIn.pushPose();
        matrixStackIn.scale(-4096.0f, -4096.0f, -4096.0f);
        getRenderer().reRender(getDefaultBakedModel(animatable), matrixStackIn, bufferSource, animatable, cameo,
                bufferSource.getBuffer(cameo), partialTicks, packedLightIn, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1f);
        matrixStackIn.popPose();
    }
}
