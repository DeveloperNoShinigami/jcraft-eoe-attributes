package net.arna.jcraft.client.renderer.entity.projectiles;

import net.arna.jcraft.client.model.entity.EmeraldModel;
import net.arna.jcraft.common.entity.projectile.EmeraldProjectile;
import net.minecraft.client.renderer.entity.EntityRendererProvider;


public class EmeraldRenderer extends GeoProjectileRenderer<EmeraldProjectile> {
    public EmeraldRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new EmeraldModel());
    }
}
