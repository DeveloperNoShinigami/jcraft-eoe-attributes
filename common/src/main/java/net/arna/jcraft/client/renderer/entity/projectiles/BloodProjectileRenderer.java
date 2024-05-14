package net.arna.jcraft.client.renderer.entity.projectiles;

import net.arna.jcraft.client.model.entity.BloodProjectileModel;
import net.arna.jcraft.common.entity.projectile.BloodProjectile;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class BloodProjectileRenderer extends GeoProjectileRenderer<BloodProjectile> {
    public BloodProjectileRenderer(EntityRendererFactory.Context renderManagerIn) {
        super(renderManagerIn, new BloodProjectileModel());
    }

    @Override
    public RenderLayer getRenderType(BloodProjectile animatable, Identifier texture, VertexConsumerProvider bufferSource, float partialTick) {
        return RenderLayer.getEntityTranslucent(texture);
    }
}
