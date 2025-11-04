package net.arna.jcraft.client.renderer.entity.projectiles;

import com.mojang.blaze3d.vertex.PoseStack;
import lombok.NonNull;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.entity.projectile.LifeDetectorEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import org.joml.Quaternionf;

/**
 * The {@link ProjectileRenderer} for {@link LifeDetectorEntity}.
 */
@Environment(EnvType.CLIENT)
public class LifeDetectorRenderer extends ProjectileRenderer<LifeDetectorEntity> {

    public static final String ID = "detector";

    public LifeDetectorRenderer(final @NonNull EntityRendererProvider.Context context) {
        super(context, () -> new EntityAnimator<>(ID), b -> b
                .setRenderType(RenderType.eyes(JCraft.id(TEXTURE_STR_TEMPLATE.formatted(ID)))),
                ID);
    }

    @Override
    public int getBlockLightLevel(final @NonNull LifeDetectorEntity entity, final @NonNull BlockPos pos) {
        return 15;
    }

    @Override
    public void render(final @NonNull LifeDetectorEntity animatable, final float yaw, final float partialTick, final PoseStack poseStack, final @NonNull MultiBufferSource bufferSource, final int packedLight) {
        poseStack.pushPose();
        poseStack.mulPose(new Quaternionf().rotateXYZ(3.1415f, 3.1415f, 0)); // Why is this necessary???
        super.render(animatable, yaw, partialTick, poseStack, bufferSource, packedLight);
        poseStack.popPose();
    }
}
