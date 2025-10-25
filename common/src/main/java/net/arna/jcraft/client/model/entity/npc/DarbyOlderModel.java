package net.arna.jcraft.client.model.entity.npc;

import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.entity.npc.DarbyOlderEntity;
import net.minecraft.resources.ResourceLocation;

/**
 * The model for {@link DarbyOlderEntity}.
 * @see net.arna.jcraft.client.renderer.entity.npc.DarbyOlderRenderer DarbyOlderRenderer
 */
public final class DarbyOlderModel {
    private static final ResourceLocation model = JCraft.id("geo/darby_older.geo.json");
    private static final ResourceLocation texture = JCraft.id("textures/entity/darby_older.png");
    private static final ResourceLocation animation = JCraft.id("animations/darby_older.animation.json");
    /*
    @Override
    public ResourceLocation getModelResource(final DarbyOlderEntity animatable) {
        return model;
    }

    @Override
    public ResourceLocation getTextureResource(final DarbyOlderEntity animatable) {
        return texture;
    }

    @Override
    public ResourceLocation getAnimationResource(final DarbyOlderEntity animatable) {
        return animation;
    }*/
}
