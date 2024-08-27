package net.arna.jcraft.client.renderer.entity.stands;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mod.azure.azurelib.cache.object.BakedGeoModel;
import net.arna.jcraft.client.model.entity.StandEntityModel;
import net.arna.jcraft.common.entity.stand.GEREntity;
import net.arna.jcraft.common.entity.stand.StandType;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class GERRenderer extends StandEntityRenderer<GEREntity> {
    public GERRenderer(EntityRendererProvider.Context context) {
        super(context, new StandEntityModel<>(StandType.GOLD_EXPERIENCE_REQUIEM));
    }

    @Override
    public void actuallyRender(PoseStack poseStack, GEREntity animatable, BakedGeoModel model, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        float a = StandEntityRenderer.getAlpha(animatable, partialTick);
        super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, a);
    }
}
