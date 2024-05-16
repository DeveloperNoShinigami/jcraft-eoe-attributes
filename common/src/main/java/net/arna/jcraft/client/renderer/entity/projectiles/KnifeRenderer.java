package net.arna.jcraft.client.renderer.entity.projectiles;

import net.arna.jcraft.client.model.entity.KnifeModel;
import net.arna.jcraft.common.entity.projectile.KnifeProjectile;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LightLayer;


public class KnifeRenderer extends GeoProjectileRenderer<KnifeProjectile> {

    public KnifeRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new KnifeModel());
    }

    protected int getBlockLight(KnifeProjectile entityIn, BlockPos partialTicks) {
        return (entityIn.getLightning() || entityIn.isOnFire()) ? 15 : entityIn.level().getBrightness(LightLayer.BLOCK, entityIn.blockPosition());
    }

    @Override
    public RenderType getRenderType(KnifeProjectile animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(texture);
    }


}
