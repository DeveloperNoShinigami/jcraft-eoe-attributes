package net.arna.jcraft.client.renderer.entity;

import mod.azure.azurelib.cache.object.BakedGeoModel;
import mod.azure.azurelib.cache.object.GeoBone;
import mod.azure.azurelib.renderer.DynamicGeoEntityRenderer;
import mod.azure.azurelib.renderer.layer.BlockAndItemGeoLayer;
import net.arna.jcraft.client.model.entity.GESnakeModel;
import net.arna.jcraft.common.entity.GESnakeEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import javax.annotation.Nullable;

public class GESnakeRenderer extends DynamicGeoEntityRenderer<GESnakeEntity> {
    protected ItemStack mainHandItem;

    public GESnakeRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new GESnakeModel());
        addRenderLayer(new BlockAndItemGeoLayer<>(this) {

            @Nullable
            @Override
            protected ItemStack getStackForBone(GeoBone bone, GESnakeEntity animatable) {
                // Retrieve the items in the entity's hands for the relevant bone
                return switch (bone.getName()) {
                    case "body" -> GESnakeRenderer.this.mainHandItem;
                    default -> null;
                };
            }

            @Override
            protected ItemDisplayContext getTransformTypeForStack(GeoBone bone, ItemStack stack, GESnakeEntity animatable) {
                // Apply the camera transform for the given hand
                return ItemDisplayContext.NONE;
            }

            // Do some quick render modifications depending on what the item is
            @Override
            protected void renderStackForBone(PoseStack poseStack, GeoBone bone, ItemStack stack, GESnakeEntity animatable,
                                              MultiBufferSource bufferSource, float partialTick, int packedLight, int packedOverlay) {

                if (stack == GESnakeRenderer.this.mainHandItem) {
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
    public void preRender(PoseStack poseStack, GESnakeEntity animatable, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
        this.mainHandItem = animatable.getItemBySlot(EquipmentSlot.MAINHAND);
    }
}
