package net.arna.jcraft.client.renderer.entity;

import net.arna.jcraft.client.model.entity.AyaTsujiModel;
import net.arna.jcraft.common.entity.AyaTsujiEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class AyaTsujiRenderer extends GeoEntityRenderer<AyaTsujiEntity> {
    public AyaTsujiRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new AyaTsujiModel());
    }
}
