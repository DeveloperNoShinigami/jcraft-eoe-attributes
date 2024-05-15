package net.arna.jcraft.client.renderer.entity;

import net.arna.jcraft.client.model.entity.DarbyYoungerModel;
import net.arna.jcraft.common.entity.DarbyYoungerEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class DarbyYoungerRenderer extends GeoEntityRenderer<DarbyYoungerEntity> {
    public DarbyYoungerRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new DarbyYoungerModel());
    }
}
