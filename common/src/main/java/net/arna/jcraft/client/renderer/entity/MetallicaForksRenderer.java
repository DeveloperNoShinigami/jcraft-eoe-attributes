package net.arna.jcraft.client.renderer.entity;

import lombok.NonNull;
import mod.azure.azurelib.renderer.GeoEntityRenderer;
import net.arna.jcraft.client.model.entity.MetallicaForksModel;
import net.arna.jcraft.common.entity.projectile.MetallicaForksEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * The {@link GeoEntityRenderer} for {@link MetallicaForksEntity}.
 * @see MetallicaForksModel
 */
public class MetallicaForksRenderer extends GeoEntityRenderer<MetallicaForksEntity> {
    public MetallicaForksRenderer(final EntityRendererProvider.Context renderManager) {
        super(renderManager, new MetallicaForksModel());
    }

    @Override
    public boolean shouldShowName(final @NonNull MetallicaForksEntity animatable) {
        return false;
    }
}
