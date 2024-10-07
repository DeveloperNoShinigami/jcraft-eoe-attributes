package net.arna.jcraft.client.renderer.entity.npc;

import mod.azure.azurelib.renderer.GeoEntityRenderer;
import net.arna.jcraft.client.model.entity.npc.DarbyOlderModel;
import net.arna.jcraft.common.entity.npc.DarbyOlderEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class DarbyOlderRenderer extends GeoEntityRenderer<DarbyOlderEntity> {
    public DarbyOlderRenderer(final EntityRendererProvider.Context renderManager) {
        super(renderManager, new DarbyOlderModel());
    }
}
