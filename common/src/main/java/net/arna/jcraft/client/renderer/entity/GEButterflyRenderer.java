package net.arna.jcraft.client.renderer.entity;

import net.arna.jcraft.client.model.entity.GEButterflyModel;
import net.arna.jcraft.common.entity.GEButterflyEntity;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.DynamicGeoEntityRenderer;

import java.util.Objects;

public class GEButterflyRenderer extends DynamicGeoEntityRenderer<GEButterflyEntity> {
    protected ItemStack mainHandItem;

    @Override
    public RenderLayer getRenderType(GEButterflyEntity animatable, Identifier texture, @Nullable VertexConsumerProvider bufferSource, float partialTick) {
        return RenderLayer.getEntityTranslucent(texture);
    }

    public GEButterflyRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new GEButterflyModel());
    }

    @Override
    public void renderEarly(GEButterflyEntity animatable, MatrixStack poseStack, float partialTick, VertexConsumerProvider bufferSource, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float partialTicks) {
        super.renderEarly(animatable, poseStack, partialTick, bufferSource, buffer, packedLight, packedOverlay, red, green, blue, partialTicks);
        this.mainHandItem = animatable.getEquippedStack(EquipmentSlot.MAINHAND);
    }

    @Override
    protected ItemStack getHeldItemForBone(String boneName, GEButterflyEntity currentEntity) {
        return Objects.equals(boneName, "base") ? mainHandItem : null;
    }

    @Override
    protected void preRenderItem(MatrixStack stack, ItemStack item, String boneName, GEButterflyEntity currentEntity, IBone bone) {
        if (item != this.mainHandItem) return;

        stack.scale(0.33f, 0.33f, 0.33f);
        stack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(-90f));
    }
}
