package net.arna.jcraft.client.renderer.entity;

import mod.azure.azurelib.renderer.GeoEntityRenderer;
import net.arna.jcraft.client.model.entity.SandTornadoModel;
import net.arna.jcraft.common.entity.projectile.SandTornadoEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SandTornadoRenderer extends GeoEntityRenderer<SandTornadoEntity> {
    @Override
    public RenderType getRenderType(SandTornadoEntity animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(getTextureLocation(animatable));
    }

    @Override
    public boolean shouldShowName(@NotNull SandTornadoEntity animatable) {
        return false;
    }

    public SandTornadoRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new SandTornadoModel());
        this.shadowRadius = 1.1f;
    }
}
