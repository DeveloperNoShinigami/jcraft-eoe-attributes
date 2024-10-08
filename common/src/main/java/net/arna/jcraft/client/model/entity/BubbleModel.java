package net.arna.jcraft.client.model.entity;

import mod.azure.azurelib.model.GeoModel;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.entity.projectile.BubbleProjectile;
import net.minecraft.resources.ResourceLocation;

/**
 * The {@link GeoModel} for {@link BubbleProjectile}.
 * @see net.arna.jcraft.client.renderer.entity.projectiles.BubbleRenderer BubbleRenderer
 */
public class BubbleModel extends GeoModel<BubbleProjectile> {
    @Override
    public ResourceLocation getModelResource(final BubbleProjectile object) {
        return JCraft.id("geo/bubble.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(final BubbleProjectile object) {
        return JCraft.id("textures/entity/projectiles/bubble.png");
    }

    @Override
    public ResourceLocation getAnimationResource(final BubbleProjectile animatable) {
        return JCraft.id("animations/bubble.animation.json");
    }

}
