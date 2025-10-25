package net.arna.jcraft.client.renderer.entity.projectiles;

import lombok.NonNull;
import net.arna.jcraft.client.model.JProjectileModel;
import net.arna.jcraft.common.entity.projectile.LaserProjectile;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

/**
 * The {@link ProjectileRenderer} for {@link LaserProjectile}.
 */
@Environment(EnvType.CLIENT)
public class LaserProjectileRenderer extends ProjectileRenderer<LaserProjectile> {
    public LaserProjectileRenderer(final @NonNull EntityRendererProvider.Context context) {
        super(context, "laser");
    }

    /*@Override
    public RenderType getRenderType(final LaserProjectile animatable, final ResourceLocation texture, final MultiBufferSource bufferSource, final float partialTick) {
        return RenderType.entityTranslucent(texture);
    }*/
}
