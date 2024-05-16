package net.arna.jcraft.client.renderer.entity.stands;

import net.arna.jcraft.client.model.entity.TheFoolModel;
import net.arna.jcraft.common.entity.stand.TheFoolEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class TheFoolRenderer extends StandEntityRenderer<TheFoolEntity> {
    public TheFoolRenderer(EntityRendererProvider.Context context) {
        super(context, new TheFoolModel());
    }
}
