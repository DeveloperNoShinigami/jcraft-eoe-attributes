package net.arna.jcraft.client.renderer.entity.stands;

import net.arna.jcraft.client.model.entity.CinderellaModel;
import net.arna.jcraft.common.entity.stand.CinderellaEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class CinderellaRenderer extends GeoEntityRenderer<CinderellaEntity> {
    public CinderellaRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new CinderellaModel());
    }
}
