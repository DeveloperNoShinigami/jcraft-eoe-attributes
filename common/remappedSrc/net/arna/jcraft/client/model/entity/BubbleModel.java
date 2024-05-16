package net.arna.jcraft.client.model.entity;

import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.entity.projectile.BubbleProjectile;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class BubbleModel extends GeoModel<BubbleProjectile> {
    @Override
    public ResourceLocation getModelResource(BubbleProjectile object) {
        return JCraft.id("geo/bubble.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(BubbleProjectile object) {
        return JCraft.id("textures/entity/projectiles/bubble.png");
    }

    @Override
    public ResourceLocation getAnimationResource(BubbleProjectile animatable) {
        return JCraft.id("animations/bubble.animation.json");
    }

}
