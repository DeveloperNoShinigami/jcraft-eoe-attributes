package net.arna.jcraft.client.renderer.entity.projectiles;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import lombok.NonNull;
import mod.azure.azurelib.animation.AzAnimator;
import mod.azure.azurelib.render.AzRendererPipelineContext;
import mod.azure.azurelib.render.entity.AzEntityRendererConfig;
import mod.azure.azurelib.render.entity.AzEntityRendererPipeline;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.client.renderer.entity.AbstractEntityRenderer;
import net.arna.jcraft.client.renderer.entity.ProjectileModelRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

@Environment(EnvType.CLIENT)
public class ProjectileRenderer<T extends Entity> extends AbstractEntityRenderer<T> {
    protected static final String TEXTURE_STR_TEMPLATE = "textures/entity/projectiles/%s.png";

    public ProjectileRenderer(final EntityRendererProvider.Context context, final @NonNull Supplier<AzAnimator<UUID,T>> azAnimatorSupplier, final @NonNull UnaryOperator<AzEntityRendererConfig.Builder<T>> additionalConfigs, final ResourceLocation model, final ResourceLocation texture) {
        super(context, azAnimatorSupplier, additionalConfigs.compose(b -> b
                .setModelRenderer(
                        (pc, layer) -> new ProjectileModelRenderer<>((AzEntityRendererPipeline<T>) pc, layer)
                )),
                // .setRenderEntry(preRenderEntry())),
                model, texture);
    }

    public ProjectileRenderer(final @NonNull EntityRendererProvider.Context context, final @NonNull Supplier<AzAnimator<UUID, T>> azAnimatorSupplier, final @NonNull UnaryOperator<AzEntityRendererConfig.Builder<T>> additionalConfigs, final @NonNull String id) {
        this(context, azAnimatorSupplier, additionalConfigs, JCraft.id(MODEL_STR_TEMPLATE.formatted(id)), JCraft.id(TEXTURE_STR_TEMPLATE.formatted(id)));
    }

    public ProjectileRenderer(final @NonNull EntityRendererProvider.Context context, final @NonNull Supplier<AzAnimator<UUID, T>> azAnimatorSupplier, final @NonNull ResourceLocation model, final @NonNull ResourceLocation texture) {
        this(context, azAnimatorSupplier, UnaryOperator.identity(), model, texture);
    }

    public ProjectileRenderer(final @NonNull EntityRendererProvider.Context context, final @NonNull Supplier<AzAnimator<UUID, T>> azAnimatorSupplier, final @NonNull String id) {
        this(context, azAnimatorSupplier, JCraft.id(MODEL_STR_TEMPLATE.formatted(id)), JCraft.id(TEXTURE_STR_TEMPLATE.formatted(id)));
    }

    public ProjectileRenderer(final @NonNull EntityRendererProvider.Context context, final @NonNull String id) {
        this(context, () -> new EntityAnimator<>(id), id);
    }

    protected ProjectileRenderer(final @NonNull AzEntityRendererConfig<T> config, final @NonNull EntityRendererProvider.Context context, final @NonNull ResourceLocation model, final @NonNull ResourceLocation texture) {
        super(config, context, model, texture);
    }

    protected ProjectileRenderer(final @NonNull AzEntityRendererConfig<T> config, final @NonNull EntityRendererProvider.Context context, final @NonNull String id) {
        this(config, context, JCraft.id(MODEL_STR_TEMPLATE.formatted(id)), JCraft.id(TEXTURE_STR_TEMPLATE.formatted(id)));
    }

    protected static <T extends Entity> Function<AzRendererPipelineContext<UUID, T>, AzRendererPipelineContext<UUID, T>> preRenderEntry() {
        return pc -> {
            final T animatable = pc.animatable();
            final PoseStack poseStack = pc.poseStack();
            poseStack.pushPose();
            //RenderUtils.faceRotation(poseStack, animatable, pc.partialTick());
            poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(pc.partialTick(), animatable.yRotO, animatable.getYRot()) + 90));
            poseStack.mulPose(Axis.ZN.rotationDegrees(Mth.lerp(pc.partialTick(), animatable.xRotO, animatable.getXRot())));
            poseStack.popPose();
            return pc;
        };
    }

    @Override
    public boolean shouldShowName(final @NonNull T animatable) {
        return false;
    }

}
