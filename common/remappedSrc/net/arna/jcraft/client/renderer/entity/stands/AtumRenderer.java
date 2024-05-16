package net.arna.jcraft.client.renderer.entity.stands;

import net.arna.jcraft.client.model.entity.AtumModel;
import net.arna.jcraft.common.entity.stand.AtumEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class AtumRenderer extends GeoEntityRenderer<AtumEntity> {

    public AtumRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new AtumModel());
    }
}
