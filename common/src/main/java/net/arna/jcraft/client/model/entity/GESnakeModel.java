package net.arna.jcraft.client.model.entity;

import mod.azure.azurelib.model.GeoModel;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.entity.GESnakeEntity;
import net.minecraft.resources.ResourceLocation;

/**
 * The {@link GeoModel} for {@link GESnakeEntity}.
 * @see net.arna.jcraft.client.renderer.entity.GESnakeRenderer GESnakeRenderer
 */
public final class GESnakeModel extends GeoModel<GESnakeEntity> {
    private static final ResourceLocation model = JCraft.id("geo/gesnake.geo.json");
    private static final ResourceLocation texture = JCraft.id("textures/entity/gesnake.png");
    private static final ResourceLocation animation = JCraft.id("animations/gesnake.animation.json");

    @Override
    public ResourceLocation getModelResource(final GESnakeEntity animatable) {
        return model;
    }

    @Override
    public ResourceLocation getTextureResource(final GESnakeEntity animatable) {
        return texture;
    }

    @Override
    public ResourceLocation getAnimationResource(final GESnakeEntity animatable) {
        return animation;
    }
}
