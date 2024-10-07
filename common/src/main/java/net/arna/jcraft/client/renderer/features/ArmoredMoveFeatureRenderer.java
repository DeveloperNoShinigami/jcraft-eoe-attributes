package net.arna.jcraft.client.renderer.features;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.arna.jcraft.platform.JComponentPlatformUtils;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

public class ArmoredMoveFeatureRenderer<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {
    public ArmoredMoveFeatureRenderer(final EntityRendererProvider.Context context, final LivingEntityRenderer<T, M> entityRenderer) {
        super(entityRenderer);
    }

    @Override
    public void render(final PoseStack matrixStack, final MultiBufferSource vertexConsumers, final int light, final T entity, final float limbAngle, final float limbDistance, final float tickDelta, final float animationProgress, final float headYaw, final float headPitch) {
        if (entity.isInvisible()) {
            return;
        }

        final float armoredHitTicks = (float) JComponentPlatformUtils.getMiscData(entity).getArmoredHitTicks();
        final float flashTime = Mth.lerp(1.0F - tickDelta, armoredHitTicks - 1, armoredHitTicks) / 10.0F;

        if (flashTime <= 0.0F) {
            return;
        }

        final M model = getParentModel();
        final VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderType.lightning());

        matrixStack.pushPose();
        final float s = 0.75F + flashTime - flashTime * flashTime * flashTime * flashTime;
        matrixStack.scale(s, s, s);
        model.renderToBuffer(matrixStack, vertexConsumer, 0xff, OverlayTexture.NO_OVERLAY, 1.0F, 0.5F + flashTime / 2.0F, flashTime, flashTime);
        matrixStack.popPose();
    }
}
