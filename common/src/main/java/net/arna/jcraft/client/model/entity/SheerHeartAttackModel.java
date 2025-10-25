package net.arna.jcraft.client.model.entity;

import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.entity.SheerHeartAttackEntity;
import net.minecraft.resources.ResourceLocation;

/**
 * The model for {@link SheerHeartAttackEntity}.
 * @see net.arna.jcraft.client.renderer.entity.SheerHeartAttackRenderer SheerHeartAttackRenderer
 */
public final class SheerHeartAttackModel {
    private static final ResourceLocation model = JCraft.id("geo/sha.geo.json");
    private static final ResourceLocation texture = JCraft.id("textures/entity/sha.png");
    private static final ResourceLocation animation = JCraft.id("animations/sha.animation.json");

    /*@Override
    public ResourceLocation getModelResource(final SheerHeartAttackEntity animatable) {
        return model;
    }

    @Override
    public ResourceLocation getTextureResource(final SheerHeartAttackEntity animatable) {
        return texture;
    }

    @Override
    public ResourceLocation getAnimationResource(final SheerHeartAttackEntity animatable) {
        return animation;
    }*/
}