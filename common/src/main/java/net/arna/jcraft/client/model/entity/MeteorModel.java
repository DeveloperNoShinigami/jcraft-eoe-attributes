package net.arna.jcraft.client.model.entity;

import mod.azure.azurelib.model.GeoModel;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.entity.projectile.MeteorProjectile;
import net.minecraft.resources.ResourceLocation;

/**
 * The {@link GeoModel} for {@link MeteorProjectile}.
 * @see net.arna.jcraft.client.renderer.entity.projectiles.MeteorRenderer MeteorRenderer
 */
public class MeteorModel extends GeoModel<MeteorProjectile> {
    @Override
    public ResourceLocation getModelResource(final MeteorProjectile object) {
        return JCraft.id("geo/meteor.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(final MeteorProjectile object) {
        return JCraft.id("textures/entity/meteor/skin_" + object.getSkin() + ".png");
    }

    @Override
    public ResourceLocation getAnimationResource(final MeteorProjectile animatable) {
        return JCraft.id("animations/meteor.animation.json");
    }
}
