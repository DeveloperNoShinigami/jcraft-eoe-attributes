package net.arna.jcraft.client.renderer.entity;

import mod.azure.azurelib.renderer.GeoEntityRenderer;
import net.arna.jcraft.client.model.entity.GERScorpionModel;
import net.arna.jcraft.common.entity.GERScorpionEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;


public class GERScorpionRenderer extends GeoEntityRenderer<GERScorpionEntity> {
    public GERScorpionRenderer(final EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new GERScorpionModel());
    }
}
