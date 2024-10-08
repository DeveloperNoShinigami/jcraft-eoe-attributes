package net.arna.jcraft.client.renderer.entity.projectiles;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import mod.azure.azurelib.cache.object.BakedGeoModel;
import mod.azure.azurelib.renderer.GeoEntityRenderer;
import net.arna.jcraft.client.model.entity.LargeIcicleModel;
import net.arna.jcraft.common.entity.projectile.LargeIcicleProjectile;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

/**
 * The {@link GeoEntityRenderer} for {@link LargeIcicleProjectile}.
 * @see LargeIcicleModel
 */
public class LargeIcicleRenderer extends GeoEntityRenderer<LargeIcicleProjectile> {
    public LargeIcicleRenderer(final EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new LargeIcicleModel());
    }

    @Override
    public boolean shouldShowName(final LargeIcicleProjectile animatable) {
        return false;
    }

    @Override
    public RenderType getRenderType(final LargeIcicleProjectile animatable, final ResourceLocation texture, final @Nullable MultiBufferSource bufferSource, final float partialTick) {
        return RenderType.entityTranslucent(texture);
    }

    @Override
    public void preRender(final PoseStack poseStack, final LargeIcicleProjectile animatable, final BakedGeoModel model, final MultiBufferSource bufferSource, final VertexConsumer buffer, final boolean isReRender, final float partialTick, final int packedLight, final int packedOverlay, final float red, final float green, final float blue, final float alpha) {
        poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTick, animatable.yRotO, animatable.getYRot()) + 90));
        poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTick, animatable.xRotO, animatable.getXRot())));
        float scale = animatable.getScale();
        poseStack.scale(scale, scale, scale);
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
