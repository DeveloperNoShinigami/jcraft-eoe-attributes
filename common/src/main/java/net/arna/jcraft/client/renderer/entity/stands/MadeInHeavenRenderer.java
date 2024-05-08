package net.arna.jcraft.client.renderer.entity.stands;

import net.arna.jcraft.client.model.entity.MadeInHeavenModel;
import net.arna.jcraft.common.entity.stand.MadeInHeavenEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import software.bernie.geckolib.cache.object.BakedGeoModel;

public class MadeInHeavenRenderer extends StandEntityRenderer<MadeInHeavenEntity> {

    public MadeInHeavenRenderer(EntityRendererFactory.Context context) {
        super(context, new MadeInHeavenModel());
    }

    @Override
    public void actuallyRender(MatrixStack poseStack, MadeInHeavenEntity animatable, BakedGeoModel model, RenderLayer renderType, VertexConsumerProvider bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);

        if (!animatable.getAfterimage()) {
            return;
        }

        float aa = getAlpha(animatable, partialTick) - 0.5f;
        if (aa < 0) {
            aa = 0;
        }

        Vec3d baseVel = Vec3d.ZERO;
        float bodyYaw = animatable.bodyYaw;
        if (animatable.hasUser()) {
            LivingEntity user = animatable.getUserOrThrow();
            baseVel = user.getVelocity();
            bodyYaw = user.bodyYaw;
        }

        for (int i = 0; i <= 3; ++i) {
            renderAfter(baseVel.multiply(i), bodyYaw, aa * (1f / i), model, animatable, partialTick,
                    RenderLayer.getEntityNoOutline(getTextureLocation(animatable)), poseStack, bufferSource,
                    buffer, packedLight, packedOverlay, red, green, blue, alpha);
        }
    }

    private void renderAfter(Vec3d velocity, float bodyYaw, float aa, BakedGeoModel model, MadeInHeavenEntity animatable,
                             float partialTicks, RenderLayer type, MatrixStack matrixStack,
                             VertexConsumerProvider renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn,
                             int packedOverlayIn, float red, float green, float blue, float alpha) {
        matrixStack.push();

        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(bodyYaw));
        matrixStack.translate(velocity.x, -velocity.y, velocity.z);
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-bodyYaw));
        super.actuallyRender(matrixStack, animatable, model, type, renderTypeBuffer, vertexBuilder, false, partialTicks, packedLightIn, packedOverlayIn, red, green, blue, aa);
        matrixStack.pop();
    }
}
