package net.arna.jcraft.client.renderer.entity.stands;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mod.azure.azurelib.cache.object.BakedGeoModel;
import net.arna.jcraft.client.model.entity.GoldenExperienceModel;
import net.arna.jcraft.common.entity.stand.GoldExperienceEntity;
import net.arna.jcraft.common.util.JUtils;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;

public class GoldExperienceRenderer extends StandEntityRenderer<GoldExperienceEntity> {
    private int currentTick = -1;
    private static final int overclockWindupPoint = GoldExperienceEntity.OVERCLOCK.getWindupPoint();
    private static final ParticleOptions chargeParticle = ParticleTypes.COMPOSTER;

    public GoldExperienceRenderer(EntityRendererProvider.Context context) {
        super(context, new GoldenExperienceModel());
    }

    @Override
    public void actuallyRender(PoseStack poseStack, GoldExperienceEntity stand, BakedGeoModel model, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);

        if (stand.getState() == GoldExperienceEntity.State.OVERCLOCK && stand.getMoveStun() > overclockWindupPoint) {
            if (currentTick < 0 || currentTick != stand.tickCount) {
                this.currentTick = stand.tickCount;
                model.getBone("lowerleft").ifPresent(bone -> {
                    RandomSource random = stand.getRandom();
                    Vector3d worldPos = bone.getWorldPosition();
                    Vec3 standVel = JUtils.deltaPos(stand);

                    stand.getCommandSenderWorld().addParticle(chargeParticle,
                            worldPos.x, worldPos.y, worldPos.z,
                            standVel.x + random.nextGaussian() * 0.3,
                            standVel.y + random.nextGaussian() * 0.3,
                            standVel.z + random.nextGaussian() * 0.3
                    );
                });
            }
        }
    }
}
