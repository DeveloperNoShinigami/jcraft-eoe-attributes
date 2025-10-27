package net.arna.jcraft.client.renderer.entity.projectiles;

import lombok.NonNull;
import net.arna.jcraft.client.model.JProjectileModel;
import net.arna.jcraft.common.entity.projectile.KnifeProjectile;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LightLayer;

/**
 * The {@link ProjectileRenderer} for {@link KnifeProjectile}.
 */
@Environment(EnvType.CLIENT)
public class KnifeRenderer extends ProjectileRenderer<KnifeProjectile> {

    public KnifeRenderer(final @NonNull EntityRendererProvider.Context context) {
        super(context, "knife");
    }

    @Override
    public int getBlockLightLevel(final @NonNull KnifeProjectile entityIn, final @NonNull BlockPos blockPos) {
        return (entityIn.getLightning() || entityIn.isOnFire()) ? 15 : entityIn.level().getBrightness(LightLayer.BLOCK, entityIn.blockPosition());
    }

    /*
    @Override
    public RenderType getRenderType(final KnifeProjectile animatable, final ResourceLocation texture, final MultiBufferSource bufferSource, final float partialTick) {
        return RenderType.entityTranslucent(texture);
    }*/
}
