package net.arna.jcraft.client.renderer.entity;

import net.arna.jcraft.client.model.entity.GESnakeModel;
import net.arna.jcraft.client.renderer.entity.stands.D4CRenderer;
import net.arna.jcraft.common.entity.GESnakeEntity;
import net.arna.jcraft.common.entity.stand.D4CEntity;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.DynamicGeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.BlockAndItemGeoLayer;

import javax.annotation.Nullable;
import java.util.Objects;

public class GESnakeRenderer extends DynamicGeoEntityRenderer<GESnakeEntity> {
    protected ItemStack mainHandItem;

    public GESnakeRenderer(EntityRendererFactory.Context renderManager) {
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
            protected ModelTransformationMode getTransformTypeForStack(GeoBone bone, ItemStack stack, GESnakeEntity animatable) {
                // Apply the camera transform for the given hand
                return ModelTransformationMode.NONE;
            }

            // Do some quick render modifications depending on what the item is
            @Override
            protected void renderStackForBone(MatrixStack poseStack, GeoBone bone, ItemStack stack, GESnakeEntity animatable,
                                              VertexConsumerProvider bufferSource, float partialTick, int packedLight, int packedOverlay) {

                if (stack == GESnakeRenderer.this.mainHandItem) {
                    poseStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90f));

                    if (stack.getItem() instanceof ShieldItem)
                        poseStack.translate(0, 0.125, -0.25);
                }

                super.renderStackForBone(poseStack, bone, stack, animatable, bufferSource, partialTick, packedLight, packedOverlay);
            }
        });
    }

    @Override
    public void preRender(MatrixStack poseStack, GESnakeEntity animatable, BakedGeoModel model, VertexConsumerProvider bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
        this.mainHandItem = animatable.getEquippedStack(EquipmentSlot.MAINHAND);
    }
}
