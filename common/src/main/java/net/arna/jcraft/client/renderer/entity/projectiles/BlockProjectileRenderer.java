package net.arna.jcraft.client.renderer.entity.projectiles;

import com.mojang.blaze3d.vertex.PoseStack;
import lombok.NonNull;
import net.arna.jcraft.client.model.JProjectileModel;
import net.arna.jcraft.common.entity.projectile.BlockProjectile;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;

/**
 * The {@link ProjectileRenderer} for {@link BlockProjectile}.
 */
@Environment(EnvType.CLIENT)
public class BlockProjectileRenderer extends ProjectileRenderer<BlockProjectile> {
    private final ItemRenderer itemRenderer;

    public BlockProjectileRenderer(final @NonNull EntityRendererProvider.Context context) {
        super(context, "block");
        this.itemRenderer = context.getItemRenderer();
    }

    /*
    @Override
    public RenderType getRenderType(final BlockProjectile animatable, final ResourceLocation texture, final MultiBufferSource bufferSource, final float partialTick) {
        return RenderType.eyes(texture);
    }*/

    @Override
    public void render(final BlockProjectile animatable, final float yaw, final float partialTick, final PoseStack poseStack, final MultiBufferSource bufferSource, final int packedLight) {
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
