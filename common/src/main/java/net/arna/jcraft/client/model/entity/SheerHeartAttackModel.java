package net.arna.jcraft.client.model.entity;

import mod.azure.azurelib.model.GeoModel;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.entity.SheerHeartAttackEntity;
import net.minecraft.resources.ResourceLocation;

public class SheerHeartAttackModel extends GeoModel<SheerHeartAttackEntity> {

    @Override
    public ResourceLocation getModelResource(final SheerHeartAttackEntity object) {
        return JCraft.id("geo/sha.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(final SheerHeartAttackEntity object) {
        return JCraft.id("textures/entity/sha.png");
    }

    @Override
    public ResourceLocation getAnimationResource(final SheerHeartAttackEntity animatable) {
        return JCraft.id("animations/sha.animation.json");
    }
}