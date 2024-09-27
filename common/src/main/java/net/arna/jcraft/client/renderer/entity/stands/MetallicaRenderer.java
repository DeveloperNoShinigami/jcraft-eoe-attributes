package net.arna.jcraft.client.renderer.entity.stands;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import mod.azure.azurelib.cache.object.BakedGeoModel;
import mod.azure.azurelib.util.RenderUtils;
import net.arna.jcraft.client.model.entity.stand.MetallicaModel;
import net.arna.jcraft.common.entity.stand.MetallicaEntity;
import net.arna.jcraft.common.gravity.api.GravityChangerAPI;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;

public class MetallicaRenderer extends StandEntityRenderer<MetallicaEntity> {
    private final ItemInHandRenderer heldItemRenderer;
    public MetallicaRenderer(EntityRendererProvider.Context context) {
        super(context, new MetallicaModel());
        this.heldItemRenderer = context.getItemInHandRenderer();
    }

    private static final ItemStack IRON_NUGGET = Items.IRON_NUGGET.getDefaultInstance();
    @Override
    public void actuallyRender(PoseStack matrixStack, MetallicaEntity animatable, BakedGeoModel model, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        float a = StandEntityRenderer.getAlpha(animatable, partialTick);
        super.actuallyRender(matrixStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, a);

        if (!animatable.hasUser()) return;
        if (animatable.getState() == MetallicaEntity.State.HARVEST) {
            BlockPos siphonPos = animatable.getSiphonPos();
            if (siphonPos == MetallicaEntity.NO_SIPHON) return;
            LivingEntity user = animatable.getUserOrThrow();
            Vec3 eyeOffset = GravityChangerAPI.getEyeOffset(user).scale(0.75);
            Vec3 midPos = new Vec3(
                    Mth.lerp(partialTick, user.xOld, user.getX()),
                    Mth.lerp(partialTick, user.yOld, user.getY()),
                    Mth.lerp(partialTick, user.zOld, user.getZ())
            ).add(GravityChangerAPI.getEyeOffset(user));
            Vec3 toUser = siphonPos.getCenter().subtract(midPos).scale(0.2);
            Vec3 standToUser = user.position().subtract(animatable.position()).add(eyeOffset);
            double time = (RenderUtils.getCurrentTick() / 5.0) % 1.0;
            matrixStack.pushPose();
            matrixStack.translate(standToUser.x, standToUser.y, standToUser.z);
            matrixStack.translate(-toUser.x * time, -toUser.y * time, -toUser.z * time);
            for (int i = 0; i < 5; i++) {
                matrixStack.translate(toUser.x, toUser.y, toUser.z);
                matrixStack.pushPose();
                matrixStack.mulPose(Axis.XP.rotationDegrees(Mth.lerp(partialTick, user.xRotO, user.getXRot())));
                matrixStack.mulPose(Axis.YN.rotationDegrees(Mth.lerp(partialTick, user.yRotO, user.getYRot())));
                this.heldItemRenderer.renderItem(animatable, IRON_NUGGET, ItemDisplayContext.GROUND, false, matrixStack, bufferSource, i);
                matrixStack.popPose();
            }
            matrixStack.popPose();
        }
    }
}
