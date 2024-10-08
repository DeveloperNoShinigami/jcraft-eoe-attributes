package net.arna.jcraft.client.renderer.entity.stands;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import mod.azure.azurelib.cache.object.BakedGeoModel;
import mod.azure.azurelib.cache.object.GeoBone;
import mod.azure.azurelib.renderer.layer.BlockAndItemGeoLayer;
import net.arna.jcraft.client.model.entity.stand.StandEntityModel;
import net.arna.jcraft.common.entity.stand.D4CEntity;
import net.arna.jcraft.common.entity.stand.StandType;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;

import javax.annotation.Nullable;

/**
 * The {@link StandEntityRenderer} for {@link D4CEntity}.
 */
public class D4CRenderer extends StandEntityRenderer<D4CEntity> {
    public D4CRenderer(final EntityRendererProvider.Context context) {
        super(context, new StandEntityModel<>(StandType.D4C));

        addRenderLayer(new BlockAndItemGeoLayer<>(this) {

            @Nullable
            @Override
            protected ItemStack getStackForBone(final GeoBone bone, final D4CEntity animatable) {
                // Retrieve the items in the entity's hands for the relevant bone
                return switch (bone.getName()) {
                    case LEFT_HAND -> animatable.isLeftHanded() ?
                            D4CRenderer.this.mainHandItem : D4CRenderer.this.offHandItem;
                    case RIGHT_HAND -> animatable.isLeftHanded() ?
                            D4CRenderer.this.offHandItem : D4CRenderer.this.mainHandItem;
                    default -> null;
                };
            }

            @Override
            protected ItemDisplayContext getTransformTypeForStack(final GeoBone bone, final ItemStack stack, final D4CEntity animatable) {
                // Apply the camera transform for the given hand
                return switch (bone.getName()) {
                    case LEFT_HAND, RIGHT_HAND -> ItemDisplayContext.THIRD_PERSON_RIGHT_HAND;
                    default -> ItemDisplayContext.NONE;
                };
            }

            // Do some quick render modifications depending on what the item is
            @Override
            protected void renderStackForBone(final PoseStack poseStack, final GeoBone bone, final ItemStack stack, final D4CEntity animatable,
                                              final MultiBufferSource bufferSource, final float partialTick, final int packedLight, final int packedOverlay) {
                /*
                float ang = -90f;
                D4CEntity.State state = animatable.getState();
                if (state == D4CEntity.State.THROW || state == D4CEntity.State.GIVE_GUN) {
                    ang += (animatable.getMoveStun() + 1f - partialTick) * 65f;
                }
                 */
                poseStack.mulPose(Axis.XP.rotationDegrees(bone.getRotX() * 57.29578f));

                if (stack == D4CRenderer.this.mainHandItem) {
                    // poseStack.mulPose(Axis.XP.rotationDegrees(-90f));

                    if (stack.getItem() instanceof ShieldItem) {
                        poseStack.translate(0, 0.125, -0.25);
                    }
                } else if (stack == D4CRenderer.this.offHandItem) {
                    // poseStack.mulPose(Axis.XP.rotationDegrees(-90f));

                    if (stack.getItem() instanceof ShieldItem) {
                        poseStack.translate(0, 0.125, 0.25);
                        poseStack.mulPose(Axis.YP.rotationDegrees(180));
                    }
                }

                super.renderStackForBone(poseStack, bone, stack, animatable, bufferSource, partialTick, packedLight, packedOverlay);
            }
        });
    }

    @Override
    public void actuallyRender(final PoseStack poseStack, final D4CEntity animatable, final BakedGeoModel model, final RenderType renderType, final MultiBufferSource bufferSource, final VertexConsumer buffer, final boolean isReRender, final float partialTick, final int packedLight, final int packedOverlay, final float red, final float green, final float blue, final float alpha) {
        final float a = StandEntityRenderer.getAlpha(animatable, partialTick);
        final float gR = 1.0f - a;

        this.mainHandItem = animatable.getMainHandItem();
        this.offHandItem = animatable.getOffhandItem();

        super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green - gR, blue, a);
    }

    /*
        /execute as @e[type=jcraft:d4c] run data merge entity @s {HandItems:[{id:"jcraft:fv_revolver", Count:1b},{id:"jcraft:fv_revolver", Count:1b}]}
     */
    protected float partialTick = 0f;
}
