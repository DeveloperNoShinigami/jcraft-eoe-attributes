package net.arna.jcraft.client.renderer.entity.projectiles;

import net.arna.jcraft.client.model.entity.LaserProjectileModel;
import net.arna.jcraft.common.entity.projectile.LaserProjectile;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class LaserProjectileRenderer extends GeoEntityRenderer<LaserProjectile> {
    public LaserProjectileRenderer(EntityRendererFactory.Context renderManagerIn) {
        super(renderManagerIn, new LaserProjectileModel());
    }

    @Override
    public RenderLayer getRenderType(LaserProjectile animatable, Identifier texture, VertexConsumerProvider bufferSource, float partialTick) {
        return RenderLayer.getEntityTranslucent(texture);
    }
}
