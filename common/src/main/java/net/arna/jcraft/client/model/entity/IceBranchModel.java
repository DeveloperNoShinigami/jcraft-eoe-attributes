package net.arna.jcraft.client.model.entity;

import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.entity.projectile.IceBranchProjectile;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

/**
 * The model for {@link IceBranchProjectile}.
 * @see net.arna.jcraft.client.renderer.entity.projectiles.IceBranchRenderer IceBranchRenderer
 */
public final class IceBranchModel {//extends GeoModel<IceBranchProjectile> {
    public static final Map<Integer, ResourceLocation> skins = new HashMap<>();

    static {
        for (int i = 0; i < 3; i++) {
            skins.put(i, JCraft.id("textures/entity/ice_branch/ice_branch_" + i + ".png"));
        }
    }

    private static final ResourceLocation model = JCraft.id("geo/ice_branch.geo.json");
    private static final ResourceLocation animation = JCraft.id("animations/ice_branch.animation.json");

    /*
    @Override
    public ResourceLocation getModelResource(final IceBranchProjectile animatable) {
        return model;
    }

    @Override
    public ResourceLocation getTextureResource(final IceBranchProjectile animatable) {
        return skins.get(animatable.getId() % 3);
    }

    @Override
    public ResourceLocation getAnimationResource(final IceBranchProjectile animatable) {
        return animation;
    }*/
}
