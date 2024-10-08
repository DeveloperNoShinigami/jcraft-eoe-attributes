package net.arna.jcraft.client.model.entity;

import mod.azure.azurelib.model.GeoModel;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.entity.projectile.IceBranchProjectile;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

/**
 * The {@link GeoModel} for {@link IceBranchProjectile}.
 * @see net.arna.jcraft.client.renderer.entity.projectiles.IceBranchRenderer IceBranchRenderer
 */
public class IceBranchModel extends GeoModel<IceBranchProjectile> {
    public static final Map<Integer, ResourceLocation> skins = new HashMap<>();
    static {
        for (int i = 0; i < 3; i++) {
            skins.put(i, JCraft.id("textures/entity/ice_branch/ice_branch_" + i + ".png"));
        }
    }
    @Override
    public ResourceLocation getModelResource(final IceBranchProjectile object) {
        return JCraft.id("geo/ice_branch.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(final IceBranchProjectile object) {
        return skins.get(object.getId() % 3);
    }

    @Override
    public ResourceLocation getAnimationResource(final IceBranchProjectile animatable) {
        return JCraft.id("animations/ice_branch.animation.json");
    }
}
