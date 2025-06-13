package net.arna.jcraft.client.renderer.entity.stands;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mod.azure.azurelib.cache.object.BakedGeoModel;
import mod.azure.azurelib.renderer.GeoEntityRenderer;
import net.arna.jcraft.client.model.entity.stand.MandomModel;
import net.arna.jcraft.common.entity.stand.MandomEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import static net.arna.jcraft.client.renderer.entity.stands.StandEntityRenderer.getAlpha;

public class MandomRenderer extends GeoEntityRenderer<MandomEntity> {
    public MandomRenderer(final EntityRendererProvider.Context context) {
        super(context, new MandomModel());
    }

    @Override
    public void actuallyRender(PoseStack poseStack, final MandomEntity
                                       animatable, BakedGeoModel model, RenderType renderType,
                               MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick,
                               int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {

        float a = getAlpha(animatable, partialTick);

        // Check if we're in first person view
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.options.getCameraType().isFirstPerson() && minecraft.player != null) {
            // Apply 50% transparency in first person
            a = Math.min(a, 0.5f);
        }

        super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick,
                packedLight, packedOverlay, red, green, blue, a);
    }

    @Override
    public RenderType getRenderType(MandomEntity animatable, ResourceLocation texture,
                                    MultiBufferSource bufferSource, float partialTick) {
        // Use entityTranslucentCull to properly render transparency without seeing through water
        return RenderType.entityTranslucentCull(texture);
    }
}