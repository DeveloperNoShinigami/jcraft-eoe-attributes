package net.arna.jcraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.FrogRenderer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

@Environment(EnvType.CLIENT)
public class GEFrogRenderer extends FrogRenderer {
    private final ItemInHandRenderer heldItemRenderer;

    public GEFrogRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.heldItemRenderer = context.getItemInHandRenderer();
    }

    @Override
    public void render(Frog frogEntity, float f, float g, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i) {
        matrixStack.pushPose();
        matrixStack.translate(0.0, 0.4, 0);
        matrixStack.mulPose(Axis.YN.rotationDegrees(frogEntity.getYRot()));
        ItemStack itemStack = frogEntity.getItemBySlot(EquipmentSlot.MAINHAND);
        this.heldItemRenderer.renderItem(frogEntity, itemStack, ItemDisplayContext.GROUND, false, matrixStack, vertexConsumerProvider, i);
        matrixStack.popPose();
        super.render(frogEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }
}
