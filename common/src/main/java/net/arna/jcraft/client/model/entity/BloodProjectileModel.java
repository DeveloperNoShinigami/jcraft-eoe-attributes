package net.arna.jcraft.client.model.entity;

import mod.azure.azurelib.model.GeoModel;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.entity.projectile.BloodProjectile;
import net.minecraft.resources.ResourceLocation;

/**
 * The {@link GeoModel} for {@link BloodProjectile}.
 * @see net.arna.jcraft.client.renderer.entity.projectiles.BloodProjectileRenderer BloodProjectileRenderer
 */
public class BloodProjectileModel extends GeoModel<BloodProjectile> {
    @Override
    public ResourceLocation getModelResource(final BloodProjectile object) {
        return JCraft.id("geo/bloodprojectile.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(final BloodProjectile object) {
        return JCraft.id("textures/entity/projectiles/bloodprojectile.png");
    }

    @Override
    public ResourceLocation getAnimationResource(final BloodProjectile animatable) {
        return JCraft.id("animations/knife.animation.json");
    }

}
