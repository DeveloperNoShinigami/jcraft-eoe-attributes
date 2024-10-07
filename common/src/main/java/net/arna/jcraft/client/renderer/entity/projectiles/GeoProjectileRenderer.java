package net.arna.jcraft.client.renderer.entity.projectiles;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import mod.azure.azurelib.cache.object.BakedGeoModel;
import mod.azure.azurelib.core.animatable.GeoAnimatable;
import mod.azure.azurelib.model.GeoModel;
import mod.azure.azurelib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class GeoProjectileRenderer<T extends Entity& GeoAnimatable> extends GeoEntityRenderer<T> {
    public GeoProjectileRenderer(final EntityRendererProvider.Context renderManager, final GeoModel<T> model) {
        super(renderManager, model);
    }

    @Override
    public boolean shouldShowName(final T animatable) {
        return false;
    }

    @Override
    public void preRender(final PoseStack poseStack, final T animatable, final BakedGeoModel model, final MultiBufferSource bufferSource, final VertexConsumer buffer, final boolean isReRender, final float partialTick, final int packedLight, final int packedOverlay, final float red, final float green, final float blue, final float alpha) {
        poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTick, animatable.yRotO, animatable.getYRot()) + 90));
        poseStack.mulPose(Axis.ZN.rotationDegrees(Mth.lerp(partialTick, animatable.xRotO, animatable.getXRot())));
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
