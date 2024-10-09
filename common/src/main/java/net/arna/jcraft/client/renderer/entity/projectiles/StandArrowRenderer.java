package net.arna.jcraft.client.renderer.entity.projectiles;

import mod.azure.azurelib.renderer.GeoEntityRenderer;
import net.arna.jcraft.client.model.entity.StandArrowModel;
import net.arna.jcraft.common.entity.projectile.StandArrowEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * The {@link GeoProjectileRenderer} for {@link StandArrowEntity}.
 * @see StandArrowModel
 */
public class StandArrowRenderer extends GeoProjectileRenderer<StandArrowEntity> {
    public StandArrowRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new StandArrowModel());
    }
}
