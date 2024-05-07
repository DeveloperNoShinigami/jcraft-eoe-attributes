package net.arna.jcraft.client.renderer.entity.stands;

import net.arna.jcraft.client.model.entity.PurpleHazeModel;
import net.arna.jcraft.common.entity.stand.AbstractPurpleHazeEntity;
import net.arna.jcraft.common.entity.stand.D4CEntity;
import net.minecraft.client.render.RenderLayer;
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
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.layer.BlockAndItemGeoLayer;

public class PurpleHazeRenderer extends StandEntityRenderer<AbstractPurpleHazeEntity<?, ?>> {
    public PurpleHazeRenderer(EntityRendererFactory.Context context) {
        super(context, new PurpleHazeModel(false));

        addRenderLayer(new BlockAndItemGeoLayer<>(this) {

            @javax.annotation.Nullable
            @Override
            protected ItemStack getStackForBone(GeoBone bone, AbstractPurpleHazeEntity animatable) {
                // Retrieve the items in the entity's hands for the relevant bone
                return switch (bone.getName()) {
                    case LEFT_HAND -> animatable.isLeftHanded() ?
                            PurpleHazeRenderer.this.mainHandItem : PurpleHazeRenderer.this.offHandItem;
                    case RIGHT_HAND -> animatable.isLeftHanded() ?
                            PurpleHazeRenderer.this.offHandItem : PurpleHazeRenderer.this.mainHandItem;
                    default -> null;
                };
            }

            @Override
            protected ModelTransformationMode getTransformTypeForStack(GeoBone bone, ItemStack stack, AbstractPurpleHazeEntity animatable) {
                // Apply the camera transform for the given hand
                return switch (bone.getName()) {
                    case LEFT_HAND, RIGHT_HAND -> ModelTransformationMode.THIRD_PERSON_RIGHT_HAND;
                    default -> ModelTransformationMode.NONE;
                };
            }

            // Do some quick render modifications depending on what the item is
            @Override
            protected void renderStackForBone(MatrixStack poseStack, GeoBone bone, ItemStack stack, AbstractPurpleHazeEntity animatable,
                                              VertexConsumerProvider bufferSource, float partialTick, int packedLight, int packedOverlay) {

                poseStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(bone.getRotX() - 90f));
                poseStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(bone.getRotY() - 90f));
                poseStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(bone.getRotZ()));

                if (stack == PurpleHazeRenderer.this.mainHandItem) {
                    poseStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90f));

                    if (stack.getItem() instanceof ShieldItem)
                        poseStack.translate(0, 0.125, -0.25);
                }
                else if (stack == PurpleHazeRenderer.this.offHandItem) {
                    poseStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90f));

                    if (stack.getItem() instanceof ShieldItem) {
                        poseStack.translate(0, 0.125, 0.25);
                        poseStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
                    }
                }

                super.renderStackForBone(poseStack, bone, stack, animatable, bufferSource, partialTick, packedLight, packedOverlay);
            }
        });
    }

    @Override
    public RenderLayer getRenderType(AbstractPurpleHazeEntity<?, ?> animatable, Identifier texture, @Nullable VertexConsumerProvider bufferSource, float partialTick) {
        return StandEntityRenderer.renderTypeOf(animatable, texture);
    }

    @Override
    public void actuallyRender(MatrixStack poseStack, AbstractPurpleHazeEntity<?, ?> animatable, BakedGeoModel model, RenderLayer renderType, VertexConsumerProvider bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        float a = StandEntityRenderer.getAlpha(animatable, partialTick);
        super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, a);
    }
}
