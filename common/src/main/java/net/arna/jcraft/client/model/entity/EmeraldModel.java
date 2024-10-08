package net.arna.jcraft.client.model.entity;

import mod.azure.azurelib.model.GeoModel;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.entity.projectile.EmeraldProjectile;
import net.minecraft.resources.ResourceLocation;

/**
 * The {@link GeoModel} for {@link EmeraldProjectile}.
 * @see net.arna.jcraft.client.renderer.entity.projectiles.EmeraldRenderer EmeraldRenderer
 */
public class EmeraldModel extends GeoModel<EmeraldProjectile> {
    @Override
    public ResourceLocation getModelResource(final EmeraldProjectile object) {
        return JCraft.id("geo/emerald.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(final EmeraldProjectile object) {
        return JCraft.id("textures/entity/projectiles/emerald.png");
    }

    @Override
    public ResourceLocation getAnimationResource(final EmeraldProjectile animatable) {
        return JCraft.id("animations/knife.animation.json");
    }

}
