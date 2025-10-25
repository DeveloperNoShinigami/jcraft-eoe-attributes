package net.arna.jcraft.client.model.entity;

import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.entity.projectile.RedBindEntity;
import net.minecraft.resources.ResourceLocation;

/**
 * The model for {@link RedBindEntity}.
 * @see net.arna.jcraft.client.renderer.entity.projectiles.RedBindRenderer RedBindRenderer
 */
public final class RedBindModel {
    private static final ResourceLocation model = JCraft.id("geo/red_bind.geo.json");
    private static final ResourceLocation texture = JCraft.id("textures/entity/red_bind.png");
    private static final ResourceLocation animation = JCraft.id("animations/red_bind.animation.json");

    /*@Override
    public ResourceLocation getModelResource(final RedBindEntity animatable) {
        return model;
    }

    @Override
    public ResourceLocation getTextureResource(final RedBindEntity animatable) {
        return texture;
    }

    @Override
    public ResourceLocation getAnimationResource(final RedBindEntity animatable) {
        return animation;
    }*/

}
