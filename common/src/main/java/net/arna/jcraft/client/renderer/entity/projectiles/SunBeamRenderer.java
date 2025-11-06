package net.arna.jcraft.client.renderer.entity.projectiles;

import lombok.NonNull;
import mod.azure.azurelib.render.entity.AzEntityRendererConfig;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.entity.projectile.SunBeamProjectile;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.stream.IntStream;

/**
 * The {@link ProjectileRenderer} for {@link SunBeamProjectile}.
 */
@Environment(EnvType.CLIENT)
public class SunBeamRenderer extends ProjectileRenderer<SunBeamProjectile> {
    protected static final List<ResourceLocation> SKINS = IntStream.range(0, 4).mapToObj(
            i -> JCraft.id("textures/entity/sunbeam/skin_" + i + ".png")).toList();

    public static final String ID = "sunbeam";

    public SunBeamRenderer(final @NonNull EntityRendererProvider.Context context) {
        super(AzEntityRendererConfig.<SunBeamProjectile>builder(
                entity -> JCraft.id(MODEL_STR_TEMPLATE.formatted(ID)),
                entity -> SKINS.get(entity.getSkin())
                )
                .setRenderType(entity -> RenderType.eyes(SKINS.get(entity.getSkin())))
                .setAnimatorProvider(() -> new EntityAnimator<>(ID))
                .build(),
                context, ID);
    }

    @Override
    public int getBlockLightLevel(final @NonNull SunBeamProjectile entity, final @NonNull BlockPos pos) {
        return 15;
    }

}
