package net.arna.jcraft.client.renderer.entity.projectiles;

import com.mojang.blaze3d.vertex.PoseStack;
import net.arna.jcraft.client.model.entity.RedBindModel;
import net.arna.jcraft.common.entity.projectile.RedBindEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

/**
 * The {@link ProjectileRenderer} for {@link RedBindEntity}.
 * @see RedBindModel
 */
public class RedBindRenderer extends ProjectileRenderer<RedBindEntity> {
    public RedBindRenderer(final EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, "red_bind");
    }

    /*@Override
    public RenderType getRenderType(final RedBindEntity animatable, final ResourceLocation texture, final MultiBufferSource bufferSource, final float partialTick) {
        return RenderType.eyes(texture);
    }*/

    @Override
    public void render(final RedBindEntity animatable, final float yaw, final float partialTick, final PoseStack poseStack, final MultiBufferSource bufferSource, final int packedLight) {
        float xz = animatable.getBoundWidth();

        poseStack.pushPose();
        poseStack.scale(xz, 1f, xz);
        super.render(animatable, yaw, partialTick, poseStack, bufferSource, packedLight);
        poseStack.popPose();
    }
}
