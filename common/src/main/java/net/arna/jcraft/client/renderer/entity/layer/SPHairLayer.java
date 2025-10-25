package net.arna.jcraft.client.renderer.entity.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.client.renderer.entity.stands.StandEntityRenderer;
import net.arna.jcraft.common.entity.stand.AbstractStarPlatinumEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.stream.IntStream;

@Environment(EnvType.CLIENT)
public class SPHairLayer extends AbstractRenderLayer<AbstractStarPlatinumEntity<?, ?>> {
    private static final List<ResourceLocation> skins = IntStream.range(0, 4).mapToObj(
            i -> JCraft.id("textures/entity/stands/star_platinum/" + (i == 0 ? "default" : "skin" + i) + "_hair.png")).toList();

    /*@Override
    public void render(final PoseStack poseStack, final AbstractStarPlatinumEntity<?, ?> animatable, final BakedGeoModel bakedModel, final RenderType renderType,
                       final MultiBufferSource bufferSource, final VertexConsumer buffer, final float partialTick, final int packedLight, final int packedOverlay) {
        if (StandEntityRenderer.standIsFirstPersonViewers(animatable)) return;

        final RenderType cameo = RenderType.eyes(skins.get(animatable.getSkin()));

        getRenderer().reRender(bakedModel, poseStack, bufferSource, animatable, cameo,
                bufferSource.getBuffer(cameo), partialTick, packedLight, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1f);
    }*/
}
