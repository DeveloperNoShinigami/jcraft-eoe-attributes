package net.arna.jcraft.client.model.entity.npc;

import mod.azure.azurelib.model.GeoModel;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.entity.npc.DarbyYoungerEntity;
import net.minecraft.resources.ResourceLocation;

/**
 * The {@link GeoModel} for {@link DarbyYoungerModel}.
 * @see net.arna.jcraft.client.renderer.entity.npc.DarbyYoungerRenderer DarbyYoungerRenderer
 */
public final class DarbyYoungerModel extends GeoModel<DarbyYoungerEntity> {
    private static final ResourceLocation model = JCraft.id("geo/darby_younger.geo.json");
    private static final ResourceLocation texture = JCraft.id("textures/entity/darby_younger.png");
    private static final ResourceLocation animation = JCraft.id("animations/darby_younger.animation.json");

    @Override
    public ResourceLocation getModelResource(final DarbyYoungerEntity animatable) {
        return model;
    }

    @Override
    public ResourceLocation getTextureResource(final DarbyYoungerEntity animatable) {
        return texture;
    }

    @Override
    public ResourceLocation getAnimationResource(final DarbyYoungerEntity animatable) {
        return animation;
    }
}
