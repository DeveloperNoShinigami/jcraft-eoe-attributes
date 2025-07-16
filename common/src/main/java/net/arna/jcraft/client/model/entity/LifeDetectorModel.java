package net.arna.jcraft.client.model.entity;

import mod.azure.azurelib.model.GeoModel;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.entity.projectile.LifeDetectorEntity;
import net.minecraft.resources.ResourceLocation;

/**
 * The {@link GeoModel} for {@link LifeDetectorEntity}.
 * @see net.arna.jcraft.client.renderer.entity.projectiles.LifeDetectorRenderer LifeDetectorRenderer
 */
public final class LifeDetectorModel extends GeoModel<LifeDetectorEntity> {
    private static final ResourceLocation model = JCraft.id("geo/detector.geo.json");
    private static final ResourceLocation texture = JCraft.id("textures/entity/projectiles/detector.png");
    private static final ResourceLocation animation = JCraft.id("animations/detector.animation.json");

    @Override
    public ResourceLocation getModelResource(final LifeDetectorEntity animatable) {
        return model;
    }

    @Override
    public ResourceLocation getTextureResource(final LifeDetectorEntity animatable) {
        return texture;
    }

    @Override
    public ResourceLocation getAnimationResource(final LifeDetectorEntity animatable) {
        return animation;
    }
}
