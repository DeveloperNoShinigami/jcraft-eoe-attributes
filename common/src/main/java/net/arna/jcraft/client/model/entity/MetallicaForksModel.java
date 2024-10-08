package net.arna.jcraft.client.model.entity;

import mod.azure.azurelib.model.GeoModel;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.entity.projectile.MetallicaForksEntity;
import net.minecraft.resources.ResourceLocation;

/**
 * The {@link GeoModel} for {@link MetallicaForksEntity}.
 * @see net.arna.jcraft.client.renderer.entity.MetallicaForksRenderer MetallicaForksRenderer
 */
public class MetallicaForksModel extends GeoModel<MetallicaForksEntity> {
    @Override
    public ResourceLocation getModelResource(final MetallicaForksEntity animatable) {
        return JCraft.id("geo/forks.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(final MetallicaForksEntity animatable) {
        return JCraft.id("textures/entity/forks.png");
    }

    @Override
    public ResourceLocation getAnimationResource(final MetallicaForksEntity animatable) {
        return JCraft.id("animations/forks.animation.json");
    }
}
