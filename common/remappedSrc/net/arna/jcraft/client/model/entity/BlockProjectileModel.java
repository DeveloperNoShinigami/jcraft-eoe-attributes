package net.arna.jcraft.client.model.entity;

import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.entity.projectile.BlockProjectile;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class BlockProjectileModel extends GeoModel<BlockProjectile> {
    @Override
    public ResourceLocation getModelResource(BlockProjectile object) {
        return JCraft.id("geo/block.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(BlockProjectile object) {
        return JCraft.id("textures/entity/projectiles/block.png");
    }

    @Override
    public ResourceLocation getAnimationResource(BlockProjectile animatable) {
        return JCraft.id("animations/block.animation.json");
    }

}
