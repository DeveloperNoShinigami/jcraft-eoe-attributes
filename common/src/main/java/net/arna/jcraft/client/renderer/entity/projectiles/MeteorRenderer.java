package net.arna.jcraft.client.renderer.entity.projectiles;

import net.arna.jcraft.client.model.entity.MeteorModel;
import net.arna.jcraft.common.entity.projectile.MeteorProjectile;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.renderer.GeoEntityRenderer;


public class MeteorRenderer extends GeoEntityRenderer<MeteorProjectile> {

    public MeteorRenderer(EntityRendererFactory.Context renderManagerIn) {
        super(renderManagerIn, new MeteorModel());
    }

    @Override
    public RenderLayer getRenderType(MeteorProjectile animatable, Identifier texture, VertexConsumerProvider bufferSource, float partialTick) {
        return RenderLayer.getEyes(texture);
    }

    @Override
    public void render(MeteorProjectile animatable, float yaw, float partialTick, MatrixStack poseStack, VertexConsumerProvider bufferSource, int packedLight) {
        super.render(animatable, yaw, partialTick, poseStack, bufferSource, packedLight);
    }
}
