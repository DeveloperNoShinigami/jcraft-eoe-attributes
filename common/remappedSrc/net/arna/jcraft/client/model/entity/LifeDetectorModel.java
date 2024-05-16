package net.arna.jcraft.client.model.entity;

import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.entity.projectile.LifeDetectorEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class LifeDetectorModel extends GeoModel<LifeDetectorEntity> {
    @Override
    public ResourceLocation getModelResource(LifeDetectorEntity object) {
        return JCraft.id("geo/detector.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(LifeDetectorEntity object) {
        return JCraft.id("textures/entity/projectiles/detector.png");
    }

    @Override
    public ResourceLocation getAnimationResource(LifeDetectorEntity animatable) {
        return JCraft.id("animations/detector.animation.json");
    }

}
