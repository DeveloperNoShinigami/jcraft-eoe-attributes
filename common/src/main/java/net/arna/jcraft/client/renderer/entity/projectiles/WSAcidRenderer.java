package net.arna.jcraft.client.renderer.entity.projectiles;

import net.arna.jcraft.client.model.JProjectileModel;
import net.arna.jcraft.common.entity.projectile.WSAcidProjectile;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

/**
 * The {@link GeoProjectileRenderer} for {@link WSAcidProjectile}.
 */
public class WSAcidRenderer extends GeoProjectileRenderer<WSAcidProjectile> {
    public WSAcidRenderer(final EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new JProjectileModel<>("wsacid", true));
    }

    @Override
    public RenderType getRenderType(final WSAcidProjectile animatable, final ResourceLocation texture, final MultiBufferSource bufferSource, final float partialTick) {
        return RenderType.entityTranslucent(texture);
    }
}
