package net.arna.jcraft.client.renderer.entity.stands;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mod.azure.azurelib.cache.object.BakedGeoModel;
import net.arna.jcraft.client.model.entity.ShadowTheWorldModel;
import net.arna.jcraft.client.renderer.entity.layer.STWGlowLayer;
import net.arna.jcraft.common.entity.stand.ShadowTheWorldEntity;
import net.arna.jcraft.common.util.JUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class ShadowTheWorldRenderer extends StandEntityRenderer<ShadowTheWorldEntity> {
    public ShadowTheWorldRenderer(EntityRendererProvider.Context context) {
        super(context, new ShadowTheWorldModel());
        addRenderLayer(new STWGlowLayer(this));
    }

    @Override
    public RenderType getRenderType(ShadowTheWorldEntity animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        Minecraft mcClient = Minecraft.getInstance();
        return mcClient.options.getCameraType().isFirstPerson() && mcClient.player != null && JUtils.getStand(mcClient.player) == animatable ?
                RenderType.entityNoOutline(texture) : RenderType.entityTranslucentCull(texture);
    }

    @Override
    public void actuallyRender(PoseStack poseStack, ShadowTheWorldEntity animatable, BakedGeoModel model, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        float a = StandEntityRenderer.getAlpha(animatable, partialTick) / 2.0f; // Other half is glow layer
        super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, 15728640, packedOverlay, red, green, blue, a);
    }
}
