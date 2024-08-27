package net.arna.jcraft.client.renderer.entity.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mod.azure.azurelib.cache.object.BakedGeoModel;
import mod.azure.azurelib.renderer.GeoRenderer;
import mod.azure.azurelib.renderer.layer.GeoRenderLayer;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.entity.stand.MagiciansRedEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;


public class MRGlowLayer extends GeoRenderLayer<MagiciansRedEntity> {
    private static final ResourceLocation LAYER = new ResourceLocation(JCraft.MOD_ID, "textures/entity/stands/magicians_red/glow.png");

    public MRGlowLayer(GeoRenderer<MagiciansRedEntity> entityRendererIn) {
        super(entityRendererIn);
    }

    @Override
    public void render(PoseStack poseStack, MagiciansRedEntity animatable, BakedGeoModel bakedModel, RenderType renderType,
                       MultiBufferSource bufferSource, VertexConsumer bufferIn, float partialTick, int packedLight, int packedOverlay) {
        RenderType cameo = RenderType.eyes(LAYER);

        getRenderer().reRender(bakedModel, poseStack, bufferSource, animatable, cameo,
                bufferSource.getBuffer(cameo), partialTick, 15728640, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1f);
    }
}
