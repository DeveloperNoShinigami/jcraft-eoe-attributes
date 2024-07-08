package net.arna.jcraft.client.renderer.entity.stands;

import mod.azure.azurelib.renderer.GeoEntityRenderer;
import net.arna.jcraft.client.model.entity.DiverDownModel;
import net.arna.jcraft.common.entity.stand.DiverDownEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class DiverDownRenderer extends GeoEntityRenderer<DiverDownEntity> {

    public DiverDownRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new DiverDownModel());
    }

}
