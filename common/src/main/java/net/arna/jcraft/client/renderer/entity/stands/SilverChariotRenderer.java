package net.arna.jcraft.client.renderer.entity.stands;

import net.arna.jcraft.client.model.entity.SilverChariotModel;
import net.arna.jcraft.client.renderer.entity.layer.SCRapierLayer;
import net.arna.jcraft.common.entity.stand.SilverChariotEntity;
import net.arna.jcraft.common.util.JUtils;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import software.bernie.geckolib.cache.object.BakedGeoModel;

public class SilverChariotRenderer extends StandEntityRenderer<SilverChariotEntity> {

    public SilverChariotRenderer(EntityRendererFactory.Context context) {
        super(context, new SilverChariotModel());
        addRenderLayer(new SCRapierLayer(this));
    }

    // Adds ability to change render alpha


    @Override
    public void actuallyRender(MatrixStack poseStack, SilverChariotEntity animatable, BakedGeoModel model, RenderLayer renderType, VertexConsumerProvider bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
        if (animatable.getMode() == SilverChariotEntity.Mode.ARMORLESS && animatable.hasUser()) {
            for (double i = 0; i < 3; i++) {
                renderAfter(animatable.getUserOrThrow(), JUtils.deltaPos(animatable).multiply(i * 2.0), 1f,
                        model, animatable, partialTick, RenderLayer.getEntityNoOutline(getTextureLocation(animatable)),
                        poseStack, bufferSource, buffer, packedLight, packedOverlay, red, green, blue,
                        alpha);
            }
        }
    }

    private void renderAfter(LivingEntity user, Vec3d velocity, float a, BakedGeoModel model, SilverChariotEntity animatable,
                             float partialTicks, RenderLayer type, MatrixStack matrixStack, VertexConsumerProvider renderTypeBuffer,
                             VertexConsumer vertexBuilder, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        matrixStack.push();

        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(user.bodyYaw));

        double y = velocity.y;
        if (-0.2 < -y && y < 0.2) {
            y = 0;
        }

        matrixStack.translate(velocity.x, y, velocity.z);
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-user.bodyYaw));
        super.actuallyRender(matrixStack, animatable, model, type, renderTypeBuffer, vertexBuilder, false, partialTicks, packedLightIn, packedOverlayIn, red, green, blue, a);
        matrixStack.pop();
    }
}
