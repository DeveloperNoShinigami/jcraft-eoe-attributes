package net.arna.jcraft.client.model.entity;

import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.entity.projectile.SunBeamProjectile;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class SunBeamModel extends GeoModel<SunBeamProjectile> {
    @Override
    public ResourceLocation getModelResource(SunBeamProjectile object) {
        return JCraft.id("geo/sunbeam.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(SunBeamProjectile object) {
        return JCraft.id("textures/entity/sunbeam/skin_" + object.getSkin() + ".png");
    }

    @Override
    public ResourceLocation getAnimationResource(SunBeamProjectile animatable) {
        return JCraft.id("animations/sunbeam.animation.json");
    }

}
