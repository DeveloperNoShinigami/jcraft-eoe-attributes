package net.arna.jcraft.client.model.entity;

import mod.azure.azurelib.model.GeoModel;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.entity.GERScorpionEntity;
import net.minecraft.resources.ResourceLocation;

public class GERScorpionModel extends GeoModel<GERScorpionEntity> {
    @Override
    public ResourceLocation getModelResource(final GERScorpionEntity object) {
        return JCraft.id("geo/gerscorpion.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(final GERScorpionEntity object) {
        return object.isRock() ? JCraft.id("textures/entity/rock.png") : JCraft.id("textures/entity/gerscorpion.png");
    }

    @Override
    public ResourceLocation getAnimationResource(final GERScorpionEntity animatable) {
        return JCraft.id("animations/gerscorpion.animation.json");
    }

}
