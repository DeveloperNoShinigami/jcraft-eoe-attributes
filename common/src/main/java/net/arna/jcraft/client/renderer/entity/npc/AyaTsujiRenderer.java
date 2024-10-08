package net.arna.jcraft.client.renderer.entity.npc;

import mod.azure.azurelib.renderer.GeoEntityRenderer;
import net.arna.jcraft.client.model.entity.npc.AyaTsujiModel;
import net.arna.jcraft.common.entity.npc.AyaTsujiEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * The {@link GeoEntityRenderer} for {@link AyaTsujiEntity}
 * @see AyaTsujiModel
 */
public class AyaTsujiRenderer extends GeoEntityRenderer<AyaTsujiEntity> {
    public AyaTsujiRenderer(final EntityRendererProvider.Context renderManager) {
        super(renderManager, new AyaTsujiModel());
    }
}
