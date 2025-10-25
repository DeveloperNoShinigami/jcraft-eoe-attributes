package net.arna.jcraft.client.renderer.entity;

import lombok.NonNull;
import mod.azure.azurelib.animation.AzAnimator;
import mod.azure.azurelib.animation.controller.AzAnimationController;
import mod.azure.azurelib.animation.controller.AzAnimationControllerContainer;
import mod.azure.azurelib.animation.impl.AzEntityAnimator;
import mod.azure.azurelib.render.entity.AzEntityRenderer;
import mod.azure.azurelib.render.entity.AzEntityRendererConfig;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.client.model.entity.GESnakeModel;
import net.arna.jcraft.common.entity.GESnakeEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.function.Supplier;

/**
 * The {@link AbstractEntityRenderer} for {@link GESnakeEntity}.
 */
@Environment(EnvType.CLIENT)
public class GESnakeRenderer extends AbstractEntityRenderer<GESnakeEntity> {
    
    private static final String ID = "gesnake";
    
    protected ItemStack mainHandItem;

    protected GESnakeRenderer(final @NonNull  EntityRendererProvider.Context context) {
        super(context, () -> new EntityAnimator<>(ID), ID);
    }

    /*
    public GESnakeRenderer(final EntityRendererProvider.Context renderManager) {
        super(renderManager, new GESnakeModel());
        addRenderLayer(new BlockAndItemGeoLayer<>(this) {

            @Nullable
            @Override
            protected ItemStack getStackForBone(final GeoBone bone, final GESnakeEntity animatable) {
                // Retrieve the items in the entity's hands for the relevant bone
                if ("body".equals(bone.getName())) return GESnakeRenderer.this.mainHandItem;
                return null;
            }

            @Override
            protected ItemDisplayContext getTransformTypeForStack(final GeoBone bone, final ItemStack stack, final GESnakeEntity animatable) {
                // Apply the camera transform for the given hand
                return ItemDisplayContext.NONE;
            }

            // Do some quick render modifications depending on what the item is
            @Override
            protected void renderStackForBone(final PoseStack poseStack, final GeoBone bone, final ItemStack stack, final GESnakeEntity animatable,
                                              final MultiBufferSource bufferSource, final float partialTick, final int packedLight, final int packedOverlay) {

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
    public void preRender(final PoseStack poseStack, final GESnakeEntity animatable, final BakedGeoModel model, final MultiBufferSource bufferSource, final VertexConsumer buffer, final boolean isReRender, final float partialTick, final int packedLight, final int packedOverlay, final float red, final float green, final float blue, final float alpha) {
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
        this.mainHandItem = animatable.getItemBySlot(EquipmentSlot.MAINHAND);
    }*/
}
