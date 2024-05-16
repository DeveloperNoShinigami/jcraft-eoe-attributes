package net.arna.jcraft.client.renderer.entity;

import net.arna.jcraft.client.model.entity.GERScorpionModel;
import net.arna.jcraft.common.entity.GERScorpionEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class GERScorpionRenderer extends GeoEntityRenderer<GERScorpionEntity> {
    public GERScorpionRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new GERScorpionModel());
    }
}
