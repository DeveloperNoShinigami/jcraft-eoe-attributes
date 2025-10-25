package net.arna.jcraft.client.model.entity;

import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.entity.GERScorpionEntity;
import net.minecraft.resources.ResourceLocation;

/**
 * The model for {@link GERScorpionEntity}.
 * @see net.arna.jcraft.client.renderer.entity.GERScorpionRenderer GERScorpionRenderer
 */
public final class GERScorpionModel { //extends GeoModel<GERScorpionEntity> {
    private static final ResourceLocation model = JCraft.id("geo/gerscorpion.geo.json");

    private static final ResourceLocation
            texture = JCraft.id("textures/entity/rock.png"),
            rock = JCraft.id("textures/entity/gerscorpion.png");

    private static final ResourceLocation animation = JCraft.id("animations/gerscorpion.animation.json");

    /*
    @Override
    public ResourceLocation getModelResource(final GERScorpionEntity animatable) {
        return model;
    }

    @Override
    public ResourceLocation getTextureResource(final GERScorpionEntity animatable) {
        return animatable.isRock() ? rock : texture;
    }

    @Override
    public ResourceLocation getAnimationResource(final GERScorpionEntity animatable) {
        return animation;
    }*/

}
