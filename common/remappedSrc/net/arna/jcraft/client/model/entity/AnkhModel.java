package net.arna.jcraft.client.model.entity;

import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.entity.projectile.AnkhProjectile;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class AnkhModel extends GeoModel<AnkhProjectile> {
    @Override
    public ResourceLocation getModelResource(AnkhProjectile object) {

        return JCraft.id("geo/ankh.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(AnkhProjectile object) {
        return JCraft.id("textures/entity/projectiles/ankh.png");
    }

    @Override
    public ResourceLocation getAnimationResource(AnkhProjectile animatable) {
        return JCraft.id("animations/knife.animation.json");
    }

}
