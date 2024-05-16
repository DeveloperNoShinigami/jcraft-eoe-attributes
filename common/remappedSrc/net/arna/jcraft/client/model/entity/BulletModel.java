package net.arna.jcraft.client.model.entity;

import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.entity.projectile.BulletProjectile;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class BulletModel extends GeoModel<BulletProjectile> {
    @Override
    public ResourceLocation getModelResource(BulletProjectile object) {
        return JCraft.id("geo/bullet.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(BulletProjectile object) {
        return JCraft.id("textures/entity/projectiles/bullet.png");
    }

    @Override
    public ResourceLocation getAnimationResource(BulletProjectile animatable) {
        return JCraft.id("animations/knife.animation.json");
    }

}
