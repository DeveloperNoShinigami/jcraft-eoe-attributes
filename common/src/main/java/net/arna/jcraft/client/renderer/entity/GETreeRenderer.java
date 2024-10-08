package net.arna.jcraft.client.renderer.entity;

import net.arna.jcraft.client.model.JProjectileModel;
import net.arna.jcraft.client.renderer.entity.projectiles.GeoProjectileRenderer;
import net.arna.jcraft.common.entity.projectile.GETreeEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * The {@link GeoProjectileRenderer} for {@link GETreeEntity}.
 */
public class GETreeRenderer extends GeoProjectileRenderer<GETreeEntity> {
    public GETreeRenderer(final EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new JProjectileModel<>("getree", true));
        shadowRadius = 2.5f;
    }
}
