package net.arna.jcraft.client.renderer.entity.stands;

import mod.azure.azurelib.renderer.GeoEntityRenderer;
import net.arna.jcraft.client.model.entity.AtumModel;
import net.arna.jcraft.common.entity.stand.AtumEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;


public class AtumRenderer extends GeoEntityRenderer<AtumEntity> {

    public AtumRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new AtumModel());
    }
}
