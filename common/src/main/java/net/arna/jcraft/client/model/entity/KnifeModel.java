package net.arna.jcraft.client.model.entity;

import mod.azure.azurelib.model.GeoModel;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.entity.projectile.KnifeProjectile;
import net.minecraft.resources.ResourceLocation;

/**
 * The {@link GeoModel} for {@link KnifeProjectile}.
 * @see net.arna.jcraft.client.renderer.entity.projectiles.KnifeRenderer KnifeRenderer
 */
public class KnifeModel extends GeoModel<KnifeProjectile> {
    @Override
    public ResourceLocation getModelResource(KnifeProjectile object) {
        return JCraft.id("geo/knife.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(KnifeProjectile object) {
        return (object.getLightning()) ? JCraft.id("textures/entity/projectiles/lknife.png") : JCraft.id("textures/entity/projectiles/knife.png");
    }

    @Override
    public ResourceLocation getAnimationResource(KnifeProjectile animatable) {
        return JCraft.id("animations/knife.animation.json");
    }

}
