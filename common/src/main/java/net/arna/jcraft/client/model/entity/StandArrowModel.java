package net.arna.jcraft.client.model.entity;

import mod.azure.azurelib.model.GeoModel;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.entity.projectile.StandArrowEntity;
import net.minecraft.resources.ResourceLocation;

/**
 * The {@link GeoModel} for {@link StandArrowEntity}.
 * @see net.arna.jcraft.client.renderer.entity.projectiles.StandArrowRenderer StandArrowRenderer
 */
public class StandArrowModel extends GeoModel<StandArrowEntity> {
    @Override
    public ResourceLocation getModelResource(StandArrowEntity animatable) {
        return JCraft.id("geo/stand_arrow.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(StandArrowEntity animatable) {
        return JCraft.id("textures/entity/projectiles/stand_arrow.png");
    }

    @Override
    public ResourceLocation getAnimationResource(StandArrowEntity animatable) {
        return JCraft.id("animations/knife.animation.json");
    }
}
