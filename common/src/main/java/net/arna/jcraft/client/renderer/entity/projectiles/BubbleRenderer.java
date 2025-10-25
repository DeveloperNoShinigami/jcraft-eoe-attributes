package net.arna.jcraft.client.renderer.entity.projectiles;

import lombok.NonNull;
import net.arna.jcraft.client.model.JProjectileModel;
import net.arna.jcraft.common.entity.projectile.BubbleProjectile;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

/**
 * The {@link ProjectileRenderer} for {@link BubbleProjectile}.
 */
@Environment(EnvType.CLIENT)
public class BubbleRenderer extends ProjectileRenderer<BubbleProjectile> {
    public BubbleRenderer(final @NonNull EntityRendererProvider.Context context) {
        super(context, "bubble");
    }

    /*@Override
    public RenderType getRenderType(final BubbleProjectile animatable, final ResourceLocation texture, final MultiBufferSource bufferSource, final float partialTick) {
        return RenderType.entityTranslucent(texture);
    }*/
}
