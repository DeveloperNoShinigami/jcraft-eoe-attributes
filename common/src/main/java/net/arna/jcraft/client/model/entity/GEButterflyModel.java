package net.arna.jcraft.client.model.entity;

import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.entity.GEButterflyEntity;
import net.minecraft.resources.ResourceLocation;

/**
 * The model for {@link GEButterflyEntity}.
 * @see net.arna.jcraft.client.renderer.entity.GEButterflyRenderer GEButterflyRenderer
 */
public final class GEButterflyModel {// extends GeoModel<GEButterflyEntity> {
    private static final ResourceLocation model = JCraft.id("geo/gebutterfly.geo.json");
    private static final ResourceLocation texture = JCraft.id("textures/entity/gebutterfly.png");
    private static final ResourceLocation animation = JCraft.id("animations/gebutterfly.animation.json");

    /*
    @Override
    public ResourceLocation getModelResource(final GEButterflyEntity animatable) {
        return model;
    }

    @Override
    public ResourceLocation getTextureResource(final GEButterflyEntity animatable) {
        return texture;
    }

    @Override
    public ResourceLocation getAnimationResource(final GEButterflyEntity animatable) {
        return animation;
    }*/
}
