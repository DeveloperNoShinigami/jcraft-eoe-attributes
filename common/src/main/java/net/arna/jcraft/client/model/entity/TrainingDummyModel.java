package net.arna.jcraft.client.model.entity;

import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.entity.TrainingDummyEntity;
import net.minecraft.resources.ResourceLocation;

/**
 * Model for the TrainingDummyEntity
 */
public final class TrainingDummyModel {
    private static final ResourceLocation model = JCraft.id("geo/training_dummy.geo.json");
    private static final ResourceLocation texture = JCraft.id("textures/entity/training_dummy.png");
    private static final ResourceLocation animation = JCraft.id("animations/training_dummy.animation.json");

    /*@Override
    public ResourceLocation getModelResource(final TrainingDummyEntity animatable) {
        return model;
    }

    @Override
    public ResourceLocation getTextureResource(final TrainingDummyEntity animatable) {
        return texture;
    }

    @Override
    public ResourceLocation getAnimationResource(final TrainingDummyEntity animatable) {
        return animation;
    }*/
}