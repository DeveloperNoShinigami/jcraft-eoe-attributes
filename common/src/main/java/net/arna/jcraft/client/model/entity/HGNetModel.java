package net.arna.jcraft.client.model.entity;

import mod.azure.azurelib.model.GeoModel;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.entity.projectile.HGNetEntity;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

/**
 * The {@link GeoModel} for {@link HGNetEntity}.
 * @see net.arna.jcraft.client.renderer.entity.HGNetRenderer HGNetRenderer
 * @see net.arna.jcraft.client.renderer.entity.layer.HGNetGlowLayer
 */
public final class HGNetModel extends GeoModel<HGNetEntity> {
    private static final Map<Integer, ResourceLocation> skins = new HashMap<>();

    static {
        for (int i = 0; i < 4; i++) {
            skins.put(i, JCraft.id("textures/entity/hg_nets/" + i + ".png"));
        }
    }

    private static final ResourceLocation model = JCraft.id("geo/hg_nets.geo.json");
    private static final ResourceLocation animation = JCraft.id("animations/hg_nets.animation.json");

    @Override
    public ResourceLocation getModelResource(final HGNetEntity animatable) {
        return model;
    }

    @Override
    public ResourceLocation getTextureResource(final HGNetEntity animatable) {
        return skins.get(animatable.getSkin());
    }

    @Override
    public ResourceLocation getAnimationResource(final HGNetEntity animatable) {
        return animation;
    }

}
