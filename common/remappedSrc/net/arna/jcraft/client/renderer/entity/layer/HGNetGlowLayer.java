package net.arna.jcraft.client.renderer.entity.layer;

import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.entity.projectile.HGNetEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.List;
import java.util.stream.IntStream;

public class HGNetGlowLayer extends GeoRenderLayer<HGNetEntity> {
    private static final ResourceLocation MODEL = new ResourceLocation(JCraft.MOD_ID, "geo/hg_nets.geo.json");
    private static final List<ResourceLocation> skins = IntStream.range(0, 4).mapToObj(
            i -> JCraft.id("textures/entity/hg_nets/glow_" + i + ".png")).toList();

    public HGNetGlowLayer(GeoRenderer<HGNetEntity> entityRendererIn) {
        super(entityRendererIn);
    }

    @Override
    public void render(PoseStack matrixStackIn, HGNetEntity animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTicks, int packedLightIn, int packedOverlay) {
        if (animatable.isCharged()) {
            RenderType cameo = RenderType.eyes(skins.get(animatable.getSkin()));

            matrixStackIn.pushPose();
            getRenderer().reRender(getDefaultBakedModel(animatable), matrixStackIn, bufferSource, animatable, cameo,
                    bufferSource.getBuffer(cameo), partialTicks, packedLightIn, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1f);
            matrixStackIn.popPose();
        }
    }
}
