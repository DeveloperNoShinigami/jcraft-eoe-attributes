package net.arna.jcraft.client.renderer.entity.projectiles;

import lombok.NonNull;
import mod.azure.azurelib.animation.AzAnimator;
import mod.azure.azurelib.render.entity.AzEntityRendererConfig;
import net.arna.jcraft.client.renderer.entity.AbstractEntityRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

import java.util.UUID;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

@Environment(EnvType.CLIENT)
public class ProjectileRenderer<T extends Entity> extends AbstractEntityRenderer<T> {

    public ProjectileRenderer(final @NonNull EntityRendererProvider.Context context, final @NonNull String id) {
        this(context, () -> new EntityAnimator<>(id), id);
    }

    public ProjectileRenderer(final @NonNull EntityRendererProvider.Context context, final @NonNull Supplier<AzAnimator<UUID, T>> azAnimatorSupplier, final @NonNull UnaryOperator<AzEntityRendererConfig.Builder<T>> additionalConfigs, final @NonNull String id) {
        super(context, azAnimatorSupplier, additionalConfigs, id);
    }

    public ProjectileRenderer(final @NonNull AzEntityRendererConfig<T> config, final @NonNull EntityRendererProvider.Context context, final @NonNull ResourceLocation model, final @NonNull ResourceLocation texture, final @NonNull ResourceLocation animation) {
        super(config, context, model, texture);
    }

    public ProjectileRenderer(final @NonNull AzEntityRendererConfig<T> config, final @NonNull EntityRendererProvider.Context context, final @NonNull String id) {
        super(config, context, id);
    }

    public ProjectileRenderer(final @NonNull EntityRendererProvider.Context context, final @NonNull Supplier<AzAnimator<UUID, T>> azAnimatorSupplier, final @NonNull ResourceLocation model, final @NonNull ResourceLocation texture, final @NonNull ResourceLocation animation) {
        super(context, azAnimatorSupplier, model, texture);
    }

    public ProjectileRenderer(final @NonNull EntityRendererProvider.Context context, final @NonNull Supplier<AzAnimator<UUID, T>> azAnimatorSupplier, final @NonNull String id) {
        super(context, azAnimatorSupplier, id);
    }

    @Override
    public boolean shouldShowName(final @NonNull T animatable) {
        return false;
    }

    /*@Override
    public void preRender(final PoseStack poseStack, final T animatable, final BakedGeoModel model, final MultiBufferSource bufferSource, final VertexConsumer buffer, final boolean isReRender, final float partialTick, final int packedLight, final int packedOverlay, final float red, final float green, final float blue, final float alpha) {
        poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTick, animatable.yRotO, animatable.getYRot()) + 90));
        poseStack.mulPose(Axis.ZN.rotationDegrees(Mth.lerp(partialTick, animatable.xRotO, animatable.getXRot())));
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }*/
}
