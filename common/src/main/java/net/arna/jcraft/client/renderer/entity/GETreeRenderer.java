package net.arna.jcraft.client.renderer.entity;

import lombok.NonNull;
import mod.azure.azurelib.render.entity.AzEntityRendererConfig;
import net.arna.jcraft.client.renderer.entity.projectiles.ProjectileRenderer;
import net.arna.jcraft.common.entity.projectile.GETreeEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * The {@link ProjectileRenderer} for {@link GETreeEntity}.
 */
public class GETreeRenderer extends ProjectileRenderer<GETreeEntity> {

    public static final String ID = "getree";

    public GETreeRenderer(final @NonNull EntityRendererProvider.Context context) {
        super(context, () -> new EntityAnimator<>(ID), b -> b.setShadowRadius(2.5f), ID);
    }
}
