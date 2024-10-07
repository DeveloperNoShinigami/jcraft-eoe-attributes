package net.arna.jcraft.client.renderer.entity.npc;

import mod.azure.azurelib.renderer.GeoEntityRenderer;
import net.arna.jcraft.client.model.entity.npc.DarbyYoungerModel;
import net.arna.jcraft.common.entity.npc.DarbyYoungerEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class DarbyYoungerRenderer extends GeoEntityRenderer<DarbyYoungerEntity> {
    public DarbyYoungerRenderer(final EntityRendererProvider.Context renderManager) {
        super(renderManager, new DarbyYoungerModel());
    }
}
