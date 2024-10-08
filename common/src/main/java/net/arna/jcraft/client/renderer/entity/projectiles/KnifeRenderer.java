package net.arna.jcraft.client.renderer.entity.projectiles;

import net.arna.jcraft.client.model.entity.KnifeModel;
import net.arna.jcraft.common.entity.projectile.KnifeProjectile;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LightLayer;

/**
 * The {@link GeoProjectileRenderer} for {@link KnifeProjectile}.
 * @see KnifeModel
 */
public class KnifeRenderer extends GeoProjectileRenderer<KnifeProjectile> {

    public KnifeRenderer(final EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new KnifeModel());
    }

    @Override
    protected int getBlockLightLevel(final KnifeProjectile entityIn, final BlockPos blockPos) {
        return (entityIn.getLightning() || entityIn.isOnFire()) ? 15 : entityIn.level().getBrightness(LightLayer.BLOCK, entityIn.blockPosition());
    }

    @Override
    public RenderType getRenderType(final KnifeProjectile animatable, final ResourceLocation texture, final MultiBufferSource bufferSource, final float partialTick) {
        return RenderType.entityTranslucent(texture);
    }


}
