package net.arna.jcraft.client.renderer.entity.projectiles;

import lombok.NonNull;
import net.arna.jcraft.client.model.JProjectileModel;
import net.arna.jcraft.common.entity.projectile.IcicleProjectile;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

/**
 * The {@link ProjectileRenderer} for {@link IcicleProjectile}.
 */
@Environment(EnvType.CLIENT)
public class IcicleRenderer extends ProjectileRenderer<IcicleProjectile> {
    public IcicleRenderer(final @NonNull EntityRendererProvider.Context context) {
        super(context, "icicle");
    }

    /*
    @Override
    public RenderType getRenderType(final IcicleProjectile animatable, final ResourceLocation texture, final @Nullable MultiBufferSource bufferSource, final float partialTick) {
        return RenderType.entityTranslucent(texture);
    }
    */
}
