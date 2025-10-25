package net.arna.jcraft.client.renderer.entity.projectiles;

import net.arna.jcraft.client.model.JProjectileModel;
import net.arna.jcraft.common.entity.projectile.ScalpelProjectile;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

/**
 * The {@link ProjectileRenderer} for {@link ScalpelProjectile}.
 */
public class ScalpelRenderer extends ProjectileRenderer<ScalpelProjectile> {
    public ScalpelRenderer(final EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, "scalpel");
    }

    /*@Override
    public RenderType getRenderType(final ScalpelProjectile animatable, final ResourceLocation texture, final MultiBufferSource bufferSource, final float partialTick) {
        return RenderType.entityTranslucent(texture);
    }*/
}