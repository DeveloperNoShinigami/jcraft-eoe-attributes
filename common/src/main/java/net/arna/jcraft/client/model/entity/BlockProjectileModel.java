package net.arna.jcraft.client.model.entity;

import mod.azure.azurelib.model.GeoModel;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.entity.projectile.BlockProjectile;
import net.minecraft.resources.ResourceLocation;

/**
 * The {@link GeoModel} for {@link BlockProjectile}.
 * @see net.arna.jcraft.client.renderer.entity.projectiles.BlockProjectileRenderer BlockProjectileRenderer
 */
public class BlockProjectileModel extends GeoModel<BlockProjectile> {
    @Override
    public ResourceLocation getModelResource(final BlockProjectile object) {
        return JCraft.id("geo/block.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(final BlockProjectile object) {
        return JCraft.id("textures/entity/projectiles/block.png");
    }

    @Override
    public ResourceLocation getAnimationResource(final BlockProjectile animatable) {
        return JCraft.id("animations/block.animation.json");
    }

}
