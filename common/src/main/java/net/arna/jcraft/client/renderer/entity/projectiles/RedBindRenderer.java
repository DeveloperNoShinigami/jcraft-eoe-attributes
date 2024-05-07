package net.arna.jcraft.client.renderer.entity.projectiles;

import net.arna.jcraft.client.model.entity.RedBindModel;
import net.arna.jcraft.common.entity.projectile.BloodProjectile;
import net.arna.jcraft.common.entity.projectile.RedBindEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class RedBindRenderer extends GeoEntityRenderer<RedBindEntity> {
    public RedBindRenderer(EntityRendererFactory.Context renderManagerIn) {
        super(renderManagerIn, new RedBindModel());
    }

    @Override
    public RenderLayer getRenderType(RedBindEntity animatable, Identifier texture, VertexConsumerProvider bufferSource, float partialTick) {
        return RenderLayer.getEntityTranslucent(texture);
    }

    @Override
    public void render(RedBindEntity animatable, float yaw, float partialTick, MatrixStack poseStack, VertexConsumerProvider bufferSource, int packedLight) {
        float xz = animatable.getBoundWidth();

        poseStack.push();
        poseStack.scale(xz, 1f, xz);
        super.render(animatable, yaw, partialTick, poseStack, bufferSource, packedLight);
        poseStack.pop();
    }
}
