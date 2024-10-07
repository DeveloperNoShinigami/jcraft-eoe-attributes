package net.arna.jcraft.client.model.entity;

import mod.azure.azurelib.model.GeoModel;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.entity.GESnakeEntity;
import net.minecraft.resources.ResourceLocation;

public class GESnakeModel extends GeoModel<GESnakeEntity> {
    @Override
    public ResourceLocation getModelResource(final GESnakeEntity object) {
        return JCraft.id("geo/gesnake.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(final GESnakeEntity object) {
        return JCraft.id("textures/entity/gesnake.png");
    }

    @Override
    public ResourceLocation getAnimationResource(final GESnakeEntity animatable) {
        return JCraft.id("animations/gesnake.animation.json");
    }
}
