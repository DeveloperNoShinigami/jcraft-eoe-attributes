package net.arna.jcraft.client.renderer.entity.stands;

import mod.azure.azurelib.renderer.GeoEntityRenderer;
import net.arna.jcraft.client.model.entity.GooGooDollsModel;
import net.arna.jcraft.common.entity.stand.GooGooDollsEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class GooGooDollsRenderer extends GeoEntityRenderer<GooGooDollsEntity> {

    public GooGooDollsRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new GooGooDollsModel());
    }

}
