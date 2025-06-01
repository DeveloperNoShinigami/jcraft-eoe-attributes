package net.arna.jcraft.client.model.entity;

import mod.azure.azurelib.model.GeoModel;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.entity.projectile.RapierProjectile;
import net.minecraft.resources.ResourceLocation;

/**
 * The {@link GeoModel} for {@link RapierProjectile}.
 * @see net.arna.jcraft.client.renderer.entity.projectiles.RapierRenderer RapierRenderer
 */
public class RapierModel extends GeoModel<RapierProjectile> {

    @Override
    public ResourceLocation getModelResource(final RapierProjectile object) {
        return JCraft.id("geo/rapier.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(final RapierProjectile object) {
        int skin = object.getSkin();
        return switch (skin) {
            case -2 -> RapierProjectile.POSSESSED_TEXTURE;
            case -1 -> RapierProjectile.ARMOR_OFF_TEXTURE;
            default -> JCraft.id("textures/entity/stands/silver_chariot/rapier_" +
                    (skin == 0 ? "default" : "skin" + skin) + ".png");
        };
    }

    @Override
    public ResourceLocation getAnimationResource(final RapierProjectile animatable) {
        return JCraft.id("animations/knife.animation.json");
    }
}
