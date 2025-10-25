package net.arna.jcraft.client.renderer.entity.projectiles;

import lombok.NonNull;
import net.arna.jcraft.client.model.entity.RazorModel;
import net.arna.jcraft.common.entity.projectile.RazorProjectile;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

/**
 * The {@link ProjectileRenderer} for {@link RazorProjectile}.
 */
@Environment(EnvType.CLIENT)
public class RazorRenderer extends ProjectileRenderer<RazorProjectile> {

    // TODO fix textures

    public RazorRenderer(final @NonNull EntityRendererProvider.Context context) {
        super(context, "razor");
    }

    /*@Override
    public RenderType getRenderType(final RazorProjectile animatable, final ResourceLocation texture, final MultiBufferSource bufferSource, final float partialTick) {
        return RenderType.entityTranslucent(texture);
    }*/
}