package net.arna.jcraft.client.renderer.entity;

import mod.azure.azurelib.renderer.GeoEntityRenderer;
import net.arna.jcraft.client.model.entity.DarbyYoungerModel;
import net.arna.jcraft.common.entity.DarbyYoungerEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class DarbyYoungerRenderer extends GeoEntityRenderer<DarbyYoungerEntity> {
    public DarbyYoungerRenderer(final EntityRendererProvider.Context renderManager) {
        super(renderManager, new DarbyYoungerModel());
    }
}
