package net.arna.jcraft.client.renderer.entity.stands;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mod.azure.azurelib.cache.object.BakedGeoModel;
import net.arna.jcraft.client.model.entity.stand.KingCrimsonModel;
import net.arna.jcraft.common.entity.stand.KingCrimsonEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class KingCrimsonRenderer extends StandEntityRenderer<KingCrimsonEntity> {

    public KingCrimsonRenderer(final EntityRendererProvider.Context context) {
        super(context, new KingCrimsonModel());
        //this.addLayer(new KCTELayer(this));
    }

    @Override
    public void actuallyRender(final PoseStack poseStack, final KingCrimsonEntity animatable, final BakedGeoModel model, final RenderType renderType, final MultiBufferSource bufferSource, final VertexConsumer buffer, final boolean isReRender, final float partialTick, final int packedLight, final int packedOverlay, final float red, final float green, final float blue, final float alpha) {
        final float a = StandEntityRenderer.getAlpha(animatable, partialTick);
        super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, a);
    }

    @Override
    protected float getGreen(final KingCrimsonEntity stand, final float green, final float alpha) {
        return green - (1f - alpha);
    }

    @Override
    protected float getBlue(final KingCrimsonEntity stand, final float blue, final float alpha) {
        return blue - (1f - alpha);
    }
}
