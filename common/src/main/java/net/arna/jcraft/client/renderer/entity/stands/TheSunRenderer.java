package net.arna.jcraft.client.renderer.entity.stands;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import lombok.NonNull;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.api.registry.JStandTypeRegistry;
import net.arna.jcraft.client.renderer.entity.layer.SunGlowLayer;
import net.arna.jcraft.common.entity.stand.TheSunEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

/**
 * The {@link StandEntityRenderer} for {@link TheSunEntity}.
 */
@Environment(EnvType.CLIENT)
public class TheSunRenderer extends StandEntityRenderer<TheSunEntity> {

    public TheSunRenderer(final @NonNull EntityRendererProvider.Context context) {
        super(context, b -> b
                .addRenderLayer(new SunGlowLayer())
                //TODO: translucent layer that isn't layered over and has no shading
                .setRenderType(RenderType.dragonExplosionAlpha(JCraft.id(TEXTURE_STR_TEMPLATE.formatted(JStandTypeRegistry.THE_SUN.get().getId().getPath())))),
                JStandTypeRegistry.THE_SUN.get());
    }

    @Override
    protected int getBlockLightLevel(final @NonNull TheSunEntity entity, final @NonNull BlockPos pos) {
        return 15;
    }

    @Override
    protected int getSkyLightLevel(final @NonNull TheSunEntity entity, final @NonNull BlockPos pos) {
        return 15;
    }

    /*@Override
    public void preRender(final PoseStack poseStack, final TheSunEntity animatable, final BakedGeoModel model, final MultiBufferSource bufferSource, final VertexConsumer buffer, final boolean isReRender, final float partialTick, final int packedLight, final int packedOverlay, final float red, final float green, final float blue, final float alpha) {
        if (!isReRender) {
            final float scale = animatable.getScale(partialTick);
            poseStack.scale(scale, scale, scale);
        }

        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }*/
}
