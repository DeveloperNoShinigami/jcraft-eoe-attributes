package net.arna.jcraft.client.model.entity;

import mod.azure.azurelib.model.GeoModel;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.entity.projectile.BulletProjectile;
import net.minecraft.resources.ResourceLocation;

/**
 * The {@link GeoModel} for {@link BulletProjectile}.
 * @see net.arna.jcraft.client.renderer.entity.projectiles.BulletRenderer BulletRenderer
 */
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
