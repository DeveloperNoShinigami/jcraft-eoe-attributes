package net.arna.jcraft.client.model.entity;

import mod.azure.azurelib.model.GeoModel;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.entity.projectile.LaserProjectile;
import net.minecraft.resources.ResourceLocation;

/**
 * The {@link GeoModel} for {@link LaserProjectile}.
 * @see net.arna.jcraft.client.renderer.entity.projectiles.LaserProjectileRenderer LaserProjectileRenderer
 */
public class LaserProjectileModel extends GeoModel<LaserProjectile> {
    @Override
    public ResourceLocation getModelResource(final LaserProjectile object) {
        return JCraft.id("geo/laser.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(final LaserProjectile object) {
        return JCraft.id("textures/entity/projectiles/laser.png");
    }

    @Override
    public ResourceLocation getAnimationResource(final LaserProjectile animatable) {
        return JCraft.id("animations/knife.animation.json");
    }

}
