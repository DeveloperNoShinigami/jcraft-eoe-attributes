package net.arna.jcraft.client.renderer.entity.stands;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mod.azure.azurelib.cache.object.BakedGeoModel;
import net.arna.jcraft.client.model.entity.MagiciansRedModel;
import net.arna.jcraft.common.entity.stand.MagiciansRedEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.particles.ParticleTypes;
import org.joml.Vector3d;

public class MagiciansRedRenderer extends StandEntityRenderer<MagiciansRedEntity> {
    public MagiciansRedRenderer(EntityRendererProvider.Context context) {
        super(context, new MagiciansRedModel());
        //todo: animated glow layer for mr fire + whip
        // addRenderLayer(new MRGlowLayer(this));
    }

    @Override
    public void actuallyRender(PoseStack poseStack, MagiciansRedEntity animatable, BakedGeoModel model, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, StandEntityRenderer.getAlpha(animatable, partialTick));
        if (animatable.getState() == MagiciansRedEntity.State.RED_BIND) {
            if (Minecraft.getInstance().isPaused()) {
                return;
            }
            model.getBone("rope3").ifPresent(bone -> {
                Vector3d worldPos = bone.getWorldPosition();

                animatable.getCommandSenderWorld().addParticle(ParticleTypes.FLAME,
                        worldPos.x, worldPos.y, worldPos.z,
                        0.0, 0.0, 0.0
                );
            });
        }
    }
}
