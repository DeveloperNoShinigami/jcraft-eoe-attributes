package net.arna.jcraft.client.renderer.entity.projectiles;

import net.arna.jcraft.client.model.entity.BloodProjectileModel;
import net.arna.jcraft.common.entity.projectile.BloodProjectile;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;


public class BloodProjectileRenderer extends GeoProjectileRenderer<BloodProjectile> {
    public BloodProjectileRenderer(final EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new BloodProjectileModel());
    }

    @Override
    public RenderType getRenderType(final BloodProjectile animatable, final ResourceLocation texture, final MultiBufferSource bufferSource, final float partialTick) {
        return RenderType.entityTranslucent(texture);
    }
}
