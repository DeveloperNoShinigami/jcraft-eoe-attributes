package net.arna.jcraft.client.model.entity;

import mod.azure.azurelib.model.GeoModel;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.entity.projectile.ScalpelProjectile;
import net.minecraft.resources.ResourceLocation;

public class ScalpelModel extends GeoModel<ScalpelProjectile> {
    @Override
    public ResourceLocation getModelResource(final ScalpelProjectile object) {
        return JCraft.id("geo/scalpel.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(final ScalpelProjectile object) {
        return JCraft.id("textures/entity/projectiles/scalpel.png");
    }

    @Override
    public ResourceLocation getAnimationResource(final ScalpelProjectile animatable) {
        return JCraft.id("animations/knife.animation.json");
    }

}
