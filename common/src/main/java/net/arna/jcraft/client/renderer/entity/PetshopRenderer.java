package net.arna.jcraft.client.renderer.entity;

import net.arna.jcraft.JCraft;
import net.arna.jcraft.client.model.entity.PetshopModel;
import net.arna.jcraft.common.entity.PetshopEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class PetshopRenderer extends GeoEntityRenderer<PetshopEntity> {

    public PetshopRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new PetshopModel());
    }

    @Override
    public ResourceLocation getTextureLocation(PetshopEntity animatable) {
        return JCraft.id("textures/entity/petshop.png");
    }
}
