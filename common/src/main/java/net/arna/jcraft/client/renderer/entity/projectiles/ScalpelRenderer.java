package net.arna.jcraft.client.renderer.entity.projectiles;

import net.arna.jcraft.client.model.entity.ScalpelModel;
import net.arna.jcraft.common.entity.projectile.ScalpelProjectile;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

/**
 * The {@link GeoProjectileRenderer} for {@link ScalpelProjectile}.
 * @see ScalpelModel
 */
public class ScalpelRenderer extends GeoProjectileRenderer<ScalpelProjectile> {
    public ScalpelRenderer(final EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new ScalpelModel());
    }

    @Override
    public RenderType getRenderType(final ScalpelProjectile animatable, final ResourceLocation texture, final MultiBufferSource bufferSource, final float partialTick) {
        return RenderType.entityTranslucent(texture);
    }
}