package net.arna.jcraft.client.model.entity;

import mod.azure.azurelib.model.GeoModel;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.entity.projectile.BulletProjectile;
import net.minecraft.resources.ResourceLocation;

public class BulletModel extends GeoModel<BulletProjectile> {
    @Override
    public ResourceLocation getModelResource(final BulletProjectile object) {
        return JCraft.id("geo/bullet.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(final BulletProjectile object) {
        return JCraft.id("textures/entity/projectiles/bullet.png");
    }

    @Override
    public ResourceLocation getAnimationResource(final BulletProjectile animatable) {
        return JCraft.id("animations/knife.animation.json");
    }

}
