package net.arna.jcraft.client.renderer.entity.stands;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mod.azure.azurelib.cache.object.BakedGeoModel;
import mod.azure.azurelib.renderer.GeoEntityRenderer;
import net.arna.jcraft.client.model.entity.ChariotRequiemModel;
import net.arna.jcraft.client.renderer.entity.layer.SCROutlineLayer;
import net.arna.jcraft.common.entity.stand.ChariotRequiemEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class ChariotRequiemRenderer extends GeoEntityRenderer<ChariotRequiemEntity> {
    public ChariotRequiemRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ChariotRequiemModel());
        addRenderLayer(new SCROutlineLayer(this));
    }

    @Override
    public void actuallyRender(PoseStack poseStack, ChariotRequiemEntity animatable, BakedGeoModel model, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        float a = StandEntityRenderer.getAlpha(animatable, partialTick);
        super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, a);
    }
}
