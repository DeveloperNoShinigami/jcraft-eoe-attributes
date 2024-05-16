package net.arna.jcraft.client.renderer.entity.stands;

import net.arna.jcraft.client.model.entity.CreamModel;
import net.arna.jcraft.common.entity.stand.CreamEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class CreamRenderer extends StandEntityRenderer<CreamEntity> {

    public CreamRenderer(EntityRendererProvider.Context context) {
        super(context, new CreamModel());
    }

    @Override
    public RenderType getRenderType(CreamEntity animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return animatable.getVoidTime() > 0 ? RenderType.entitySolid(texture) : super.getRenderType(animatable, texture, bufferSource, partialTick);
    }

    @Override
    protected float getRed(CreamEntity stand, float red, float alpha) {
        return red - (1f - alpha) / 2f;
    }

    @Override
    protected float getGreen(CreamEntity stand, float green, float alpha) {
        return green - (1f - alpha) / 2f;
    }
}
