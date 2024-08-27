package net.arna.jcraft.client.renderer.entity.projectiles;

import com.mojang.blaze3d.vertex.PoseStack;
import net.arna.jcraft.client.model.entity.BlockProjectileModel;
import net.arna.jcraft.common.entity.projectile.BlockProjectile;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;


public class BlockProjectileRenderer extends GeoProjectileRenderer<BlockProjectile> {
    private final ItemRenderer itemRenderer;

    public BlockProjectileRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new BlockProjectileModel());
        this.itemRenderer = ctx.getItemRenderer();
    }

    @Override
    public boolean shouldShowName(BlockProjectile animatable) {
        return false;
    }

    @Override
    public RenderType getRenderType(BlockProjectile animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        return RenderType.eyes(texture);
    }

    @Override
    public void render(BlockProjectile animatable, float yaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();
        //poseStack.multiply(Quaternion.fromEulerXyz(3.1415f, 3.1415f, 0));
        itemRenderer.renderStatic(
                animatable,
                animatable.getMainHandItem(),
                ItemDisplayContext.HEAD,
                false, poseStack, bufferSource, null, packedLight,
                LivingEntityRenderer.getOverlayCoords(animatable, 0),
                animatable.getId());
        super.render(animatable, yaw, partialTick, poseStack, bufferSource, packedLight);
        poseStack.popPose();
    }
}
