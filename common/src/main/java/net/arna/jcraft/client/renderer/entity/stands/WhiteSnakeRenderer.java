package net.arna.jcraft.client.renderer.entity.stands;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import lombok.NonNull;
import mod.azure.azurelib.model.AzBone;
import mod.azure.azurelib.render.AzRendererPipelineContext;
import mod.azure.azurelib.render.layer.AzBlockAndItemLayer;
import net.arna.jcraft.api.registry.JStandTypeRegistry;
import net.arna.jcraft.common.entity.stand.WhiteSnakeEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;

import java.util.UUID;

/**
 * The {@link StandEntityRenderer} for {@link WhiteSnakeEntity}.
 */
@Environment(EnvType.CLIENT)
public class WhiteSnakeRenderer extends StandEntityRenderer<WhiteSnakeEntity> {

    public WhiteSnakeRenderer(final @NonNull EntityRendererProvider.Context context) {
        super(context, b -> b
                .addRenderLayer(new WhiteSnakeRenderLayer()),
                JStandTypeRegistry.WHITE_SNAKE.get(), -0.10f, -0.10f);
    }

    protected static class WhiteSnakeRenderLayer extends AzBlockAndItemLayer<UUID, WhiteSnakeEntity> {
        protected ItemStack mainHandItem;
        protected ItemStack offHandItem;

        @Override
        public void preRender(final AzRendererPipelineContext<UUID, WhiteSnakeEntity> context) {
            mainHandItem = context.animatable().getMainHandItem();
            offHandItem = context.animatable().getOffhandItem();
        }

        @Override
        public ItemStack itemStackForBone(final AzBone bone, final WhiteSnakeEntity animatable) {
            // Retrieve the items in the entity's hands for the relevant bone
            return switch (bone.getName()) {
                case LEFT_HAND -> animatable.isLeftHanded() ? mainHandItem : offHandItem;
                case RIGHT_HAND -> animatable.isLeftHanded() ? offHandItem : mainHandItem;
                default -> null;
            };
        }

        @Override
        protected ItemDisplayContext getTransformTypeForStack(final AzBone bone, final ItemStack stack, final WhiteSnakeEntity animatable) {
            // Apply the camera transform for the given hand
            return switch (bone.getName()) {
                case LEFT_HAND -> ItemDisplayContext.THIRD_PERSON_LEFT_HAND;
                case RIGHT_HAND -> ItemDisplayContext.THIRD_PERSON_RIGHT_HAND;
                default -> ItemDisplayContext.NONE;
            };
        }

        @Override
        protected void renderItemForBone(final AzRendererPipelineContext<UUID, WhiteSnakeEntity> context, final AzBone bone, final ItemStack itemStack, final WhiteSnakeEntity animatable) {
            final PoseStack poseStack = context.poseStack();
            poseStack.mulPose(Axis.XP.rotationDegrees(bone.getRotX() - 90f));
            poseStack.mulPose(Axis.ZP.rotationDegrees(bone.getRotZ() - 90f));
            if (itemStack == mainHandItem) {
                poseStack.mulPose(Axis.XP.rotationDegrees(-90f));
                if (itemStack.getItem() instanceof ShieldItem) {
                    poseStack.translate(0, 0.125, -0.25);
                }
            } else if (itemStack == offHandItem) {
                poseStack.mulPose(Axis.XP.rotationDegrees(-90f));
                if (itemStack.getItem() instanceof ShieldItem) {
                    poseStack.translate(0, 0.125, 0.25);
                    poseStack.mulPose(Axis.YP.rotationDegrees(180));
                }
            }
            super.renderItemForBone(context, bone, itemStack, animatable);
        }
    }

}
