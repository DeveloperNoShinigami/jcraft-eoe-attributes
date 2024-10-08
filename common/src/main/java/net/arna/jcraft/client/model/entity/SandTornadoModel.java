package net.arna.jcraft.client.model.entity;

import mod.azure.azurelib.model.GeoModel;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.entity.projectile.SandTornadoEntity;
import net.minecraft.resources.ResourceLocation;

/**
 * The {@link GeoModel} for {@link SandTornadoEntity}.
 * @see net.arna.jcraft.client.renderer.entity.SandTornadoRenderer SandTornadoRenderer
 */
public class SandTornadoModel extends GeoModel<SandTornadoEntity> {
    @Override
    public ResourceLocation getModelResource(final SandTornadoEntity object) {
        return JCraft.id("geo/sandtornado.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(final SandTornadoEntity object) {
        return JCraft.id("textures/entity/sandtornado.png");
    }

    @Override
    public ResourceLocation getAnimationResource(final SandTornadoEntity animatable) {
        return JCraft.id("animations/sandtornado.animation.json");
    }
}
