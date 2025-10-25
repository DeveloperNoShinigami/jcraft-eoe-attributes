package net.arna.jcraft.client.renderer.entity;

import lombok.NonNull;
import net.arna.jcraft.client.model.entity.HGNetModel;
import net.arna.jcraft.client.renderer.entity.layer.HGNetGlowLayer;
import net.arna.jcraft.common.entity.projectile.HGNetEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * The {@link AbstractEntityRenderer} for {@link HGNetEntity}.
 * @see HGNetModel
 */
@Environment(EnvType.CLIENT)
public class HGNetRenderer extends AbstractEntityRenderer<HGNetEntity> {
    // TODO fix the skins

    public static final String ID = "hg_nets";

    public HGNetRenderer(final @NonNull EntityRendererProvider.Context context) {
        super(context, () -> new EntityAnimator<>(ID),
                b -> b.addRenderLayer(new HGNetGlowLayer(this)).setShadowRadius(1.25f),
                ID);
    }

    @Override
    public boolean shouldShowName(final HGNetEntity animatable) {
        return false;
    }
}
