package net.arna.jcraft.client.model.entity;

import mod.azure.azurelib.model.GeoModel;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.entity.vehicle.RoadRollerEntity;
import net.minecraft.resources.ResourceLocation;

/**
 * The {@link GeoModel} for {@link RoadRollerEntity}.
 * @see net.arna.jcraft.client.renderer.entity.SheerHeartAttackRenderer SheerHeartAttackRenderer
 */
public final class RoadRollerModel extends GeoModel<RoadRollerEntity> {
    private static final ResourceLocation model = JCraft.id("geo/road_roller.geo.json");
    private static final ResourceLocation texture = JCraft.id("textures/entity/road_roller.png");
    private static final ResourceLocation animation = JCraft.id("animations/road_roller.animation.json");

    @Override
    public ResourceLocation getModelResource(final RoadRollerEntity animatable) {
        return model;
    }

    @Override
    public ResourceLocation getTextureResource(final RoadRollerEntity animatable) {
        return texture;
    }

    @Override
    public ResourceLocation getAnimationResource(final RoadRollerEntity animatable) {
        return animation;
    }
}