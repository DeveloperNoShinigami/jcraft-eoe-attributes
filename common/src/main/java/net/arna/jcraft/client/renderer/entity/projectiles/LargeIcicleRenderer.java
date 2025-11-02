package net.arna.jcraft.client.renderer.entity.projectiles;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import lombok.NonNull;
import mod.azure.azurelib.render.AzRendererPipelineContext;
import mod.azure.azurelib.util.client.RenderUtils;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.client.renderer.entity.AbstractEntityRenderer;
import net.arna.jcraft.common.entity.projectile.LargeIcicleProjectile;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;

import java.util.UUID;
import java.util.function.Function;

/**
 * The {@link AbstractEntityRenderer} for {@link LargeIcicleProjectile}.
 */
@Environment(EnvType.CLIENT)
public class LargeIcicleRenderer extends ProjectileRenderer<LargeIcicleProjectile> {

    public static final String ID = "large_icicle";

    public LargeIcicleRenderer(final @NonNull EntityRendererProvider.Context pc) {
        super(pc, () -> new EntityAnimator<>(ID), b -> b
                .setRenderType(RenderType.entityTranslucent(JCraft.id(TEXTURE_STR_TEMPLATE.formatted(ID))))
                .setRenderEntry(preRenderEntry()),
                ID);
    }

    protected static Function<AzRendererPipelineContext<UUID, LargeIcicleProjectile>, AzRendererPipelineContext<UUID, LargeIcicleProjectile>> preRenderEntry() {
        return pc -> {
            final LargeIcicleProjectile animatable = pc.animatable();
            final PoseStack poseStack = pc.poseStack();
            //poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(pc.partialTick(), animatable.yRotO, animatable.getYRot()) + 90));
            //poseStack.mulPose(Axis.ZN.rotationDegrees(Mth.lerp(pc.partialTick(), animatable.xRotO, animatable.getXRot())));
            RenderUtils.faceRotation(poseStack, animatable, pc.partialTick());
            final float scale = animatable.getScale();
            poseStack.scale(scale, scale, scale);
            return pc;
        };
    }

}
