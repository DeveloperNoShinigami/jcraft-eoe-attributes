package net.arna.jcraft.client.renderer.entity.stands;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import lombok.NonNull;
import net.arna.jcraft.api.registry.JStandTypeRegistry;
import net.arna.jcraft.client.model.entity.stand.ShadowTheWorldModel;
import net.arna.jcraft.client.renderer.entity.layer.STWGlowLayer;
import net.arna.jcraft.common.entity.stand.ShadowTheWorldEntity;
import net.arna.jcraft.common.util.JUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

/**
 * The {@link StandEntityRenderer} for {@link ShadowTheWorldEntity}.
 * @see ShadowTheWorldModel
 */

public class ShadowTheWorldRenderer extends StandEntityRenderer<ShadowTheWorldEntity> {

    public ShadowTheWorldRenderer(final @NonNull EntityRendererProvider.Context context) {
        super(context, b -> b.addRenderLayer(new STWGlowLayer()), JStandTypeRegistry.SHADOW_THE_WORLD.get());
    }

    /*
    @Override
    public RenderType getRenderType(final ShadowTheWorldEntity animatable, final ResourceLocation texture, final @Nullable MultiBufferSource bufferSource, final float partialTick) {
        Minecraft mcClient = Minecraft.getInstance();
        return mcClient.options.getCameraType().isFirstPerson() && mcClient.player != null && JUtils.getStand(mcClient.player) == animatable ?
                RenderType.entityNoOutline(texture) : RenderType.entityTranslucentCull(texture);
    }

    @Override
    public void actuallyRender(final PoseStack poseStack, final ShadowTheWorldEntity animatable, final BakedGeoModel model, final RenderType renderType, final MultiBufferSource bufferSource, final VertexConsumer buffer, final boolean isReRender, final float partialTick, final int packedLight, final int packedOverlay, final float red, final float green, final float blue, final float alpha) {
        final float a = StandEntityRenderer.getAlpha(animatable, partialTick);
        super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, 15728640, packedOverlay, red, green, blue, a);
    }*/
}
