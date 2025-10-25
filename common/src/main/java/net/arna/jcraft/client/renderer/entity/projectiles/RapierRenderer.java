package net.arna.jcraft.client.renderer.entity.projectiles;

import net.arna.jcraft.client.model.entity.RapierModel;
import net.arna.jcraft.common.entity.projectile.RapierProjectile;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

/**
 * The {@link ProjectileRenderer} for {@link RapierProjectile}.
 * @see RapierModel
 */
public class RapierRenderer extends ProjectileRenderer<RapierProjectile> {

    public RapierRenderer(final EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, "rapier");
    }

    /*@Override
    public RenderType getRenderType(final RapierProjectile animatable, final ResourceLocation texture, final MultiBufferSource bufferSource, final float partialTick) {
        return RenderType.entityTranslucent(texture);
    }*/
}
