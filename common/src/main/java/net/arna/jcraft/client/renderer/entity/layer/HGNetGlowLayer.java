package net.arna.jcraft.client.renderer.entity.layer;

import mod.azure.azurelib.model.AzBone;
import mod.azure.azurelib.render.AzRendererPipelineContext;
import mod.azure.azurelib.render.layer.AzRenderLayer;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.entity.projectile.HGNetEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

@Environment(EnvType.CLIENT)
public class HGNetGlowLayer extends AbstractRenderLayer<HGNetEntity> {
    private static final List<ResourceLocation> skins = IntStream.range(0, 4).mapToObj(
            i -> JCraft.id("textures/entity/hg_nets/glow_" + i + ".png")).toList();

    /*
    @Override
    public void render(final PoseStack poseStack, final HGNetEntity animatable, final BakedGeoModel bakedModel, final RenderType renderType,
                       final MultiBufferSource bufferSource, final VertexConsumer buffer, final float partialTicks, final int packedLight, final int packedOverlay) {
        if (animatable.isCharged()) {
            final RenderType cameo = RenderType.eyes(skins.get(animatable.getSkin()));

            getRenderer().reRender(bakedModel, poseStack, bufferSource, animatable, cameo,
                    bufferSource.getBuffer(cameo), partialTicks, 15728640, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1f);
        }
    }*/
}
