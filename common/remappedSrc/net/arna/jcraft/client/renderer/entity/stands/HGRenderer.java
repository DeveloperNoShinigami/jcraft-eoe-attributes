package net.arna.jcraft.client.renderer.entity.stands;

import net.arna.jcraft.client.model.entity.HGModel;
import net.arna.jcraft.common.entity.stand.HGEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class HGRenderer extends StandEntityRenderer<HGEntity> {
    public HGRenderer(EntityRendererProvider.Context context) {
        super(context, new HGModel());
    }
}