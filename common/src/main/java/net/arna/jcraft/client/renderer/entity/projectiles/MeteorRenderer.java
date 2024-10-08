package net.arna.jcraft.client.renderer.entity.projectiles;

import lombok.NonNull;
import net.arna.jcraft.client.model.entity.MeteorModel;
import net.arna.jcraft.common.entity.projectile.MeteorProjectile;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

/**
 * The {@link GeoProjectileRenderer} for {@link MeteorProjectile}.
 * @see MeteorModel
 */
public class MeteorRenderer extends GeoProjectileRenderer<MeteorProjectile> {
    public MeteorRenderer(final EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new MeteorModel());
    }

    @Override
    protected int getBlockLightLevel(final @NonNull MeteorProjectile entity, final @NonNull BlockPos pos) {
        return 15;
    }

    @Override
    public RenderType getRenderType(final MeteorProjectile animatable, final ResourceLocation texture, final MultiBufferSource bufferSource, final float partialTick) {
        return RenderType.eyes(texture);
    }
}
