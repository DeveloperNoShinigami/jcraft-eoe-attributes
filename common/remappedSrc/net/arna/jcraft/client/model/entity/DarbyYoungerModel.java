package net.arna.jcraft.client.model.entity;

import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.entity.DarbyYoungerEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class DarbyYoungerModel extends GeoModel<DarbyYoungerEntity> {
    @Override
    public ResourceLocation getModelResource(DarbyYoungerEntity animatable) {
        return JCraft.id("geo/darby_younger.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(DarbyYoungerEntity animatable) {
        return JCraft.id("textures/entity/darby_younger.png");
    }

    @Override
    public ResourceLocation getAnimationResource(DarbyYoungerEntity animatable) {
        // TODO Arna
        return JCraft.id("darby_younger");
    }
}
