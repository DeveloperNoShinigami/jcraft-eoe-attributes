package net.arna.jcraft.client.renderer.entity.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mod.azure.azurelib.model.AzBone;
import mod.azure.azurelib.render.AzRendererPipelineContext;
import mod.azure.azurelib.render.layer.AzRenderLayer;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.entity.stand.HorusEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

@Environment(EnvType.CLIENT)
public class HorusEyesLayer extends AbstractRenderLayer<HorusEntity> {
    private static final ResourceLocation LAYER = new ResourceLocation(JCraft.MOD_ID, "textures/entity/stands/horus/eyes.png");

    /*@Override
    public void render(final PoseStack poseStack, final HorusEntity animatable, final BakedGeoModel bakedModel, final RenderType renderType,
                       final MultiBufferSource bufferSource, final VertexConsumer bufferIn, final float partialTick, final int packedLight, final int packedOverlay) {
        final RenderType cameo = RenderType.entityTranslucentEmissive(LAYER);
        getRenderer().reRender(bakedModel, poseStack, bufferSource, animatable, cameo,
                bufferSource.getBuffer(cameo), partialTick, 15728640, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);
    }*/
}
