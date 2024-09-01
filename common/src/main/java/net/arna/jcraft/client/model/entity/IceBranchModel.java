package net.arna.jcraft.client.model.entity;

import mod.azure.azurelib.model.GeoModel;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.entity.projectile.IceBranchProjectile;
import net.minecraft.resources.ResourceLocation;

public class IceBranchModel extends GeoModel<IceBranchProjectile> {
    @Override
    public ResourceLocation getModelResource(IceBranchProjectile object) {
        return JCraft.id("geo/ice_branch.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(IceBranchProjectile object) {
        return JCraft.id("textures/entity/projectiles/ice_branch.png");
    }

    @Override
    public ResourceLocation getAnimationResource(IceBranchProjectile animatable) {
        return JCraft.id("animations/ice_branch.animation.json");
    }
}
