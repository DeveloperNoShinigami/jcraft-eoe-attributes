package net.arna.jcraft.client.model.entity;

import mod.azure.azurelib.model.GeoModel;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.entity.DarbyYoungerEntity;
import net.minecraft.resources.ResourceLocation;


public class DarbyYoungerModel extends GeoModel<DarbyYoungerEntity> {
    @Override
    public ResourceLocation getModelResource(DarbyYoungerEntity animatable) {
        return JCraft.id("geo/darby_younger.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(DarbyYoungerEntity animatable) {
        return JCraft.id("textures/entity/darby_younger.png");
    }

    @Override
    public ResourceLocation getAnimationResource(DarbyYoungerEntity animatable) {
        return JCraft.id("animations/darby_younger.animation.json");
    }
}
