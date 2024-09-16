package net.arna.jcraft.client.renderer.entity.projectiles;

import net.arna.jcraft.client.model.entity.ScalpelModel;
import net.arna.jcraft.common.entity.projectile.ScalpelProjectile;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class ScalpelRenderer extends GeoProjectileRenderer<ScalpelProjectile> {
    public ScalpelRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new ScalpelModel());
    }

    @Override
    public RenderType getRenderType(ScalpelProjectile animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(texture);
    }
}