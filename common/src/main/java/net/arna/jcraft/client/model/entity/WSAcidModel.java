package net.arna.jcraft.client.model.entity;

import mod.azure.azurelib.model.GeoModel;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.entity.projectile.WSAcidProjectile;
import net.minecraft.resources.ResourceLocation;

/**
 * The {@link GeoModel} for {@link WSAcidProjectile}.
 * @see net.arna.jcraft.client.renderer.entity.projectiles.WSAcidRenderer WSAcidRenderer
 */
public class WSAcidModel extends GeoModel<WSAcidProjectile> {
    @Override
    public ResourceLocation getModelResource(final WSAcidProjectile object) {
        return JCraft.id("geo/wsacid.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(final WSAcidProjectile object) {
        return JCraft.id("textures/entity/projectiles/wsacid.png");
    }

    @Override
    public ResourceLocation getAnimationResource(final WSAcidProjectile animatable) {
        return JCraft.id("animations/wsacid.animation.json");
    }

}
