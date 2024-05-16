package net.arna.jcraft.client.renderer.entity.projectiles;

import com.mojang.blaze3d.vertex.PoseStack;
import net.arna.jcraft.client.model.entity.PHCapsuleModel;
import net.arna.jcraft.common.entity.projectile.PHCapsuleProjectile;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class PHCapsuleRenderer extends GeoProjectileRenderer<PHCapsuleProjectile> {

    public PHCapsuleRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new PHCapsuleModel());
    }

    @Override
    public void render(PHCapsuleProjectile animatable, float yaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        super.render(animatable, yaw, partialTick, poseStack, bufferSource, packedLight);
    }
}
