package net.arna.jcraft.client.renderer.entity.projectiles;

import net.arna.jcraft.client.model.JProjectileModel;
import net.arna.jcraft.common.entity.projectile.IcicleProjectile;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

/**
 * The {@link GeoProjectileRenderer} for {@link IcicleProjectile}.
 */
public class IcicleRenderer extends GeoProjectileRenderer<IcicleProjectile> {
    public IcicleRenderer(final EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new JProjectileModel<>("icicle"));
    }

    @Override
    public RenderType getRenderType(final IcicleProjectile animatable, final ResourceLocation texture, final @Nullable MultiBufferSource bufferSource, final float partialTick) {
        return RenderType.entityTranslucent(texture);
    }
}
