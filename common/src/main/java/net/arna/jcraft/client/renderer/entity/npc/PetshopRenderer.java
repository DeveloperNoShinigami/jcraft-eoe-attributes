package net.arna.jcraft.client.renderer.entity.npc;

import mod.azure.azurelib.renderer.GeoEntityRenderer;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.client.model.entity.npc.PetshopModel;
import net.arna.jcraft.common.entity.npc.PetshopEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

/**
 * The {@link GeoEntityRenderer} for {@link PetshopEntity}
 * @see PetshopModel
 */
public class PetshopRenderer extends GeoEntityRenderer<PetshopEntity> {
    public PetshopRenderer(final EntityRendererProvider.Context renderManager) {
        super(renderManager, new PetshopModel());
    }

    @Override
    public ResourceLocation getTextureLocation(final PetshopEntity animatable) {
        return JCraft.id("textures/entity/petshop.png");
    }
}
