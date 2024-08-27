package net.arna.jcraft.client.renderer.entity.layer;

import mod.azure.azurelib.cache.object.BakedGeoModel;
import mod.azure.azurelib.renderer.GeoRenderer;
import mod.azure.azurelib.renderer.layer.GeoRenderLayer;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.entity.stand.TheSunEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.List;
import java.util.stream.IntStream;

public class SunGlowLayer extends GeoRenderLayer<TheSunEntity> {
    private static final List<ResourceLocation> skins = IntStream.range(0, 4).mapToObj(
            i -> JCraft.id("textures/entity/stands/the_sun/glow_" + i + ".png")).toList();

    public SunGlowLayer(GeoRenderer<TheSunEntity> entityRendererIn) {
        super(entityRendererIn);
    }

    @Override
    public void render(PoseStack poseStack, TheSunEntity animatable, BakedGeoModel bakedModel, RenderType renderType,
                       MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        RenderType cameo = RenderType.eyes(skins.get(animatable.getSkin()));

        getRenderer().reRender(bakedModel, poseStack, bufferSource, animatable, cameo,
                bufferSource.getBuffer(cameo), partialTick, 15728640, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1f);
    }
}
