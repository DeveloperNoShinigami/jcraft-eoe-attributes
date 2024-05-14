package net.arna.jcraft.client.renderer.entity.stands;

import net.arna.jcraft.common.entity.stand.StandEntity;
import net.arna.jcraft.common.util.JUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.*;
import net.minecraft.world.LightType;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.Color;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.GeoRenderer;

public class StandEntityRenderer<T extends StandEntity<?, ?>> extends GeoEntityRenderer<T> {

    protected ItemStack mainHandItem, offHandItem;

    protected static final String LEFT_HAND = "bipedHandLeft";
    protected static final String RIGHT_HAND = "bipedHandRight";

    protected StandEntityRenderer(EntityRendererFactory.Context renderManager, GeoModel<T> modelProvider) {
        super(renderManager, modelProvider);
    }

    /*
    Cutout - no alpha
    CutoutNoCull - identical (copium)
    Alpha - no lighting
    Translucent - with alpha, nothing renders through
    Decal - invisible
    NoOutline - transparent, everything is visible through
    Shadow - inverted normals, no alpha
    SmoothCutout - Cutout
    Solid - no transparency
     */
    public static RenderLayer renderTypeOf(StandEntity<?, ?> stand, Identifier textureLocation) {
        MinecraftClient mcClient = MinecraftClient.getInstance();
        return mcClient.options.getPerspective().isFirstPerson() && mcClient.player != null && JUtils.getStand(mcClient.player) == stand ?
                RenderLayer.getEntityNoOutline(textureLocation) : RenderLayer.getEntityTranslucent(textureLocation);
    }

    @Override
    public RenderLayer getRenderType(T animatable, Identifier texture, @Nullable VertexConsumerProvider bufferSource, float partialTick) {
        MinecraftClient mcClient = MinecraftClient.getInstance();
        return mcClient.options.getPerspective().isFirstPerson() && mcClient.player != null && JUtils.getStand(mcClient.player) == animatable ?
                RenderLayer.getEntityNoOutline(texture) : RenderLayer.getEntityTranslucent(texture);
    }

