package net.arna.jcraft.client.renderer.entity.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.entity.stand.ShadowTheWorldEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import java.util.List;
import java.util.stream.IntStream;

public class STWGlowLayer extends AbstractRenderLayer<ShadowTheWorldEntity> {

    private static final List<ResourceLocation> skins = IntStream.range(0, 4).mapToObj(
            i -> JCraft.id("textures/entity/stands/shadow_the_world/"+ "shade_" + i + ".png")).toList();

    /*@Override
    public void render(final PoseStack poseStack, final ShadowTheWorldEntity animatable, final BakedGeoModel bakedModel, final RenderType renderType,
                       final MultiBufferSource bufferSource, final VertexConsumer buffer, final float partialTick, final int packedLight, final int packedOverlay) {
        final RenderType cameo = RenderType.entityTranslucentEmissive(skins.get(animatable.getSkin()));

        poseStack.pushPose();
        final float tick = (animatable.tickCount + partialTick) * 3.1415f / 10.0f;
        final float mod = 0.015f;
        /*
        if (animatable.isAnimatedDesummoning()) {
            final float desummonProgress = (7 - animatable.getDesummonTime() + partialTick) * 0.04f;
            mod += desummonProgress;
            tick *= desummonProgress;
        }
         *//*
        final float x = Mth.sin(tick) * mod,
                y = Mth.cos(tick) * mod,
                z = Mth.cos(tick) * mod;
        poseStack.translate(x, y, z);
        getRenderer().reRender(bakedModel, poseStack, bufferSource, animatable, cameo,
                bufferSource.getBuffer(cameo), partialTick, 15728640, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
        poseStack.translate(-x * 2, -y * 2, -z * 2);
        getRenderer().reRender(bakedModel, poseStack, bufferSource, animatable, cameo,
                bufferSource.getBuffer(cameo), partialTick, 15728640, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
        poseStack.popPose();
    }*/
}
