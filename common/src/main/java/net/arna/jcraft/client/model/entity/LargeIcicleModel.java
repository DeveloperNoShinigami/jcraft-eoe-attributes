package net.arna.jcraft.client.model.entity;

import mod.azure.azurelib.model.GeoModel;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.entity.projectile.LargeIcicleProjectile;
import net.minecraft.resources.ResourceLocation;

public class LargeIcicleModel extends GeoModel<LargeIcicleProjectile> {
    @Override
    public ResourceLocation getModelResource(LargeIcicleProjectile object) {
        return JCraft.id("geo/large_icicle.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(LargeIcicleProjectile object) {
        return JCraft.id("textures/entity/projectiles/large_icicle.png");
    }

    @Override
    public ResourceLocation getAnimationResource(LargeIcicleProjectile animatable) {
        return JCraft.id("animations/large_icicle.animation.json");
    }
}
