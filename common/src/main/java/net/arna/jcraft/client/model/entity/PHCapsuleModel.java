package net.arna.jcraft.client.model.entity;

import mod.azure.azurelib.model.GeoModel;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.entity.projectile.PHCapsuleProjectile;
import net.minecraft.resources.ResourceLocation;

/**
 * The {@link GeoModel} for {@link PHCapsuleProjectile}.
 * @see net.arna.jcraft.client.renderer.entity.projectiles.PHCapsuleRenderer PHCapsuleRenderer
 */
public class PHCapsuleModel extends GeoModel<PHCapsuleProjectile> {
    @Override
    public ResourceLocation getModelResource(final PHCapsuleProjectile object) {
        return JCraft.id("geo/ph_capsule.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(final PHCapsuleProjectile object) {
        return JCraft.id("textures/entity/projectiles/ph_capsule.png");
    }

    @Override
    public ResourceLocation getAnimationResource(final PHCapsuleProjectile animatable) {
        return JCraft.id("animations/knife.animation.json");
    }
}
