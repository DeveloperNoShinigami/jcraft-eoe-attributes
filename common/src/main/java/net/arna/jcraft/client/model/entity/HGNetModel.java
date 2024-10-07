package net.arna.jcraft.client.model.entity;

import mod.azure.azurelib.model.GeoModel;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.entity.projectile.HGNetEntity;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.stream.IntStream;

public class HGNetModel extends GeoModel<HGNetEntity> {
    private static final List<ResourceLocation> skins = IntStream.range(0, 4).mapToObj(
            i -> JCraft.id("textures/entity/hg_nets/" + i + ".png")).toList();

    @Override
    public ResourceLocation getModelResource(final HGNetEntity object) {
        return JCraft.id("geo/hg_nets.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(final HGNetEntity object) {
        return skins.get(object.getSkin());
    }

    @Override
    public ResourceLocation getAnimationResource(final HGNetEntity animatable) {
        return JCraft.id("animations/hg_nets.animation.json");
    }

}
