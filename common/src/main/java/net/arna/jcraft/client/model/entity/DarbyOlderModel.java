package net.arna.jcraft.client.model.entity;

import mod.azure.azurelib.model.GeoModel;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.entity.DarbyOlderEntity;
import net.minecraft.resources.ResourceLocation;

public class DarbyOlderModel extends GeoModel<DarbyOlderEntity> {
    @Override
    public ResourceLocation getModelResource(final DarbyOlderEntity animatable) {
        return JCraft.id("geo/darby_older.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(final DarbyOlderEntity animatable) {
        return JCraft.id("textures/entity/darby_older.png");
    }

    @Override
    public ResourceLocation getAnimationResource(final DarbyOlderEntity animatable) {
        return JCraft.id("animations/darby_older.animation.json");
    }
}
