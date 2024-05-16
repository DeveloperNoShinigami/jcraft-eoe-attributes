package net.arna.jcraft.client.renderer.entity.projectiles;

import net.arna.jcraft.client.model.entity.LaserProjectileModel;
import net.arna.jcraft.common.entity.projectile.LaserProjectile;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class LaserProjectileRenderer extends GeoProjectileRenderer<LaserProjectile> {
    public LaserProjectileRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new LaserProjectileModel());
    }

    @Override
    public RenderType getRenderType(LaserProjectile animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(texture);
    }
}
