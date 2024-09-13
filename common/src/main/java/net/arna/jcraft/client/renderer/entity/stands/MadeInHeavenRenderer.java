package net.arna.jcraft.client.renderer.entity.stands;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import mod.azure.azurelib.cache.object.BakedGeoModel;
import net.arna.jcraft.client.model.entity.stand.MadeInHeavenModel;
import net.arna.jcraft.common.entity.stand.MadeInHeavenEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class MadeInHeavenRenderer extends StandEntityRenderer<MadeInHeavenEntity> {

    public MadeInHeavenRenderer(EntityRendererProvider.Context context) {
        super(context, new MadeInHeavenModel());
    }

    @Override
    public void actuallyRender(PoseStack poseStack, MadeInHeavenEntity animatable, BakedGeoModel model, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        float a = getAlpha(animatable, partialTick);
        super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, a);

        if (!animatable.getAfterimage()) {
            return;
        }

        float aa = a - 0.5f;
        if (aa < 0) {
            aa = 0;
        }

        Vec3 baseVel = Vec3.ZERO;
        float bodyYaw = animatable.yBodyRot;
        if (animatable.hasUser()) {
            LivingEntity user = animatable.getUserOrThrow();
            baseVel = user.getDeltaMovement();
            bodyYaw = user.yBodyRot;
        }

        for (int i = 0; i <= 3; ++i) {
            renderAfterImage(baseVel.scale(i), bodyYaw, aa * (1f / i), model, animatable, partialTick,
                    RenderType.entityNoOutline(getTextureLocation(animatable)), poseStack, bufferSource,
                    buffer, packedLight, packedOverlay, red, green, blue, alpha);
        }
    }

    private void renderAfterImage(Vec3 velocity, float bodyYaw, float aa, BakedGeoModel model, MadeInHeavenEntity animatable,
                             float partialTicks, RenderType type, PoseStack matrixStack,
                             MultiBufferSource renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn,
                             int packedOverlayIn, float red, float green, float blue, float alpha) {
        matrixStack.pushPose();

        matrixStack.mulPose(Axis.YP.rotationDegrees(bodyYaw));
        matrixStack.translate(velocity.x, -velocity.y, velocity.z);
        matrixStack.mulPose(Axis.YP.rotationDegrees(-bodyYaw));
        super.actuallyRender(matrixStack, animatable, model, type, renderTypeBuffer, vertexBuilder, false, partialTicks, packedLightIn, packedOverlayIn, red, green, blue, aa);
        matrixStack.popPose();
    }
}
