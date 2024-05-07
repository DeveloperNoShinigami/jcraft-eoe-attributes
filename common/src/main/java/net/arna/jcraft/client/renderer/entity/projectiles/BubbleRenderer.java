package net.arna.jcraft.client.renderer.entity.projectiles;

import net.arna.jcraft.client.model.entity.BubbleModel;
import net.arna.jcraft.common.entity.projectile.BubbleProjectile;
import net.arna.jcraft.common.entity.projectile.SunBeamProjectile;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class BubbleRenderer extends GeoEntityRenderer<BubbleProjectile> {
    public BubbleRenderer(EntityRendererFactory.Context renderManagerIn) {
        super(renderManagerIn, new BubbleModel());
    }

    @Override
    public RenderLayer getRenderType(BubbleProjectile animatable, Identifier texture, VertexConsumerProvider bufferSource, float partialTick) {
        return RenderLayer.getEntityTranslucent(texture);
    }
}
