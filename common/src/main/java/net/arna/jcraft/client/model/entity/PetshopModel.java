package net.arna.jcraft.client.model.entity;

import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.entity.PetshopEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class PetshopModel extends GeoModel<PetshopEntity> {
    @Override
    public Identifier getModelResource(PetshopEntity animatable) {
        return JCraft.id("geo/petshop.geo.json");
    }

    @Override
    public Identifier getTextureResource(PetshopEntity animatable) {
        return JCraft.id("textures/entity/petshop.png");
    }

    @Override
    public Identifier getAnimationResource(PetshopEntity animatable) {
        // TODO Arna
        return JCraft.id("petshop");
    }
}
