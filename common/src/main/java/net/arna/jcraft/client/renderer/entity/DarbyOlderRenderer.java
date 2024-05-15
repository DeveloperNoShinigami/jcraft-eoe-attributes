package net.arna.jcraft.client.renderer.entity;

import net.arna.jcraft.client.model.entity.DarbyOlderModel;
import net.arna.jcraft.common.entity.DarbyOlderEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class DarbyOlderRenderer extends GeoEntityRenderer<DarbyOlderEntity> {
    public DarbyOlderRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new DarbyOlderModel());
    }
}
