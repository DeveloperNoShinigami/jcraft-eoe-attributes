package net.arna.jcraft.client.model.armor;

import mod.azure.azurelib.core.animatable.GeoAnimatable;
import mod.azure.azurelib.model.GeoModel;
import net.arna.jcraft.JCraft;
import net.minecraft.resources.ResourceLocation;

public class JArmorModel<T extends GeoAnimatable> extends GeoModel<T> {//<T extends ArmorItem & IAnimatable> extends AnimatedGeoModel<T> {
    protected final String name;

    public JArmorModel(final String name) {
        this.name = name;
    }

    @Override
    public ResourceLocation getModelResource(final T object) {
        return JCraft.id("geo/" + name + ".geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(final T object) {
        return JCraft.id("textures/armor/" + name + ".png");
    }

    @Override
    public ResourceLocation getAnimationResource(final T animatable) {
        return JCraft.id("animations/" + name + ".animation.json");
    }
}
