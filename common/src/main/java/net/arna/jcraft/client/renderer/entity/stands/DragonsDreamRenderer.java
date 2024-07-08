package net.arna.jcraft.client.renderer.entity.stands;

import mod.azure.azurelib.renderer.GeoEntityRenderer;
import net.arna.jcraft.client.model.entity.DragonsDreamModel;
import net.arna.jcraft.common.entity.stand.DragonsDreamEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class DragonsDreamRenderer extends GeoEntityRenderer<DragonsDreamEntity> {

    public DragonsDreamRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new DragonsDreamModel());
    }

}
