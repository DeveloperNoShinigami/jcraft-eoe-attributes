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
    public MeteorRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new MeteorModel());
    }

    @Override
    protected int getBlockLightLevel(MeteorProjectile entity, BlockPos pos) {
        return 15;
    }

    @Override
    public RenderType getRenderType(MeteorProjectile animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        return RenderType.eyes(texture);
    }

    @Override
    public void render(MeteorProjectile animatable, float yaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        super.render(animatable, yaw, partialTick, poseStack, bufferSource, packedLight);
    }
}
