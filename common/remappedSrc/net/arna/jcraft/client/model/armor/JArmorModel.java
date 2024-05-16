package net.arna.jcraft.client.model.armor;

import net.arna.jcraft.JCraft;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;

public class JArmorModel<T extends GeoAnimatable> extends GeoModel<T> {//<T extends ArmorItem & IAnimatable> extends AnimatedGeoModel<T> {
    protected final String name;

    public JArmorModel(String name) {
        this.name = name;
    }

    @Override
    public ResourceLocation getModelResource(T object) {
        return JCraft.id("geo/" + name + ".geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(T object) {
        return JCraft.id("textures/armor/" + name + ".png");
    }

    @Override
    public ResourceLocation getAnimationResource(T animatable) {
        return JCraft.id("animations/" + name + ".animation.json");
    }
}
