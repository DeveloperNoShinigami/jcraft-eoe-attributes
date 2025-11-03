package net.arna.jcraft.client.renderer.entity;

import mod.azure.azurelib.render.AzLayerRenderer;
import mod.azure.azurelib.render.AzRendererPipelineContext;
import mod.azure.azurelib.render.entity.AzEntityModelRenderer;
import mod.azure.azurelib.render.entity.AzEntityRendererPipeline;
import mod.azure.azurelib.util.client.RenderUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.entity.Entity;

import java.util.UUID;

@Environment(EnvType.CLIENT)
public class ProjectileModelRenderer<T extends Entity> extends AzEntityModelRenderer<T> {
    public ProjectileModelRenderer(AzEntityRendererPipeline<T> entityRendererPipeline, AzLayerRenderer<UUID, T> layerRenderer) {
        super(entityRendererPipeline, layerRenderer);
    }

    @Override
    public void render(final AzRendererPipelineContext<UUID, T> pc, final boolean isReRender) {
        var animatable = pc.animatable();
        var partialTick = pc.partialTick();
        var poseStack = pc.poseStack();

        poseStack.pushPose();

        RenderUtils.faceRotation(poseStack, animatable, partialTick);

        // poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(pc.partialTick(), animatable.yRotO, animatable.getYRot()) + 90));
        // poseStack.mulPose(Axis.ZN.rotationDegrees(Mth.lerp(pc.partialTick(), animatable.xRotO, animatable.getXRot())));

        if (!isReRender) {
            var animator = entityRendererPipeline.getRenderer().getAnimator();

            if (animator != null) {
                handleAnimation(animator, animatable, pc.partialTick());
            }
        }

        entityRendererPipeline.modelRenderTranslations.set(poseStack.last().pose());

        if (pc.vertexConsumer() != null) {
            var model = pc.bakedModel();

            pc.rendererPipeline().updateAnimatedTextureFrame(animatable);

            for (var bone : model.getTopLevelBones()) {
                renderRecursively(pc, bone, isReRender);
            }

            var config = pc.rendererPipeline().config();
            config.renderEntry(pc);
        }

        poseStack.popPose();
    }
}
