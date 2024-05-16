package net.arna.jcraft.client.renderer.entity.stands;

import net.arna.jcraft.client.model.entity.OsirisModel;
import net.arna.jcraft.common.entity.stand.OsirisEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class OsirisRenderer extends GeoEntityRenderer<OsirisEntity> {

    public OsirisRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new OsirisModel());
    }
}
