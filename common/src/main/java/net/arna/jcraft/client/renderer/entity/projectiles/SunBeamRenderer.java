package net.arna.jcraft.client.renderer.entity.projectiles;

import com.mojang.blaze3d.vertex.PoseStack;
import lombok.NonNull;
import net.arna.jcraft.client.model.entity.SunBeamModel;
import net.arna.jcraft.common.entity.projectile.SunBeamProjectile;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

/**
 * The {@link ProjectileRenderer} for {@link SunBeamProjectile}.
 */
@Environment(EnvType.CLIENT)
public class SunBeamRenderer extends ProjectileRenderer<SunBeamProjectile> {

    // TODO fix skins

    public SunBeamRenderer(final @NonNull EntityRendererProvider.Context context) {
        super(context, "sunbeam");
    }

    @Override
    public int getBlockLightLevel(final SunBeamProjectile entity, final BlockPos pos) {
        return 15;
    }

    /*
    @Override
    public RenderType getRenderType(final SunBeamProjectile animatable, final ResourceLocation texture, final MultiBufferSource bufferSource, final float partialTick) {
        return RenderType.eyes(texture);
    }*/

    @Override
    public void render(final SunBeamProjectile animatable, final float yaw, final float partialTick, final PoseStack poseStack, final MultiBufferSource bufferSource, final int packedLight) {
        super.render(animatable, yaw, partialTick, poseStack, bufferSource, packedLight);
    }
}
