package net.arna.jcraft.client.renderer.entity.stands;

import mod.azure.azurelib.renderer.GeoEntityRenderer;
import net.arna.jcraft.client.model.entity.ChariotRequiemModel;
import net.arna.jcraft.common.entity.stand.ChariotRequiemEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class ChariotRequiemRenderer extends GeoEntityRenderer<ChariotRequiemEntity> {
    public ChariotRequiemRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ChariotRequiemModel());
    }
}
