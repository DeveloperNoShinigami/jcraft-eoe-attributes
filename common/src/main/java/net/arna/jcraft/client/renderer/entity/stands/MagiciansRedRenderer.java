package net.arna.jcraft.client.renderer.entity.stands;

import net.arna.jcraft.client.model.entity.MagiciansRedModel;
import net.arna.jcraft.client.renderer.entity.layer.MRGlowLayer;
import net.arna.jcraft.common.entity.stand.MagiciansRedEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.particle.ParticleTypes;
import org.joml.Vector3d;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.GeoModel;

public class MagiciansRedRenderer extends StandEntityRenderer<MagiciansRedEntity> {
    public MagiciansRedRenderer(EntityRendererFactory.Context context) {
        super(context, new MagiciansRedModel());
        addRenderLayer(new MRGlowLayer(this));
    }

    @Override
    public void actuallyRender(MatrixStack poseStack, MagiciansRedEntity animatable, BakedGeoModel model, RenderLayer renderType, VertexConsumerProvider bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
        if (animatable.getState() == MagiciansRedEntity.State.RED_BIND) {
            if (MinecraftClient.getInstance().isPaused()) return;
            model.getBone("rope3").ifPresent(bone -> {
                Vector3d worldPos = bone.getWorldPosition();

                animatable.getEntityWorld().addParticle(ParticleTypes.FLAME,
                        worldPos.x, worldPos.y, worldPos.z,
                        0.0, 0.0, 0.0
                );
            });
        }
    }
}
