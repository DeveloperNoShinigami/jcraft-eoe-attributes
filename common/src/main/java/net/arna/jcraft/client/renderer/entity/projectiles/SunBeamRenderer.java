package net.arna.jcraft.client.renderer.entity.projectiles;

import lombok.NonNull;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.entity.projectile.SunBeamProjectile;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;

/**
 * The {@link ProjectileRenderer} for {@link SunBeamProjectile}.
 */
@Environment(EnvType.CLIENT)
public class SunBeamRenderer extends ProjectileRenderer<SunBeamProjectile> {
    public static final String ID = "sunbeam";

    // TODO fix skins

    public SunBeamRenderer(final @NonNull EntityRendererProvider.Context context) {
        super(context, () -> new EntityAnimator<>(ID), b -> b
                        .setRenderType(RenderType.eyes(JCraft.id(TEXTURE_STR_TEMPLATE.formatted(ID)))),
                ID);
    }

    @Override
    public int getBlockLightLevel(final @NonNull SunBeamProjectile entity, final @NonNull BlockPos pos) {
        return 15;
    }

}
