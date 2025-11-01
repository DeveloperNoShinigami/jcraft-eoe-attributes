package net.arna.jcraft.client.renderer.entity;

import mod.azure.azurelib.render.AzLayerRenderer;
import mod.azure.azurelib.render.AzRendererPipeline;
import mod.azure.azurelib.render.AzRendererPipelineContext;
import mod.azure.azurelib.render.entity.AzEntityModelRenderer;
import mod.azure.azurelib.render.entity.AzEntityRendererPipeline;
import net.arna.jcraft.api.stand.StandEntity;
import net.arna.jcraft.mixin.client.azurelib.AzRendererPipelineInvoker;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;

import java.util.UUID;

public class StandEntityModelRenderer<T extends StandEntity<?, ?>> extends AzEntityModelRenderer<T> {
    public StandEntityModelRenderer(final AzEntityRendererPipeline<T> entityRendererPipeline, final AzLayerRenderer<UUID, T> layerRenderer) {
        super(entityRendererPipeline, layerRenderer);
    }

    public StandEntityModelRenderer(final AzRendererPipeline<UUID,T> entityRendererPipeline, final AzLayerRenderer<UUID, T> layerRenderer) {
        super((AzEntityRendererPipeline<T>)entityRendererPipeline, layerRenderer);
    }

    @Override
    public void render(final AzRendererPipelineContext<UUID, T> context, final boolean isReRender) {
        final var animatable = context.animatable();
        final var partialTick = context.partialTick();
        final var poseStack = context.poseStack();

        poseStack.pushPose();
        final float lerpBodyRot = getStandLerpRot(animatable, partialTick);

        if (animatable.getPose() == Pose.SLEEPING) {
            final Direction bedDirection = animatable.getBedOrientation();

            if (bedDirection != null) {
                float eyePosOffset = animatable.getEyeHeight(Pose.STANDING) - 0.1F;

                poseStack.translate(
                        -bedDirection.getStepX() * eyePosOffset,
                        0,
                        -bedDirection.getStepZ() * eyePosOffset
                );
            }
        }

        final float nativeScale = animatable.getScale();
        final float ageInTicks = animatable.tickCount + partialTick;

        poseStack.scale(nativeScale, nativeScale, nativeScale);
        applyRotations(animatable, poseStack, ageInTicks, lerpBodyRot, partialTick, nativeScale);

        if (!isReRender) {
            final var animator = entityRendererPipeline.getRenderer().getAnimator();

            if (animator != null) {
                handleAnimation(animator, animatable, context.partialTick());
            }
        }

        entityRendererPipeline.modelRenderTranslations.set(poseStack.last().pose());

        if (context.vertexConsumer() != null) { // actually render
            ((AzRendererPipelineInvoker)context.rendererPipeline()).updateAnimatedTextureFrame(animatable);

            for (var bone : context.bakedModel().getTopLevelBones()) {
                renderRecursively(context, bone, isReRender);
            }

            var config = context.rendererPipeline().config();
            config.renderEntry(context);
        }

        poseStack.popPose();
    }

    private static <T extends StandEntity<?, ?>> float getStandLerpRot(final T animatable, final float partialTick) {
        final LivingEntity user = animatable.getUser();
        final boolean hasUser = user != null;
        if (hasUser) {
            return Mth.rotLerp(
                    partialTick,
                    user.yHeadRotO,
                    user.yHeadRot
            );
        }
        return Mth.rotLerp(
                partialTick,
                animatable.yBodyRotO,
                animatable.yBodyRot
        );
    }
}
