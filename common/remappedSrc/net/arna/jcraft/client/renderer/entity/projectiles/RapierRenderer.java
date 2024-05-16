package net.arna.jcraft.client.renderer.entity.projectiles;

import net.arna.jcraft.client.model.entity.RapierModel;
import net.arna.jcraft.common.entity.projectile.RapierProjectile;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class RapierRenderer extends GeoProjectileRenderer<RapierProjectile> {

    public RapierRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new RapierModel());
    }

    @Override
    public RenderType getRenderType(RapierProjectile animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(texture);
    }
}
