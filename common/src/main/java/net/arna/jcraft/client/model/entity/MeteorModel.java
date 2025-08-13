package net.arna.jcraft.client.model.entity;

import mod.azure.azurelib.model.GeoModel;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.entity.projectile.MeteorProjectile;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

/**
 * The {@link GeoModel} for {@link MeteorProjectile}.
 * @see net.arna.jcraft.client.renderer.entity.projectiles.MeteorRenderer MeteorRenderer
 */
public final class MeteorModel extends GeoModel<MeteorProjectile> {
    private static final Map<Integer, ResourceLocation> skins = new HashMap<>();

    static {
        for (int i = 0; i < 4; i++) {
            skins.put(i, JCraft.id("textures/entity/meteor/skin_" + i + ".png"));
        }
    }

    private static final ResourceLocation model = JCraft.id("geo/meteor.geo.json");
    private static final ResourceLocation animation = JCraft.id("animations/meteor.animation.json");

    @Override
    public ResourceLocation getModelResource(final MeteorProjectile animatable) {
        return model;
    }

    @Override
    public ResourceLocation getTextureResource(final MeteorProjectile animatable) {
        return skins.get(animatable.getSkin());
    }

    @Override
    public ResourceLocation getAnimationResource(final MeteorProjectile animatable) {
        return animation;
    }
}
