package net.arna.jcraft.client.renderer.entity.projectiles;

import net.arna.jcraft.client.model.entity.SunBeamModel;
import net.arna.jcraft.common.entity.projectile.SunBeamProjectile;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class SunBeamRenderer extends GeoProjectileRenderer<SunBeamProjectile> {

    public SunBeamRenderer(EntityRendererFactory.Context renderManagerIn) {
        super(renderManagerIn, new SunBeamModel());
    }

    protected int getBlockLight(SunBeamProjectile entityIn, BlockPos partialTicks) {
        return 15;
    }

    @Override
    public RenderLayer getRenderType(SunBeamProjectile animatable, Identifier texture, VertexConsumerProvider bufferSource, float partialTick) {
        return RenderLayer.getEyes(texture);
    }

    @Override
    public void render(SunBeamProjectile animatable, float yaw, float partialTick, MatrixStack poseStack, VertexConsumerProvider bufferSource, int packedLight) {
        super.render(animatable, yaw, partialTick, poseStack, bufferSource, packedLight);
    }
}
