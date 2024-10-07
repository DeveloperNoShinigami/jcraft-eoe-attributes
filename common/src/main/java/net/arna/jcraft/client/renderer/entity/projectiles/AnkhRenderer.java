package net.arna.jcraft.client.renderer.entity.projectiles;

import com.mojang.blaze3d.vertex.PoseStack;
import net.arna.jcraft.client.model.entity.AnkhModel;
import net.arna.jcraft.common.entity.projectile.AnkhProjectile;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class AnkhRenderer extends GeoProjectileRenderer<AnkhProjectile> {
    public AnkhRenderer(final EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new AnkhModel());
    }

    @Override
    protected int getBlockLightLevel(final AnkhProjectile entity, final BlockPos pos) {
        return 15;
    }

    @Override
    public RenderType getRenderType(final AnkhProjectile animatable, final ResourceLocation texture, final @Nullable MultiBufferSource bufferSource, final float partialTick) {
        return RenderType.eyes(texture);
    }

    @Override
    public void render(final AnkhProjectile animatable, final float yaw, final float partialTick, final PoseStack poseStack, final MultiBufferSource bufferSource, final int packedLight) {
        super.render(animatable, yaw, partialTick, poseStack, bufferSource, packedLight);
    }
}
