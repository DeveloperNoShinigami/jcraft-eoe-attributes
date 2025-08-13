package net.arna.jcraft.client.renderer.entity;

import lombok.NonNull;
import mod.azure.azurelib.renderer.GeoEntityRenderer;
import net.arna.jcraft.client.model.entity.SandTornadoModel;
import net.arna.jcraft.common.entity.projectile.SandTornadoEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

/**
 * The {@link GeoEntityRenderer} for {@link SandTornadoEntity}.
 * @see SandTornadoModel
 */
public class SandTornadoRenderer extends GeoEntityRenderer<SandTornadoEntity> {
    @Override
    public RenderType getRenderType(final SandTornadoEntity animatable, final ResourceLocation texture, final @Nullable MultiBufferSource bufferSource, final float partialTick) {
        return RenderType.entityTranslucent(getGeoModel().getTextureResource(animatable));
    }

    @Override
    public boolean shouldShowName(final @NonNull SandTornadoEntity animatable) {
        return false;
    }

    public SandTornadoRenderer(final EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new SandTornadoModel());
        this.shadowRadius = 1.1f;
    }
}
