package net.arna.jcraft.client.model.entity;

import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.entity.projectile.RapierProjectile;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

/**
 * The model for {@link RapierProjectile}.
 * @see net.arna.jcraft.client.renderer.entity.projectiles.RapierRenderer RapierRenderer
 */
public final class RapierModel {
    private static final Map<Integer, ResourceLocation> skins = new HashMap<>();

    static {
        for (int i = 0; i < 4; i++) {
            skins.put(i, JCraft.id("textures/entity/stands/silver_chariot/rapier_" +
                    (i == 0 ? "default" : "skin" + i) + ".png"));
        }
    }

    private static final ResourceLocation model = JCraft.id("geo/rapier.geo.json");
    private static final ResourceLocation animation = JCraft.id("animations/knife.animation.json");

    /*@Override
    public ResourceLocation getModelResource(final RapierProjectile animatable) {
        return model;
    }

    @Override
    public ResourceLocation getTextureResource(final RapierProjectile animatable) {
        final int skin = animatable.getSkin();
        return switch (skin) {
            case -2 -> RapierProjectile.POSSESSED_TEXTURE;
            case -1 -> RapierProjectile.ARMOR_OFF_TEXTURE;
            default -> skins.get(skin);
        };
    }

    @Override
    public ResourceLocation getAnimationResource(final RapierProjectile animatable) {
        return animation;
    }*/
}
