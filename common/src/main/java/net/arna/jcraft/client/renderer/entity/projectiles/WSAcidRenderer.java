package net.arna.jcraft.client.renderer.entity.projectiles;

import net.arna.jcraft.client.model.entity.WSAcidModel;
import net.arna.jcraft.common.entity.projectile.WSAcidProjectile;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class WSAcidRenderer extends GeoProjectileRenderer<WSAcidProjectile> {
    public WSAcidRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new WSAcidModel());
    }

    @Override
    public RenderType getRenderType(WSAcidProjectile animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(texture);
    }
}
