package net.arna.jcraft.client.renderer.entity.stands;

import mod.azure.azurelib.renderer.GeoEntityRenderer;
import net.arna.jcraft.client.model.entity.FooFightersModel;
import net.arna.jcraft.common.entity.stand.FooFightersEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class FooFightersRenderer extends GeoEntityRenderer<FooFightersEntity> {

    public FooFightersRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new FooFightersModel());
    }

}
