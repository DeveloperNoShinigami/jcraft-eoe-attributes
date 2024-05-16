package net.arna.jcraft.client.model.entity;

import mod.azure.azurelib.model.GeoModel;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.entity.projectile.GETreeEntity;
import net.minecraft.resources.ResourceLocation;


public class GETreeModel extends GeoModel<GETreeEntity> {
    @Override
    public ResourceLocation getModelResource(GETreeEntity object) {
        return JCraft.id("geo/getree.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(GETreeEntity object) {
        return JCraft.id("textures/entity/getree.png");
    }

    @Override
    public ResourceLocation getAnimationResource(GETreeEntity animatable) {
        return JCraft.id("animations/getree.animation.json");
    }

}
