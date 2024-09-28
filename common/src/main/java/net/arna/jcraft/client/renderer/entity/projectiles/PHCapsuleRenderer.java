package net.arna.jcraft.client.renderer.entity.projectiles;

import net.arna.jcraft.client.model.entity.PHCapsuleModel;
import net.arna.jcraft.common.entity.projectile.PHCapsuleProjectile;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class PHCapsuleRenderer extends GeoProjectileRenderer<PHCapsuleProjectile> {
    public PHCapsuleRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new PHCapsuleModel());
    }
}
