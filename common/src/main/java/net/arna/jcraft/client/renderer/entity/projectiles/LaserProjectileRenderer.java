package net.arna.jcraft.client.renderer.entity.projectiles;

import net.arna.jcraft.client.model.entity.LaserProjectileModel;
import net.arna.jcraft.common.entity.projectile.LaserProjectile;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

/**
 * The {@link GeoProjectileRenderer} for {@link LaserProjectile}.
 * @see LaserProjectileModel
 */
public class LaserProjectileRenderer extends GeoProjectileRenderer<LaserProjectile> {
    public LaserProjectileRenderer(final EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new LaserProjectileModel());
    }

    @Override
    public RenderType getRenderType(final LaserProjectile animatable, final ResourceLocation texture, final MultiBufferSource bufferSource, final float partialTick) {
        return RenderType.entityTranslucent(texture);
    }
}
