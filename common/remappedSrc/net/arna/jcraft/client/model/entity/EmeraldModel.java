package net.arna.jcraft.client.model.entity;

import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.entity.projectile.EmeraldProjectile;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class EmeraldModel extends GeoModel<EmeraldProjectile> {
    @Override
    public ResourceLocation getModelResource(EmeraldProjectile object) {
        return JCraft.id("geo/emerald.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(EmeraldProjectile object) {
        return JCraft.id("textures/entity/projectiles/emerald.png");
    }

    @Override
    public ResourceLocation getAnimationResource(EmeraldProjectile animatable) {
        return JCraft.id("animations/knife.animation.json");
    }

}
