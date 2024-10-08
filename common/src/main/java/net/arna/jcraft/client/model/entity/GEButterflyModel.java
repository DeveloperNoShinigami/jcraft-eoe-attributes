package net.arna.jcraft.client.model.entity;

import mod.azure.azurelib.model.GeoModel;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.entity.GEButterflyEntity;
import net.minecraft.resources.ResourceLocation;

/**
 * The {@link GeoModel} for {@link GEButterflyEntity}.
 * @see net.arna.jcraft.client.renderer.entity.GEButterflyRenderer GEButterflyRenderer
 */
public class GEButterflyModel extends GeoModel<GEButterflyEntity> {
    @Override
    public ResourceLocation getModelResource(final GEButterflyEntity object) {
        return JCraft.id("geo/gebutterfly.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(final GEButterflyEntity object) {
        return JCraft.id("textures/entity/gebutterfly.png");
    }

    @Override
    public ResourceLocation getAnimationResource(final GEButterflyEntity animatable) {
        return JCraft.id("animations/gebutterfly.animation.json");
    }
}
