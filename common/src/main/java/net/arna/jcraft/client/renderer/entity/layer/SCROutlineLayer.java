package net.arna.jcraft.client.renderer.entity.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mod.azure.azurelib.cache.object.BakedGeoModel;
import mod.azure.azurelib.renderer.GeoRenderer;
import mod.azure.azurelib.renderer.layer.GeoRenderLayer;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.entity.stand.ChariotRequiemEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.stream.IntStream;

public class SCROutlineLayer extends GeoRenderLayer<ChariotRequiemEntity> {
    private static final List<ResourceLocation> skins = IntStream.range(0, 4).mapToObj(
            i -> JCraft.id("textures/entity/stands/chariot_requiem/outline" + i + ".png")).toList();

    public SCROutlineLayer(GeoRenderer<ChariotRequiemEntity> entityRendererIn) {
        super(entityRendererIn);
    }

    @Override
    public void render(final PoseStack poseStack, final ChariotRequiemEntity animatable, final BakedGeoModel bakedModel, final RenderType renderType,
                       final MultiBufferSource bufferSource, final VertexConsumer buffer, final float partialTick, final int packedLight, final int packedOverlay) {
        final RenderType cameo = RenderType.eyes(skins.get(animatable.getSkin()));

        getRenderer().reRender(bakedModel, poseStack, bufferSource, animatable, cameo,
                bufferSource.getBuffer(cameo), partialTick, packedLight, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1f);
    }
}
