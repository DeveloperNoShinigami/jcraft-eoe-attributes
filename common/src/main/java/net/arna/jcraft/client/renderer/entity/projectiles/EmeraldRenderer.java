package net.arna.jcraft.client.renderer.entity.projectiles;

import net.arna.jcraft.client.model.entity.EmeraldModel;
import net.arna.jcraft.common.entity.projectile.EmeraldProjectile;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class EmeraldRenderer extends GeoProjectileRenderer<EmeraldProjectile> {
    public EmeraldRenderer(EntityRendererFactory.Context renderManagerIn) {
        super(renderManagerIn, new EmeraldModel());
    }
}
