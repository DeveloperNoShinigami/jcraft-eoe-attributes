package net.arna.jcraft.client.model.entity;

import mod.azure.azurelib.model.GeoModel;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.entity.projectile.RazorProjectile;
import net.minecraft.resources.ResourceLocation;

/**
 * The {@link GeoModel} for {@link RazorProjectile}.
 */
public final class RazorModel extends GeoModel<RazorProjectile> {

    private static final ResourceLocation model = JCraft.id("geo/razor.geo.json");
    private static final ResourceLocation animation = JCraft.id("animations/razor.animation.json");

    @Override
    public ResourceLocation getModelResource(final RazorProjectile animatable) {
        return model;
    }

    private static final ResourceLocation
            NAIL = JCraft.id("textures/entity/projectiles/nail.png"),
            SCISSORS = JCraft.id("textures/entity/projectiles/scissors.png"),
            RAZOR = JCraft.id("textures/entity/projectiles/razor.png");
    @Override
    public ResourceLocation getTextureResource(final RazorProjectile animatable) {
        return switch (animatable.getId() % 3) {
            case (1) -> NAIL;
            case (2) -> SCISSORS;
            default -> RAZOR;
        };
    }

    @Override
    public ResourceLocation getAnimationResource(final RazorProjectile animatable) {
        return animation;
    }
}
