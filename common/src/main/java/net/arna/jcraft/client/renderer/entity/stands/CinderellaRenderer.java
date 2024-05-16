package net.arna.jcraft.client.renderer.entity.stands;

import mod.azure.azurelib.renderer.GeoEntityRenderer;
import net.arna.jcraft.client.model.entity.CinderellaModel;
import net.arna.jcraft.common.entity.stand.CinderellaEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;


public class CinderellaRenderer extends GeoEntityRenderer<CinderellaEntity> {
    public CinderellaRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new CinderellaModel());
    }
}
