package net.arna.jcraft.client.renderer.entity;

import mod.azure.azurelib.renderer.GeoEntityRenderer;
import net.arna.jcraft.client.model.entity.SheerHeartAttackModel;
import net.arna.jcraft.common.entity.SheerHeartAttackEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;


public class SheerHeartAttackRenderer extends GeoEntityRenderer<SheerHeartAttackEntity> {

    public SheerHeartAttackRenderer(final EntityRendererProvider.Context context) {
        super(context, new SheerHeartAttackModel());
    }

    @Override
    public RenderType getRenderType(final SheerHeartAttackEntity animatable, final ResourceLocation texture, final @Nullable MultiBufferSource bufferSource, final float partialTick) {
        return RenderType.entityTranslucent(this.getTextureLocation(animatable));
    }
}