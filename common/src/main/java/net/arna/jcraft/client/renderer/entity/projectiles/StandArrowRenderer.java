package net.arna.jcraft.client.renderer.entity.projectiles;

import net.arna.jcraft.client.model.JProjectileModel;
import net.arna.jcraft.common.entity.projectile.StandArrowEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * The {@link GeoProjectileRenderer} for {@link StandArrowEntity}.
 */
public class StandArrowRenderer extends GeoProjectileRenderer<StandArrowEntity> {
    public StandArrowRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new JProjectileModel<>("stand_arrow"));
    }
}
