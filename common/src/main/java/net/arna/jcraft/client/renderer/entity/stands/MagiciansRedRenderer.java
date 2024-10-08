package net.arna.jcraft.client.renderer.entity.stands;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mod.azure.azurelib.cache.object.BakedGeoModel;
import net.arna.jcraft.client.model.entity.stand.MagiciansRedModel;
import net.arna.jcraft.client.renderer.entity.layer.MRGlowLayer;
import net.arna.jcraft.common.entity.stand.MagiciansRedEntity;
import net.arna.jcraft.common.gravity.api.GravityChangerAPI;
import net.arna.jcraft.common.gravity.util.RotationUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;

/**
 * The {@link StandEntityRenderer} for {@link MagiciansRedEntity}.
 * @see MagiciansRedModel
 */
public class MagiciansRedRenderer extends StandEntityRenderer<MagiciansRedEntity> {
    public MagiciansRedRenderer(final EntityRendererProvider.Context context) {
        super(context, new MagiciansRedModel());
        addRenderLayer(new MRGlowLayer(this));
    }

    @Override
    public void actuallyRender(final PoseStack poseStack, final MagiciansRedEntity animatable, final BakedGeoModel model, final RenderType renderType, final MultiBufferSource bufferSource, final VertexConsumer buffer, final boolean isReRender, final float partialTick, final int packedLight, final int packedOverlay, final float red, final float green, final float blue, final float alpha) {
        super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, StandEntityRenderer.getAlpha(animatable, partialTick));
        // todo: try to replace this with a particle keyframe
        if (animatable.getState() == MagiciansRedEntity.State.RED_BIND) {
            if (Minecraft.getInstance().isPaused()) {
                return;
            }
            model.getBone("rope3").ifPresent(bone -> {
                final Vector3d localPos = bone.getLocalPosition();
                final Vec3 worldPos = RotationUtil.vecWorldToPlayer(localPos.x, localPos.y, localPos.z, GravityChangerAPI.getGravityDirection(animatable)).add(animatable.position());

                animatable.getCommandSenderWorld().addParticle(ParticleTypes.FLAME,
                        worldPos.x, worldPos.y, worldPos.z,
                        0.0, 0.0, 0.0
                );
            });
        }
    }
}
