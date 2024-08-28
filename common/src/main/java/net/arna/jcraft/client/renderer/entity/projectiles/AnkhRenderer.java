package net.arna.jcraft.client.renderer.entity.projectiles;

import com.mojang.blaze3d.vertex.PoseStack;
import net.arna.jcraft.client.model.entity.AnkhModel;
import net.arna.jcraft.common.entity.projectile.AnkhProjectile;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class AnkhRenderer extends GeoProjectileRenderer<AnkhProjectile> {
    public AnkhRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new AnkhModel());
    }

    @Override
    protected int getBlockLightLevel(AnkhProjectile entity, BlockPos pos) {
        return 15;
    }

    @Override
    public RenderType getRenderType(AnkhProjectile animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.eyes(texture);
    }

    @Override
    public void render(AnkhProjectile animatable, float yaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        super.render(animatable, yaw, partialTick, poseStack, bufferSource, packedLight);
    }
}
