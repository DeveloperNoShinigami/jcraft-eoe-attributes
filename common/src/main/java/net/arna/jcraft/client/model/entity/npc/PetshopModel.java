package net.arna.jcraft.client.model.entity.npc;

import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.entity.npc.PetshopEntity;
import net.minecraft.resources.ResourceLocation;

/**
 * The model for {@link PetshopEntity}.
 * @see net.arna.jcraft.client.renderer.entity.npc.PetshopRenderer PetshopRenderer
 */
public final class PetshopModel {
    private static final ResourceLocation model = JCraft.id("geo/aya_tsuji.geo.json");
    private static final ResourceLocation texture = JCraft.id("textures/entity/aya_tsuji.png");
    private static final ResourceLocation animation = JCraft.id("animations/aya_tsuji.animation.json");

    /*@Override
    public ResourceLocation getModelResource(final PetshopEntity animatable) {
        return model;
    }

    @Override
    public ResourceLocation getTextureResource(final PetshopEntity animatable) {
        return texture;
    }

    @Override
    public ResourceLocation getAnimationResource(final PetshopEntity animatable) {
        return animation;
    }*/
}
