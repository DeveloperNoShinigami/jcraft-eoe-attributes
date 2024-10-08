package net.arna.jcraft.client.model.entity;

import mod.azure.azurelib.model.GeoModel;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.entity.projectile.GETreeEntity;
import net.minecraft.resources.ResourceLocation;

/**
 * The {@link GeoModel} for {@link GETreeModel}.
 * @see net.arna.jcraft.client.renderer.entity.GETreeRenderer GETreeRenderer
 */
public class GETreeModel extends GeoModel<GETreeEntity> {
    @Override
    public ResourceLocation getModelResource(final GETreeEntity object) {
        return JCraft.id("geo/getree.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(final GETreeEntity object) {
        return JCraft.id("textures/entity/getree.png");
    }

    @Override
    public ResourceLocation getAnimationResource(final GETreeEntity animatable) {
        return JCraft.id("animations/getree.animation.json");
    }

}
