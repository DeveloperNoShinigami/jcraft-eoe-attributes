package net.arna.jcraft.client.renderer.entity.stands;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mod.azure.azurelib.cache.object.BakedGeoModel;
import mod.azure.azurelib.renderer.GeoEntityRenderer;
import net.arna.jcraft.client.model.entity.stand.ChariotRequiemModel;
import net.arna.jcraft.client.renderer.entity.layer.SCROutlineLayer;
import net.arna.jcraft.common.entity.stand.ChariotRequiemEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * The {@link GeoEntityRenderer} for {@link ChariotRequiemEntity}
 * @see ChariotRequiemModel
 */
public class ChariotRequiemRenderer extends GeoEntityRenderer<ChariotRequiemEntity> {
    public ChariotRequiemRenderer(final EntityRendererProvider.Context renderManager) {
        super(renderManager, new ChariotRequiemModel());
        addRenderLayer(new SCROutlineLayer(this));
    }

    @Override
    public void actuallyRender(final PoseStack poseStack, final ChariotRequiemEntity animatable, final BakedGeoModel model, final RenderType renderType, final MultiBufferSource bufferSource, final VertexConsumer buffer, final boolean isReRender, final float partialTick, final int packedLight, final int packedOverlay, final float red, final float green, final float blue, final float alpha) {
        final float a = StandEntityRenderer.getAlpha(animatable, partialTick);
        super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, a);
    }
}
