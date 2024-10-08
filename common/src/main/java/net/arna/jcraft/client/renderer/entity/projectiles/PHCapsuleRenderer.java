package net.arna.jcraft.client.renderer.entity.projectiles;

import net.arna.jcraft.client.model.entity.PHCapsuleModel;
import net.arna.jcraft.common.entity.projectile.PHCapsuleProjectile;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * The {@link GeoProjectileRenderer} for {@link PHCapsuleProjectile}.
 * @see PHCapsuleModel
 */
public class PHCapsuleRenderer extends GeoProjectileRenderer<PHCapsuleProjectile> {
    public PHCapsuleRenderer(final EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new PHCapsuleModel());
    }
}
