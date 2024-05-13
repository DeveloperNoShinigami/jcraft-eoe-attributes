package net.arna.jcraft.client.renderer.entity;

import net.arna.jcraft.JCraft;
import net.arna.jcraft.client.model.entity.PetshopModel;
import net.arna.jcraft.common.entity.PetshopEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class PetshopRenderer extends GeoEntityRenderer<PetshopEntity> {

    public PetshopRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new PetshopModel());
    }

    @Override
    public Identifier getTexture(PetshopEntity entity) {
        return JCraft.id("textures/entity/petshop.png");
    }
}
