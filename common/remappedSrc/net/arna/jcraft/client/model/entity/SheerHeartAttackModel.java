package net.arna.jcraft.client.model.entity;

import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.entity.SheerHeartAttackEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class SheerHeartAttackModel extends GeoModel<SheerHeartAttackEntity> {

    @Override
    public ResourceLocation getModelResource(SheerHeartAttackEntity object) {
        return JCraft.id("geo/sha.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(SheerHeartAttackEntity object) {
        return JCraft.id("textures/entity/sha.png");
    }

    @Override
    public ResourceLocation getAnimationResource(SheerHeartAttackEntity animatable) {
        return JCraft.id("animations/sha.animation.json");
    }
}