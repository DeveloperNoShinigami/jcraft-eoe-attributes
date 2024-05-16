package net.arna.jcraft.client.renderer.entity.projectiles;

import com.mojang.blaze3d.vertex.PoseStack;
import net.arna.jcraft.client.model.entity.SunBeamModel;
import net.arna.jcraft.common.entity.projectile.SunBeamProjectile;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class SunBeamRenderer extends GeoProjectileRenderer<SunBeamProjectile> {

    public SunBeamRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new SunBeamModel());
    }

    protected int getBlockLight(SunBeamProjectile entityIn, BlockPos partialTicks) {
        return 15;
    }

    @Override
    public RenderType getRenderType(SunBeamProjectile animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        return RenderType.eyes(texture);
    }

    @Override
    public void render(SunBeamProjectile animatable, float yaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        super.render(animatable, yaw, partialTick, poseStack, bufferSource, packedLight);
    }
}
