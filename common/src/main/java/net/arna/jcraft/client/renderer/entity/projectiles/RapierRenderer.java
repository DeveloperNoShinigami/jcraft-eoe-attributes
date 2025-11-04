package net.arna.jcraft.client.renderer.entity.projectiles;

import lombok.NonNull;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.client.model.entity.RapierModel;
import net.arna.jcraft.common.entity.projectile.RapierProjectile;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * The {@link ProjectileRenderer} for {@link RapierProjectile}.
 * @see RapierModel
 */
public class RapierRenderer extends ProjectileRenderer<RapierProjectile> {

    public static final String ID = "rapier";

    // TODO fix skins
    public RapierRenderer(final @NonNull EntityRendererProvider.Context context) {
        super(context, () -> new EntityAnimator<>("animations/knife.animation.json"), b -> b
                .setRenderType(RenderType.entityTranslucent(JCraft.id(TEXTURE_STR_TEMPLATE.formatted(ID)))),
                ID);
    }

}
