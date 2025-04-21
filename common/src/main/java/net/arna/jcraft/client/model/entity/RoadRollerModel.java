package net.arna.jcraft.client.model.entity;

import mod.azure.azurelib.model.GeoModel;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.entity.vehicle.RoadRollerEntity;
import net.minecraft.resources.ResourceLocation;

/**
 * The {@link GeoModel} for {@link RoadRollerEntity}.
 * @see net.arna.jcraft.client.renderer.entity.SheerHeartAttackRenderer SheerHeartAttackRenderer
 */
public class RoadRollerModel extends GeoModel<RoadRollerEntity> {

    @Override
    public ResourceLocation getModelResource(final RoadRollerEntity object) {
        return JCraft.id("geo/road_roller.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(final RoadRollerEntity object) {
        return JCraft.id("textures/entity/road_roller.png");
    }

    @Override
    public ResourceLocation getAnimationResource(final RoadRollerEntity animatable) {
        return JCraft.id("animations/road_roller.animation.json");
    }
}