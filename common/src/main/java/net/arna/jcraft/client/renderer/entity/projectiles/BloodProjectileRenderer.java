package net.arna.jcraft.client.renderer.entity.projectiles;

import lombok.NonNull;
import net.arna.jcraft.client.model.JProjectileModel;
import net.arna.jcraft.common.entity.projectile.BloodProjectile;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

/**
 * The {@link ProjectileRenderer} for {@link BloodProjectile}.
 */
@Environment(EnvType.CLIENT)
public class BloodProjectileRenderer extends ProjectileRenderer<BloodProjectile> {
    public BloodProjectileRenderer(final @NonNull EntityRendererProvider.Context context) {
        super(context, "bloodprojectile");
    }

    /*
    @Override
    public RenderType getRenderType(final BloodProjectile animatable, final ResourceLocation texture, final MultiBufferSource bufferSource, final float partialTick) {
        return RenderType.entityTranslucent(texture);
    }*/
}
