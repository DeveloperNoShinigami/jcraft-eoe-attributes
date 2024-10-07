package net.arna.jcraft.client.renderer.entity.projectiles;

import com.mojang.blaze3d.vertex.PoseStack;
import net.arna.jcraft.client.model.entity.MeteorModel;
import net.arna.jcraft.common.entity.projectile.MeteorProjectile;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

public class MeteorRenderer extends GeoProjectileRenderer<MeteorProjectile> {
    public MeteorRenderer(final EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new MeteorModel());
    }

    @Override
    protected int getBlockLightLevel(final MeteorProjectile entity, final BlockPos pos) {
        return 15;
    }

    @Override
    public RenderType getRenderType(final MeteorProjectile animatable, final ResourceLocation texture, final MultiBufferSource bufferSource, final float partialTick) {
        return RenderType.eyes(texture);
    }

    @Override
    public void render(final MeteorProjectile animatable, final float yaw, final float partialTick, final PoseStack poseStack, final MultiBufferSource bufferSource, final int packedLight) {
        super.render(animatable, yaw, partialTick, poseStack, bufferSource, packedLight);
    }
}
