package net.arna.jcraft.client.model.entity;

import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.entity.projectile.SunBeamProjectile;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

/**
 * The model for {@link SunBeamProjectile}.
 * @see net.arna.jcraft.client.renderer.entity.projectiles.SunBeamRenderer
 */
public final class SunBeamModel {
    private static final Map<Integer, ResourceLocation> skins = new HashMap<>();

    static {
        for (int i = 0; i < 4; i++) {
            skins.put(i, JCraft.id("textures/entity/sunbeam/skin_" + i + ".png"));
        }
    }

    private static final ResourceLocation model = JCraft.id("geo/sunbeam.geo.json");
    private static final ResourceLocation animation = JCraft.id("animations/sunbeam.animation.json");

    /*
    @Override
    public ResourceLocation getModelResource(final SunBeamProjectile animatable) {
        return model;
    }

    @Override
    public ResourceLocation getTextureResource(final SunBeamProjectile animatable) {
        return skins.get(animatable.getSkin());
    }

    @Override
    public ResourceLocation getAnimationResource(final SunBeamProjectile animatable) {
        return animation;
    }*/

}
