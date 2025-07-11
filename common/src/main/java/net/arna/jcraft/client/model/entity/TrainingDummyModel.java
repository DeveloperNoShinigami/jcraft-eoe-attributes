package net.arna.jcraft.client.model.entity;

import mod.azure.azurelib.model.GeoModel;
import net.arna.jcraft.common.entity.TrainingDummyEntity;
import net.minecraft.resources.ResourceLocation;

/**
 * Model for the TrainingDummyEntity
 */
public class TrainingDummyModel extends GeoModel<TrainingDummyEntity> {

    @Override
    public ResourceLocation getModelResource(TrainingDummyEntity animatable) {
        return new ResourceLocation("jcraft", "geo/training_dummy.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(TrainingDummyEntity animatable) {
        return new ResourceLocation("jcraft", "textures/entity/training_dummy.png");
    }

    @Override
    public ResourceLocation getAnimationResource(TrainingDummyEntity animatable) {
        return new ResourceLocation("jcraft", "animations/training_dummy.animation.json");
    }
}