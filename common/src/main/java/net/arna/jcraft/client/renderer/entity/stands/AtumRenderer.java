package net.arna.jcraft.client.renderer.entity.stands;

import net.arna.jcraft.client.model.entity.AtumModel;
import net.arna.jcraft.common.entity.stand.AtumEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class AtumRenderer extends GeoEntityRenderer<AtumEntity> {

    public AtumRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new AtumModel());
    }
}
