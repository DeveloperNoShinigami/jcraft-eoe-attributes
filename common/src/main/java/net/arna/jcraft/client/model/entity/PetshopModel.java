package net.arna.jcraft.client.model.entity;

import mod.azure.azurelib.model.GeoModel;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.entity.PetshopEntity;
import net.minecraft.resources.ResourceLocation;

public class PetshopModel extends GeoModel<PetshopEntity> {
    @Override
    public ResourceLocation getModelResource(final PetshopEntity animatable) {
        return JCraft.id("geo/petshop.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(final PetshopEntity animatable) {
        return JCraft.id("textures/entity/petshop.png");
    }

    @Override
    public ResourceLocation getAnimationResource(final PetshopEntity animatable) {
        return JCraft.id("animations/petshop.animation.json");
    }
}
