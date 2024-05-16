package net.arna.jcraft.client.renderer.entity;

import mod.azure.azurelib.renderer.GeoEntityRenderer;
import net.arna.jcraft.client.model.entity.DarbyOlderModel;
import net.arna.jcraft.common.entity.DarbyOlderEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;


public class DarbyOlderRenderer extends GeoEntityRenderer<DarbyOlderEntity> {
    public DarbyOlderRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new DarbyOlderModel());
    }
}
