package net.arna.jcraft.client.model.entity;

import mod.azure.azurelib.model.GeoModel;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.entity.projectile.SandTornadoEntity;
import net.minecraft.resources.ResourceLocation;


public class SandTornadoModel extends GeoModel<SandTornadoEntity> {
    @Override
    public ResourceLocation getModelResource(SandTornadoEntity object) {
        return JCraft.id("geo/sandtornado.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(SandTornadoEntity object) {
        return JCraft.id("textures/entity/sandtornado.png");
    }

    @Override
    public ResourceLocation getAnimationResource(SandTornadoEntity animatable) {
        return JCraft.id("animations/sandtornado.animation.json");
    }
}
