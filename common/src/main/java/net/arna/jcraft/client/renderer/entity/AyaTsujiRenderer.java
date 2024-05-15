package net.arna.jcraft.client.renderer.entity;

import net.arna.jcraft.client.model.entity.AyaTsujiModel;
import net.arna.jcraft.common.entity.AyaTsujiEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class AyaTsujiRenderer extends GeoEntityRenderer<AyaTsujiEntity> {
    public AyaTsujiRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new AyaTsujiModel());
    }
}
