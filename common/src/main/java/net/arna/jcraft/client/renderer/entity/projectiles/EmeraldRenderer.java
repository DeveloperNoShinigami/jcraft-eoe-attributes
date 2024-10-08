package net.arna.jcraft.client.renderer.entity.projectiles;

import net.arna.jcraft.client.model.JProjectileModel;
import net.arna.jcraft.common.entity.projectile.EmeraldProjectile;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * The {@link GeoProjectileRenderer} for {@link EmeraldProjectile}.
 */
public class EmeraldRenderer extends GeoProjectileRenderer<EmeraldProjectile> {
    public EmeraldRenderer(final EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new JProjectileModel<>("emerald"));
    }
}
