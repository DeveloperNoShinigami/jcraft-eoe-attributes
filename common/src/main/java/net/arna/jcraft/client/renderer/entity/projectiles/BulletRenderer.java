package net.arna.jcraft.client.renderer.entity.projectiles;

import net.arna.jcraft.client.model.entity.BulletModel;
import net.arna.jcraft.common.entity.projectile.BulletProjectile;
import net.arna.jcraft.common.entity.projectile.LifeDetectorEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.renderer.GeoEntityRenderer;


public class BulletRenderer extends GeoEntityRenderer<BulletProjectile> {
    public BulletRenderer(EntityRendererFactory.Context renderManagerIn) {
        super(renderManagerIn, new BulletModel()); // 3x1x1 px cuboid model
    }
    @Override
    public RenderLayer getRenderType(BulletProjectile animatable, Identifier texture, VertexConsumerProvider bufferSource, float partialTick) {
        return RenderLayer.getEntitySolid(texture);
    }

    @Override
    public void render(BulletProjectile animatable, float yaw, float partialTick, MatrixStack poseStack, VertexConsumerProvider bufferSource, int packedLight) {
        poseStack.push();
        float scale = animatable.getCaliber() * 0.016f; // 62.5mm/px
        poseStack.scale(scale, scale, scale);
        super.render(animatable, yaw, partialTick, poseStack, bufferSource, packedLight);
        poseStack.pop();
    }
}
