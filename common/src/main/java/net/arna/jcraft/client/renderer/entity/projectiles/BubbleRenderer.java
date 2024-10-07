package net.arna.jcraft.client.renderer.entity.projectiles;

import net.arna.jcraft.client.model.entity.BubbleModel;
import net.arna.jcraft.common.entity.projectile.BubbleProjectile;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class BubbleRenderer extends GeoProjectileRenderer<BubbleProjectile> {
    public BubbleRenderer(final EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new BubbleModel());
    }

    @Override
    public RenderType getRenderType(final BubbleProjectile animatable, final ResourceLocation texture, final MultiBufferSource bufferSource, final float partialTick) {
        return RenderType.entityTranslucent(texture);
    }
}
