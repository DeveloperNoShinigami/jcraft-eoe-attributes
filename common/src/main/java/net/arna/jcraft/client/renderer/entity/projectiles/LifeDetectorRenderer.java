package net.arna.jcraft.client.renderer.entity.projectiles;

import net.arna.jcraft.client.model.entity.LifeDetectorModel;
import net.arna.jcraft.common.entity.projectile.LifeDetectorEntity;
import net.arna.jcraft.common.entity.projectile.MeteorProjectile;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.joml.Quaternionf;
import software.bernie.geckolib.renderer.GeoEntityRenderer;


public class LifeDetectorRenderer extends GeoEntityRenderer<LifeDetectorEntity> {
    public LifeDetectorRenderer(EntityRendererFactory.Context renderManagerIn) { super(renderManagerIn, new LifeDetectorModel()); }
    protected int getBlockLight(LifeDetectorEntity entityIn, BlockPos partialTicks) { return 15; }

    @Override
    public RenderLayer getRenderType(LifeDetectorEntity animatable, Identifier texture, VertexConsumerProvider bufferSource, float partialTick) {
        return RenderLayer.getEyes(texture);
    }

    @Override
    public void render(LifeDetectorEntity animatable, float yaw, float partialTick, MatrixStack poseStack, VertexConsumerProvider bufferSource, int packedLight) {
        poseStack.push();
        poseStack.multiply(Quaternionf.fromEulerXyz(3.1415f, 3.1415f, 0)); // Why is this necessary???
        super.render(animatable, yaw, partialTick, poseStack, bufferSource, packedLight);
        poseStack.pop();
    }
}
