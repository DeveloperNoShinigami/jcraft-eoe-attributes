package net.arna.jcraft.client.renderer.entity.stands;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mod.azure.azurelib.cache.object.BakedGeoModel;
import net.arna.jcraft.client.model.entity.KingCrimsonModel;
import net.arna.jcraft.common.entity.stand.KingCrimsonEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class KingCrimsonRenderer extends StandEntityRenderer<KingCrimsonEntity> {

    public KingCrimsonRenderer(EntityRendererProvider.Context context) {
        super(context, new KingCrimsonModel());
        //this.addLayer(new KCTELayer(this));
    }

    @Override
    public void actuallyRender(PoseStack poseStack, KingCrimsonEntity animatable, BakedGeoModel model, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        float a = StandEntityRenderer.getAlpha(animatable, partialTick);
        super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, a);
    }

    @Override
    protected float getGreen(KingCrimsonEntity stand, float green, float alpha) {
        return green - (1f - alpha);
    }

    @Override
    protected float getBlue(KingCrimsonEntity stand, float blue, float alpha) {
        return blue - (1f - alpha);
    }
}
