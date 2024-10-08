package net.arna.jcraft.client.model.entity.npc;

import mod.azure.azurelib.model.GeoModel;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.entity.npc.DarbyYoungerEntity;
import net.minecraft.resources.ResourceLocation;

/**
 * The {@link GeoModel} for {@link DarbyYoungerModel}.
 * @see net.arna.jcraft.client.renderer.entity.npc.DarbyYoungerRenderer DarbyYoungerRenderer
 */
public class DarbyYoungerModel extends GeoModel<DarbyYoungerEntity> {
    @Override
    public ResourceLocation getModelResource(final DarbyYoungerEntity animatable) {
        return JCraft.id("geo/darby_younger.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(final DarbyYoungerEntity animatable) {
        return JCraft.id("textures/entity/darby_younger.png");
    }

    @Override
    public ResourceLocation getAnimationResource(final DarbyYoungerEntity animatable) {
        return JCraft.id("animations/darby_younger.animation.json");
    }
}
