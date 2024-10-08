package net.arna.jcraft.client.model.entity;

import mod.azure.azurelib.model.GeoModel;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.entity.projectile.IcicleProjectile;
import net.minecraft.resources.ResourceLocation;

/**
 * The {@link GeoModel} for {@link IcicleProjectile}.
 * @see net.arna.jcraft.client.renderer.entity.projectiles.IcicleRenderer IcicleRenderer
 */
public class IcicleModel extends GeoModel<IcicleProjectile> {
    @Override
    public ResourceLocation getModelResource(final IcicleProjectile object) {
        return JCraft.id("geo/icicle.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(final IcicleProjectile object) {
        return JCraft.id("textures/entity/projectiles/icicle.png");
    }

    @Override
    public ResourceLocation getAnimationResource(final IcicleProjectile animatable) {
        return JCraft.id("animations/knife.animation.json");
    }
}
