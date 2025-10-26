package net.arna.jcraft.client.renderer.entity.stands;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import lombok.NonNull;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.api.registry.JStandTypeRegistry;
import net.arna.jcraft.api.stand.StandEntity;
import net.arna.jcraft.api.stand.StandType;
import net.arna.jcraft.client.model.entity.stand.CreamModel;
import net.arna.jcraft.client.renderer.entity.layer.CreamVoidLayer;
import net.arna.jcraft.common.entity.stand.CreamEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

/**
 * The {@link StandEntityRenderer} for {@link CreamEntity}.
 */
@Environment(EnvType.CLIENT)
public class CreamRenderer extends StandEntityRenderer<CreamEntity> {
    public CreamRenderer(final @NonNull EntityRendererProvider.Context context) {
        super(context, b -> b
                .setRenderType(CreamRenderer::renderType)
                .addRenderLayer(new CreamVoidLayer()),
                JStandTypeRegistry.CREAM.get());
    }

    protected static @NotNull RenderType renderType(final @NonNull CreamEntity cream) {
        if (cream.getVoidTime() > 0) {
            return RenderType.entitySolid(JCraft.id(TEXTURE_STR_TEMPLATE.formatted(JStandTypeRegistry.CREAM.get().getId().getPath())));
        }
        return StandEntityRenderer.renderType(JStandTypeRegistry.CREAM.get()).apply(cream);
    }

    /*
    @Override
    public RenderType getRenderType(final CreamEntity animatable, final ResourceLocation texture, final @Nullable MultiBufferSource bufferSource, final float partialTick) {
        return animatable.getVoidTime() > 0 ? RenderType.entitySolid(texture) : super.getRenderType(animatable, texture, bufferSource, partialTick);
    }*/

    @Override
    protected float getRed(final CreamEntity stand, final float red, final float alpha) {
        return red - (1f - alpha) / 2f;
    }

    @Override
    protected float getGreen(final CreamEntity stand, final float green, final float alpha) {
        return green - (1f - alpha) / 2f;
    }

    /*
    @Override
    public void actuallyRender(final PoseStack poseStack, final CreamEntity animatable, final BakedGeoModel model, final RenderType renderType, final MultiBufferSource bufferSource, final VertexConsumer buffer, final boolean isReRender, final float partialTick, final int packedLight, final int packedOverlay, final float red, final float green, final float blue, final float alpha) {
        float a = StandEntityRenderer.getAlpha(animatable, partialTick);
        if (a < 0.01f) return;
        super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, a);
    }*/
}
