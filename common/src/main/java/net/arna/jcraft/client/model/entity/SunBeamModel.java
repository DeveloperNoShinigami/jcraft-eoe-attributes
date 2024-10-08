package net.arna.jcraft.client.model.entity;

import mod.azure.azurelib.model.GeoModel;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.entity.projectile.SunBeamProjectile;
import net.minecraft.resources.ResourceLocation;

/**
 * The {@link GeoModel} for {@link SunBeamProjectile}.
 * @see net.arna.jcraft.client.renderer.entity.projectiles.SunBeamRenderer
 */
public class SunBeamModel extends GeoModel<SunBeamProjectile> {
    @Override
    public ResourceLocation getModelResource(final SunBeamProjectile object) {
        return JCraft.id("geo/sunbeam.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(final SunBeamProjectile object) {
        return JCraft.id("textures/entity/sunbeam/skin_" + object.getSkin() + ".png");
    }

    @Override
    public ResourceLocation getAnimationResource(final SunBeamProjectile animatable) {
        return JCraft.id("animations/sunbeam.animation.json");
    }

}
