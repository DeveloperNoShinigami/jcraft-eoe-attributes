package net.arna.jcraft.client.renderer.entity.projectiles;

import com.mojang.blaze3d.vertex.PoseStack;
import net.arna.jcraft.client.model.entity.LifeDetectorModel;
import net.arna.jcraft.common.entity.projectile.LifeDetectorEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import org.joml.Quaternionf;

public class LifeDetectorRenderer extends GeoProjectileRenderer<LifeDetectorEntity> {
    public LifeDetectorRenderer(final EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new LifeDetectorModel());
    }

    @Override
    protected int getBlockLightLevel(final LifeDetectorEntity entity, final BlockPos pos) {
        return 15;
    }

    @Override
    public RenderType getRenderType(final LifeDetectorEntity animatable, final ResourceLocation texture, final MultiBufferSource bufferSource, final float partialTick) {
        return RenderType.eyes(texture);
    }

    @Override
    public void render(final LifeDetectorEntity animatable, final float yaw, final float partialTick, final PoseStack poseStack, final MultiBufferSource bufferSource, final int packedLight) {
        poseStack.pushPose();
        poseStack.mulPose(new Quaternionf().rotateXYZ(3.1415f, 3.1415f, 0)); // Why is this necessary???
        super.render(animatable, yaw, partialTick, poseStack, bufferSource, packedLight);
        poseStack.popPose();
    }
}
