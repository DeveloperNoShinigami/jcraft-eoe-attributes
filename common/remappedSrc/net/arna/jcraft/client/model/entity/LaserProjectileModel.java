package net.arna.jcraft.client.model.entity;

import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.entity.projectile.LaserProjectile;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class LaserProjectileModel extends GeoModel<LaserProjectile> {
    @Override
    public ResourceLocation getModelResource(LaserProjectile object) {
        return JCraft.id("geo/laser.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(LaserProjectile object) {
        return JCraft.id("textures/entity/projectiles/laser.png");
    }

    @Override
    public ResourceLocation getAnimationResource(LaserProjectile animatable) {
        return JCraft.id("animations/knife.animation.json");
    }

}
