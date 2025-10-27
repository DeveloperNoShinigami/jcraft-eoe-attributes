package net.arna.jcraft.client.renderer.entity.projectiles;

import lombok.NonNull;
import net.arna.jcraft.client.model.JProjectileModel;
import net.arna.jcraft.common.entity.projectile.AnkhProjectile;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

/**
 * The {@link ProjectileRenderer} for {@link AnkhProjectile}.
 */
@Environment(EnvType.CLIENT)
public class AnkhRenderer extends ProjectileRenderer<AnkhProjectile> {

    public AnkhRenderer(final @NonNull EntityRendererProvider.Context context) {
        super(context, "ankh");
    }

    @Override
    public int getBlockLightLevel(final @NonNull AnkhProjectile entity, final @NonNull BlockPos pos) {
        return 15;
    }

    /*@Override
    public RenderType getRenderType(final @NonNull AnkhProjectile animatable, final ResourceLocation texture,
                                    final @Nullable MultiBufferSource bufferSource, final float partialTick) {
        return RenderType.eyes(texture);
    }*/
}
