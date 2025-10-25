package net.arna.jcraft.client.renderer.entity;

import lombok.NonNull;
import net.arna.jcraft.client.model.entity.SandTornadoModel;
import net.arna.jcraft.common.entity.projectile.SandTornadoEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

/**
 * The {@link AbstractEntityRenderer} for {@link SandTornadoEntity}.
 */
@Environment(EnvType.CLIENT)
public class SandTornadoRenderer extends AbstractEntityRenderer<SandTornadoEntity> {

    public static final String ID = "sandtornado";

    public SandTornadoRenderer(final @NonNull EntityRendererProvider.Context context) {
        super(context, () -> new EntityAnimator<>(ID), b -> b.setShadowRadius(1.1f), ID);
    }
    /*
    @Override
    public RenderType getRenderType(final SandTornadoEntity animatable, final ResourceLocation texture, final @Nullable MultiBufferSource bufferSource, final float partialTick) {
        return RenderType.entityTranslucent(getGeoModel().getTextureResource(animatable));
    }
    */

    @Override
    public boolean shouldShowName(final @NonNull SandTornadoEntity animatable) {
        return false;
    }
}
