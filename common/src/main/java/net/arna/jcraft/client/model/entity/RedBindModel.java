package net.arna.jcraft.client.model.entity;

import mod.azure.azurelib.model.GeoModel;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.entity.projectile.RedBindEntity;
import net.minecraft.resources.ResourceLocation;

/**
 * The {@link GeoModel} for {@link RedBindEntity}.
 * @see net.arna.jcraft.client.renderer.entity.projectiles.RedBindRenderer RedBindRenderer
 */
public class RedBindModel extends GeoModel<RedBindEntity> {
    @Override
    public ResourceLocation getModelResource(final RedBindEntity object) {
        return JCraft.id("geo/red_bind.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(final RedBindEntity object) {
        return JCraft.id("textures/entity/red_bind.png");
    }

    @Override
    public ResourceLocation getAnimationResource(final RedBindEntity animatable) {
        return JCraft.id("animations/red_bind.animation.json");
    }

}
