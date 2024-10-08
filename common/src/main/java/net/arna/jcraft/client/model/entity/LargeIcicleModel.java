package net.arna.jcraft.client.model.entity;

import mod.azure.azurelib.model.GeoModel;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.entity.projectile.LargeIcicleProjectile;
import net.minecraft.resources.ResourceLocation;

/**
 * The {@link GeoModel} for {@link LargeIcicleProjectile}.
 * @see net.arna.jcraft.client.renderer.entity.projectiles.LargeIcicleRenderer LargeIcicleRenderer
 */
public class LargeIcicleModel extends GeoModel<LargeIcicleProjectile> {
    @Override
    public ResourceLocation getModelResource(final LargeIcicleProjectile object) {
        return JCraft.id("geo/large_icicle.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(final LargeIcicleProjectile object) {
        return JCraft.id("textures/entity/projectiles/large_icicle.png");
    }

    @Override
    public ResourceLocation getAnimationResource(final LargeIcicleProjectile animatable) {
        return JCraft.id("animations/large_icicle.animation.json");
    }
}
