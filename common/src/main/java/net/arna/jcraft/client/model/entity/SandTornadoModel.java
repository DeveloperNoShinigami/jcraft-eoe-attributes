package net.arna.jcraft.client.model.entity;

import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.entity.projectile.SandTornadoEntity;
import net.minecraft.resources.ResourceLocation;

/**
 * The model for {@link SandTornadoEntity}.
 * @see net.arna.jcraft.client.renderer.entity.SandTornadoRenderer SandTornadoRenderer
 */
public final class SandTornadoModel {
    private static final ResourceLocation model = JCraft.id("geo/sandtornado.geo.json");
    private static final ResourceLocation texture = JCraft.id("textures/entity/sandtornado.png");
    private static final ResourceLocation animation = JCraft.id("animations/sandtornado.animation.json");

    /*@Override
    public ResourceLocation getModelResource(final SandTornadoEntity animatable) {
        return model;
    }

    @Override
    public ResourceLocation getTextureResource(final SandTornadoEntity animatable) {
        return texture;
    }

    @Override
    public ResourceLocation getAnimationResource(final SandTornadoEntity animatable) {
        return animation;
    }*/
}
