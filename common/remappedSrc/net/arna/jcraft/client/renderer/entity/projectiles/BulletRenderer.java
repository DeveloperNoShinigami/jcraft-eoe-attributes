package net.arna.jcraft.client.renderer.entity.projectiles;

import com.mojang.blaze3d.vertex.PoseStack;
import net.arna.jcraft.client.model.entity.BulletModel;
import net.arna.jcraft.common.entity.projectile.BulletProjectile;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoEntityRenderer;


public class BulletRenderer extends GeoProjectileRenderer<BulletProjectile> {
    public BulletRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new BulletModel()); // 3x1x1 px cuboid model
    }

    @Override
    public RenderType getRenderType(BulletProjectile animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entitySolid(texture);
    }

    @Override
    public void render(BulletProjectile animatable, float yaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();
        float scale = animatable.getCaliber() * 0.016f; // 62.5mm/px
        poseStack.scale(scale, scale, scale);
        super.render(animatable, yaw, partialTick, poseStack, bufferSource, packedLight);
        poseStack.popPose();
    }
}
