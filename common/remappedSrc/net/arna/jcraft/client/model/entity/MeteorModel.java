package net.arna.jcraft.client.model.entity;

import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.entity.projectile.MeteorProjectile;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class MeteorModel extends GeoModel<MeteorProjectile> {
    @Override
    public ResourceLocation getModelResource(MeteorProjectile object) {
        return JCraft.id("geo/meteor.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(MeteorProjectile object) {
        return JCraft.id("textures/entity/meteor/skin_" + object.getSkin() + ".png");
    }

    @Override
    public ResourceLocation getAnimationResource(MeteorProjectile animatable) {
        return JCraft.id("animations/meteor.animation.json");
    }
}
