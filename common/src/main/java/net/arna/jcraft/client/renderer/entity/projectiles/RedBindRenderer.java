package net.arna.jcraft.client.renderer.entity.projectiles;

import com.mojang.blaze3d.vertex.PoseStack;
import lombok.NonNull;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.entity.projectile.RedBindEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * The {@link ProjectileRenderer} for {@link RedBindEntity}.
 */
public class RedBindRenderer extends ProjectileRenderer<RedBindEntity> {

    public static final String ID = "red_bind";

    public RedBindRenderer(final @NonNull EntityRendererProvider.Context context) {
        super(context, () -> new EntityAnimator<>(ID), b -> b
                .setRenderType(RenderType.eyes(JCraft.id(TEXTURE_STR_TEMPLATE.formatted(ID)))),
                ID);
    }

    @Override
    public void render(final RedBindEntity animatable, final float yaw, final float partialTick, final PoseStack poseStack, final MultiBufferSource bufferSource, final int packedLight) {
        float xz = animatable.getBoundWidth();

        poseStack.pushPose();
        poseStack.scale(xz, 1f, xz);
        super.render(animatable, yaw, partialTick, poseStack, bufferSource, packedLight);
        poseStack.popPose();
    }
}
