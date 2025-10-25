package net.arna.jcraft.client.renderer.entity.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.entity.stand.TheHandEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.stream.IntStream;

public class HandErasureLayer extends GeoRenderLayer<TheHandEntity> {
    private static final List<ResourceLocation> skins = IntStream.range(0, 4).mapToObj(
            i -> JCraft.id("textures/entity/stands/the_hand/erase" + i + ".png")).toList();
    private static final List<ResourceLocation> skins_outer = IntStream.range(0, 4).mapToObj(
            i -> JCraft.id("textures/entity/stands/the_hand/erase_outer" + i + ".png")).toList();

    public HandErasureLayer(final GeoRenderer<TheHandEntity> entityRendererIn) {
        super(entityRendererIn);
    }

    @Override
    public void render(final PoseStack poseStack, final TheHandEntity animatable, final BakedGeoModel bakedModel, final RenderType renderType,
                       final MultiBufferSource bufferSource, final VertexConsumer buffer, final float partialTick, final int packedLight, final int packedOverlay) {
        final int skin = animatable.getSkin();
        RenderType cameo = RenderType.dragonExplosionAlpha(skins.get(skin));
        getRenderer().reRender(bakedModel, poseStack, bufferSource, animatable, cameo,
                bufferSource.getBuffer(cameo), partialTick, 15728640, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1f);
        cameo = RenderType.eyes(skins_outer.get(skin));
        getRenderer().reRender(bakedModel, poseStack, bufferSource, animatable, cameo,
                bufferSource.getBuffer(cameo), partialTick, 15728640, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1f);
    }
}
