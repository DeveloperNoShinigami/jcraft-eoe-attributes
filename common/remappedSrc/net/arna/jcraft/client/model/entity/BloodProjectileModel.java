package net.arna.jcraft.client.model.entity;

import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.entity.projectile.BloodProjectile;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class BloodProjectileModel extends GeoModel<BloodProjectile> {
    @Override
    public ResourceLocation getModelResource(BloodProjectile object) {
        return JCraft.id("geo/bloodprojectile.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(BloodProjectile object) {
        return JCraft.id("textures/entity/projectiles/bloodprojectile.png");
    }

    @Override
    public ResourceLocation getAnimationResource(BloodProjectile animatable) {
        return JCraft.id("animations/knife.animation.json");
    }

}