    // Adds the ability to change render alpha
    @Override
    public void preRender(MatrixStack poseStack, T stand, BakedGeoModel model, VertexConsumerProvider bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {

        float a = getAlpha(stand, partialTick);
        a *= alpha;
        if (a <= 0.01f) {
            return;
        }

        super.preRender(poseStack, stand, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, getRed(stand, red, a), getGreen(stand, green, a), getBlue(stand, blue, a), alpha);
    }

    // Better than a mixin, makes stands look towards the users HEAD rotation as opposed to body


    @Override
    public void actuallyRender(MatrixStack poseStack, T animatable, BakedGeoModel model, RenderLayer renderType, VertexConsumerProvider bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        poseStack.push();

        boolean shouldSit = animatable.hasVehicle() && (animatable.getVehicle() != null);
        float lerpBodyRot = MathHelper.lerpAngleDegrees(partialTick, animatable.prevBodyYaw, animatable.bodyYaw);
        float lerpHeadRot = MathHelper.lerpAngleDegrees(partialTick, animatable.prevHeadYaw, animatable.headYaw);
        float netHeadYaw = lerpHeadRot - lerpBodyRot;

        if (shouldSit && !animatable.isFree() && animatable.getVehicle() instanceof LivingEntity livingentity) {
            lerpBodyRot = MathHelper.lerpAngleDegrees(partialTick, livingentity.prevHeadYaw, livingentity.headYaw);
            netHeadYaw = lerpHeadRot - lerpBodyRot;
            float clampedHeadYaw = MathHelper.clamp(MathHelper.wrapDegrees(netHeadYaw), -85, 85);
            lerpBodyRot = lerpHeadRot - clampedHeadYaw;

            if (clampedHeadYaw * clampedHeadYaw > 2500f)
                lerpBodyRot += clampedHeadYaw * 0.2f;

            netHeadYaw = lerpHeadRot - lerpBodyRot;
        }

        if (animatable.getPose() == EntityPose.SLEEPING && animatable != null) {
            Direction bedDirection = animatable.getSleepingDirection();

            if (bedDirection != null) {
                float eyePosOffset = animatable.getEyeHeight(EntityPose.STANDING) - 0.1F;

                poseStack.translate(-bedDirection.getOffsetX() * eyePosOffset, 0, -bedDirection.getOffsetZ() * eyePosOffset);
            }
        }

        float ageInTicks = animatable.age + partialTick;
        float limbSwingAmount = 0;
        float limbSwing = 0;

        applyRotations(animatable, poseStack, ageInTicks, lerpBodyRot, partialTick);

        if (!shouldSit && animatable.isAlive()) {
            limbSwingAmount = animatable.limbAnimator.getSpeed(partialTick);
            limbSwing = animatable.limbAnimator.getPos(partialTick);

            if (animatable.isBaby())
                limbSwing *= 3f;

            if (limbSwingAmount > 1f)
                limbSwingAmount = 1f;
        }

        if (!isReRender) {
            float headPitch = MathHelper.lerp(partialTick, animatable.prevPitch, animatable.getPitch());
            float motionThreshold = getMotionAnimThreshold(animatable);
            Vec3d velocity = animatable.getVelocity();
            float avgVelocity = (float)(Math.abs(velocity.x) + Math.abs(velocity.z) / 2f);
            AnimationState<T> animationState = new AnimationState<T>(animatable, limbSwing, limbSwingAmount, partialTick, avgVelocity >= motionThreshold && limbSwingAmount != 0);
            long instanceId = getInstanceId(animatable);

            animationState.setData(DataTickets.TICK, animatable.getTick(animatable));
            animationState.setData(DataTickets.ENTITY, animatable);
            animationState.setData(DataTickets.ENTITY_MODEL_DATA, new EntityModelData(shouldSit, animatable.isBaby(), -netHeadYaw, -headPitch));
            this.model.addAdditionalStateData(animatable, instanceId, animationState::setData);
            this.model.handleAnimations(animatable, instanceId, animationState);
        }

        poseStack.translate(0, 0.01f, 0);

        this.modelRenderTranslations = new Matrix4f(poseStack.peek().getPositionMatrix());

        if (animatable.isInvisibleTo(MinecraftClient.getInstance().player)) {
            if (MinecraftClient.getInstance().hasOutline(animatable)) {
                buffer = bufferSource.getBuffer(renderType = RenderLayer.getOutline(getTexture(animatable)));
            }
            else {
                renderType = null;
            }
        }

        if (renderType != null) {
            updateAnimatedTextureFrame(animatable);

            Color renderColor = getRenderColor(animatable, partialTick, packedLight);

            for (GeoBone group : model.topLevelBones()) {
                renderRecursively(poseStack, animatable, group, renderType, bufferSource, buffer, isReRender, partialTick, packedLight,
                        packedOverlay,
                        renderColor.getRed() / 255f,
                        renderColor.getGreen() / 255f,
                        renderColor.getBlue() / 255f,
                        renderColor.getAlpha() / 255f
                );
            }
        }

        poseStack.pop();
    }

    @Override
    protected int getBlockLight(T stand, BlockPos pos) {
        if (!stand.hasUser()) {
            return super.getBlockLight(stand, pos);
        }

        if (stand.isOnFire() || stand.getUserOrThrow().isOnFire()) {
            return 15;
        }
        return stand.getWorld().getLightLevel(LightType.BLOCK, stand.getUserOrThrow().getBlockPos());
    }

    @Override
    protected int getSkyLight(T stand, BlockPos pos) {
        return stand.hasUser() ? stand.getWorld().getLightLevel(LightType.SKY, stand.getUserOrThrow().getBlockPos()) :
                super.getSkyLight(stand, pos);
    }

    public static boolean shouldApplyAlpha(StandEntity<?, ?> stand) {
        MinecraftClient mcClient = MinecraftClient.getInstance();
        return mcClient.player != null && mcClient.options.getPerspective().isFirstPerson() && JUtils.getStand(mcClient.player) == stand;
    }

    public static float getAlpha(StandEntity<?, ?> stand, float tickDelta) {
        if (!shouldApplyAlpha(stand)) {
            return 1f;
        }

        // If we have an alpha override this tick and had one last tick too, just use that.
        if (stand.hasAlphaOverride() && stand.getPrevAlpha() >= 0) {
            return stand.getAlphaOverride();
        }

        float a = MathHelper.clamp((float) stand.squaredDistanceTo(MinecraftClient.getInstance().player) / 2f, 0, 1);
        if (!stand.hasAlphaOverride()) {
            return a; // If we don't have an override, use this alpha value.
        }

        // If we do have an override, but didn't last tick, lerp between the previous alpha and the override.
        return MathHelper.lerp(tickDelta, a, stand.getAlphaOverride());
    }

    protected float getRed(T stand, float red, float alpha) {
        return red;
    }

    protected float getGreen(T stand, float green, float alpha) {
        return green;
    }

    protected float getBlue(T stand, float blue, float alpha) {
        return blue;
    }
}
