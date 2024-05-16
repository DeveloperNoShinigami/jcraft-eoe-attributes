package net.arna.jcraft.client.renderer.entity.stands;

import net.arna.jcraft.client.model.entity.HorusModel;
import net.arna.jcraft.common.entity.stand.HorusEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class HorusRenderer extends StandEntityRenderer<HorusEntity> {
    public HorusRenderer(EntityRendererProvider.Context context) {
        super(context, new HorusModel());
    }
}
