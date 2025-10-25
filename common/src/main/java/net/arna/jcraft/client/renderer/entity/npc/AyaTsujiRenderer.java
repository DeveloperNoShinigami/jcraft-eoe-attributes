package net.arna.jcraft.client.renderer.entity.npc;

import net.arna.jcraft.client.model.entity.npc.AyaTsujiModel;
import net.arna.jcraft.client.renderer.entity.AbstractEntityRenderer;
import net.arna.jcraft.common.entity.npc.AyaTsujiEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * The {@link AbstractEntityRenderer} for {@link AyaTsujiEntity}
 * @see AyaTsujiModel
 */
@Environment(EnvType.CLIENT)
public class AyaTsujiRenderer extends AbstractEntityRenderer<AyaTsujiEntity> {

    public static final String ID = "aya_tsuji";

    public AyaTsujiRenderer(final EntityRendererProvider.Context renderManager) {
        super(renderManager, () -> new EntityAnimator<>(ID), ID);
    }
}
