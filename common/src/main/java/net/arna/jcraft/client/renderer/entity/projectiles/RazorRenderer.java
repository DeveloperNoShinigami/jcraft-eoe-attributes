package net.arna.jcraft.client.renderer.entity.projectiles;

import lombok.NonNull;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.entity.projectile.RazorProjectile;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * The {@link ProjectileRenderer} for {@link RazorProjectile}.
 */
@Environment(EnvType.CLIENT)
public class RazorRenderer extends ProjectileRenderer<RazorProjectile> {
    public static final String ID = "razor";

    // TODO fix textures

    public RazorRenderer(final @NonNull EntityRendererProvider.Context context) {
        super(context, () -> new EntityAnimator<>(ID), b -> b
                .setRenderType(RenderType.entityTranslucent(JCraft.id(TEXTURE_STR_TEMPLATE.formatted(ID)))),
                ID);
    }

}