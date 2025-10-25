package net.arna.jcraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import lombok.NonNull;
import mod.azure.azurelib.animation.controller.AzAnimationController;
import mod.azure.azurelib.animation.controller.AzAnimationControllerContainer;
import mod.azure.azurelib.animation.impl.AzEntityAnimator;
import mod.azure.azurelib.render.entity.AzEntityRenderer;
import mod.azure.azurelib.render.entity.AzEntityRendererConfig;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.client.model.entity.GEButterflyModel;
import net.arna.jcraft.common.entity.GEButterflyEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The {@link AbstractEntityRenderer} for {@link GEButterflyEntity}.
 */
@Environment(EnvType.CLIENT)
public class GEButterflyRenderer extends AbstractEntityRenderer<GEButterflyEntity> {
    public static final String ID = "gebutterfly";

    protected ItemStack mainHandItem;

    protected GEButterflyRenderer(final @NonNull EntityRendererProvider.Context context) {
        super(context, () -> new AbstractEntityRenderer.EntityAnimator<>(ID), ID);
    }

    /*@Override
    public RenderType getRenderType(final GEButterflyEntity animatable, final ResourceLocation texture, final @Nullable MultiBufferSource bufferSource, final float partialTick) {
        return RenderType.entityTranslucent(texture);
    }

    public GEButterflyRenderer(final EntityRendererProvider.Context renderManager) {
        super(renderManager, new GEButterflyModel());
        addRenderLayer(new BlockAndItemGeoLayer<>(this) {

            @Nullable
            @Override
            protected ItemStack getStackForBone(final GeoBone bone, final GEButterflyEntity animatable) {
                // Retrieve the items in the entity's hands for the relevant bone
                if (bone.getName().equals("base")) return mainHandItem;
                return null;
            }

            @Override
            protected ItemDisplayContext getTransformTypeForStack(final GeoBone bone, final ItemStack stack, final GEButterflyEntity animatable) {
                // Apply the camera transform for the given hand
                return ItemDisplayContext.NONE;
            }

            // Do some quick render modifications depending on what the item is
            @Override
            protected void renderStackForBone(final PoseStack poseStack, final GeoBone bone, final ItemStack stack, final GEButterflyEntity animatable,
                                              final MultiBufferSource bufferSource, final float partialTick, final int packedLight, final int packedOverlay) {

                if (stack == GEButterflyRenderer.this.mainHandItem) {
                    poseStack.scale(0.33f, 0.33f, 0.33f);
                    poseStack.mulPose(Axis.XP.rotationDegrees(-90f));

                    if (stack.getItem() instanceof ShieldItem) {
                        poseStack.translate(0, 0.125, -0.25);
                    }
                }

                super.renderStackForBone(poseStack, bone, stack, animatable, bufferSource, partialTick, packedLight, packedOverlay);
            }
        });
    }

    @Override
    public void preRender(final PoseStack poseStack, final GEButterflyEntity animatable, final BakedGeoModel model, final MultiBufferSource bufferSource, final VertexConsumer buffer, final boolean isReRender, final float partialTick, final int packedLight, final int packedOverlay, final float red, final float green, final float blue, final float alpha) {
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
        this.mainHandItem = animatable.getItemBySlot(EquipmentSlot.MAINHAND);
    }*/
}
