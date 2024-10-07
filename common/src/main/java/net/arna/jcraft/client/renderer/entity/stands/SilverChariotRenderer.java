package net.arna.jcraft.client.renderer.entity.stands;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import mod.azure.azurelib.cache.object.BakedGeoModel;
import net.arna.jcraft.client.model.entity.stand.SilverChariotModel;
import net.arna.jcraft.client.renderer.entity.layer.SCRapierLayer;
import net.arna.jcraft.common.entity.stand.SilverChariotEntity;
import net.arna.jcraft.common.util.JUtils;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class SilverChariotRenderer extends StandEntityRenderer<SilverChariotEntity> {

    public SilverChariotRenderer(final EntityRendererProvider.Context context) {
        super(context, new SilverChariotModel());
        addRenderLayer(new SCRapierLayer(this));
    }

    // Adds ability to change render alpha


    @Override
    public void actuallyRender(final PoseStack poseStack, final SilverChariotEntity animatable, final BakedGeoModel model, final RenderType renderType, final MultiBufferSource bufferSource, final VertexConsumer buffer, final boolean isReRender, final float partialTick, final int packedLight, final int packedOverlay, final float red, final float green, final float blue, final float alpha) {
        final float a = StandEntityRenderer.getAlpha(animatable, partialTick);
        super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, a);
        if (animatable.getMode() == SilverChariotEntity.Mode.ARMORLESS && animatable.hasUser()) {
            for (double i = 0; i < 3; i++) {
                renderAfterImage(animatable.getUserOrThrow(), JUtils.deltaPos(animatable).scale(i * 2.0), 1f,
                        model, animatable, partialTick, RenderType.entityNoOutline(getTextureLocation(animatable)),
                        poseStack, bufferSource, buffer, packedLight, packedOverlay, red, green, blue,
                        a);
            }
        }
    }

    private void renderAfterImage(LivingEntity user, Vec3 velocity, float a, BakedGeoModel model, SilverChariotEntity animatable,
                             float partialTicks, RenderType type, PoseStack matrixStack, MultiBufferSource renderTypeBuffer,
                             VertexConsumer vertexBuilder, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        matrixStack.pushPose();

        matrixStack.mulPose(Axis.YP.rotationDegrees(user.yBodyRot));

        double y = velocity.y;
        if (-0.2 < -y && y < 0.2) {
            y = 0;
        }

        matrixStack.translate(velocity.x, y, velocity.z);
        matrixStack.mulPose(Axis.YP.rotationDegrees(-user.yBodyRot));
        super.actuallyRender(matrixStack, animatable, model, type, renderTypeBuffer, vertexBuilder, false, partialTicks, packedLightIn, packedOverlayIn, red, green, blue, a);
        matrixStack.popPose();
    }
}
