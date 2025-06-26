package net.arna.jcraft.client.renderer.entity.stands;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mod.azure.azurelib.cache.object.BakedGeoModel;
import mod.azure.azurelib.cache.object.GeoBone;
import mod.azure.azurelib.constant.DataTickets;
import mod.azure.azurelib.core.animation.AnimationState;
import mod.azure.azurelib.model.data.EntityModelData;
import mod.azure.azurelib.renderer.GeoEntityRenderer;
import net.arna.jcraft.client.model.entity.stand.StandEntityModel;
import net.arna.jcraft.client.util.JClientUtils;
import net.arna.jcraft.api.stand.StandEntity;
import net.arna.jcraft.common.util.JUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

/**
 * The {@link GeoEntityRenderer} for stands of any {@link net.arna.jcraft.api.stand.StandType StandType}.
 * @param <T> the entity to render
 * @see StandEntityModel
 */
public class StandEntityRenderer<T extends StandEntity<?, ?>> extends GeoEntityRenderer<T> {

    protected ItemStack mainHandItem, offHandItem;

    protected static final String LEFT_HAND = "bipedHandLeft";
    protected static final String RIGHT_HAND = "bipedHandRight";

    //private final StandEntityModel<T> standEntityModel;
    protected StandEntityRenderer(final EntityRendererProvider.Context renderManager, final StandEntityModel<T> modelProvider) {
        super(renderManager, modelProvider);
    }

    public static boolean standIsFirstPersonViewers(final StandEntity<?, ?> stand)
    {
        final Minecraft mcClient = Minecraft.getInstance();
        return mcClient.options.getCameraType().isFirstPerson() && mcClient.player != null && JUtils.getStand(mcClient.player) == stand;
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
    public static RenderType renderTypeOf(final StandEntity<?, ?> stand, final ResourceLocation textureLocation) {
        return standIsFirstPersonViewers(stand) ? RenderType.entityNoOutline(textureLocation) : RenderType.entityTranslucent(textureLocation);
    }

    @Override
    public RenderType getRenderType(final T animatable, final ResourceLocation texture, final @Nullable MultiBufferSource bufferSource, final float partialTick) {
        return renderTypeOf(animatable, texture);
    }

    // Adds the ability to change render alpha
    @Override
    public void preRender(final PoseStack poseStack, final T stand, final BakedGeoModel model, final MultiBufferSource bufferSource, final VertexConsumer buffer,
                          final boolean isReRender, final float partialTick, final int packedLight, final int packedOverlay, final float red, final float green, final float blue, final float alpha) {

        float a = getAlpha(stand, partialTick);
        a *= alpha;
        if (a <= 0.01f) {
            return;
        }

        super.preRender(poseStack, stand, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, getRed(stand, red, a), getGreen(stand, green, a), getBlue(stand, blue, a), alpha);
    }

    // Better than a mixin, makes stands look towards the users HEAD rotation as opposed to body
    @Override
    public void actuallyRender(final PoseStack poseStack, final T animatable, final BakedGeoModel model, RenderType renderType, final MultiBufferSource bufferSource,
                               VertexConsumer buffer, final boolean isReRender, final float partialTick, final int packedLight, final int packedOverlay, final float red, final float green, final float blue, final float alpha) {
        if (animatable == null) return;
        poseStack.pushPose();

        final boolean shouldSit = animatable.isPassenger() && (animatable.getVehicle() != null);
        float lerpBodyRot = Mth.rotLerp(partialTick, animatable.yBodyRotO, animatable.yBodyRot);
        float lerpHeadRot = Mth.rotLerp(partialTick, animatable.yHeadRotO, animatable.yHeadRot);
        float netHeadYaw = lerpHeadRot - lerpBodyRot;

        if (shouldSit && !animatable.isFree() && animatable.getVehicle() instanceof LivingEntity livingentity) {
            lerpBodyRot = Mth.rotLerp(partialTick, livingentity.yHeadRotO, livingentity.yHeadRot);
            netHeadYaw = lerpHeadRot - lerpBodyRot;
            float clampedHeadYaw = Mth.clamp(Mth.wrapDegrees(netHeadYaw), -85, 85);
            lerpBodyRot = lerpHeadRot - clampedHeadYaw;

            if (clampedHeadYaw * clampedHeadYaw > 2500f)
                lerpBodyRot += clampedHeadYaw * 0.2f;

            netHeadYaw = lerpHeadRot - lerpBodyRot;
        }

        if (animatable.getPose() == Pose.SLEEPING) {
            Direction bedDirection = animatable.getBedOrientation();

            if (bedDirection != null) {
                float eyePosOffset = animatable.getEyeHeight(Pose.STANDING) - 0.1F;

                poseStack.translate(-bedDirection.getStepX() * eyePosOffset, 0, -bedDirection.getStepZ() * eyePosOffset);
            }
        }

        final float ageInTicks = animatable.tickCount + partialTick;
        float limbSwingAmount = 0;
        float limbSwing = 0;

        applyRotations(animatable, poseStack, ageInTicks, lerpBodyRot, partialTick);

        if (!shouldSit && animatable.isAlive()) {
            limbSwingAmount = animatable.walkAnimation.speed(partialTick); // FIRST NOTABLE DIFF FROM super.actuallyRender();
            limbSwing = animatable.walkAnimation.position(partialTick);

            if (animatable.isBaby())
                limbSwing *= 3f;

            if (limbSwingAmount > 1f)
                limbSwingAmount = 1f;
        }

        if (!isReRender) {
            final float headPitch = Mth.lerp(partialTick, animatable.xRotO, animatable.getXRot());
            final float motionThreshold = getMotionAnimThreshold(animatable);
            final Vec3 velocity = animatable.getDeltaMovement();
            final float avgVelocity = (float)(Math.abs(velocity.x) + Math.abs(velocity.z) / 2f);
            final AnimationState<T> animationState = new AnimationState<>(animatable, limbSwing, limbSwingAmount,
                    partialTick, avgVelocity >= motionThreshold && limbSwingAmount != 0);
            final long instanceId = getInstanceId(animatable);

            animationState.setData(DataTickets.TICK, animatable.getTick(animatable));
            animationState.setData(DataTickets.ENTITY, animatable);
            EntityModelData data = new EntityModelData(shouldSit, animatable.isBaby(), -netHeadYaw, -headPitch);
            animationState.setData(DataTickets.ENTITY_MODEL_DATA, data);
            this.model.addAdditionalStateData(animatable, instanceId, animationState::setData);
            this.model.handleAnimations(animatable, instanceId, animationState);
        }

        poseStack.translate(0, 0.01f, 0);

        this.modelRenderTranslations = new Matrix4f(poseStack.last().pose());

        if (animatable.isInvisibleTo(Minecraft.getInstance().player)) {
            if (Minecraft.getInstance().shouldEntityAppearGlowing(animatable)) {
                buffer = bufferSource.getBuffer(renderType = RenderType.outline(getTextureLocation(animatable)));
            }
            else {
                renderType = null;
            }
        }

        if (renderType != null) {
            updateAnimatedTextureFrame(animatable);

            // Color renderColor = getRenderColor(animatable, partialTick, packedLight);

            for (GeoBone group : model.topLevelBones()) {
                renderRecursively(poseStack, animatable, group, renderType, bufferSource, buffer, isReRender, partialTick, packedLight,
                        packedOverlay,
                        red,
                        green,
                        blue,
                        alpha
                );
            }
        }

        poseStack.popPose();

        // Clear rotation modifications - SUPER hacky, please find a better way :)
        // better way found - just zero out all the anims at the start (THANKS GECKO!(irony))
        /*
        AnimationProcessor<?> animationProcessor = this.standEntityModel.getAnimationProcessor();
        CoreGeoBone torso = animationProcessor.getBone("torso");
        if (torso != null) torso.setRotX(standEntityModel.prevTorsoPitch);
        CoreGeoBone head = animationProcessor.getBone("head");
        if (head != null) head.setRotX(standEntityModel.prevHeadPitch);
        CoreGeoBone base = animationProcessor.getBone("base");
        if (base != null) base.setRotX(standEntityModel.prevBasePitch);
         */
    }

    @Override
    protected int getBlockLightLevel(final T stand, final BlockPos pos) {
        if (!stand.hasUser()) {
            return super.getBlockLightLevel(stand, pos);
        }

        if (stand.isOnFire() || stand.getUserOrThrow().isOnFire()) {
            return 15;
        }
        return stand.level().getBrightness(LightLayer.BLOCK, stand.getUserOrThrow().blockPosition());
    }

    @Override
    protected int getSkyLightLevel(final T stand, final BlockPos pos) {
        return stand.hasUser() ? stand.level().getBrightness(LightLayer.SKY, stand.getUserOrThrow().blockPosition()) :
                super.getSkyLightLevel(stand, pos);
    }

    @Override
    public boolean firePreRenderEvent(final PoseStack poseStack, final BakedGeoModel model, final MultiBufferSource bufferSource, final float partialTick, final int packedLight) {
        if (!JClientUtils.shouldRenderStands()) {
            return false;
        }

        return super.firePreRenderEvent(poseStack, model, bufferSource, partialTick, packedLight);
    }

    public static boolean shouldApplyAlpha(final StandEntity<?, ?> stand) {
        final Minecraft mcClient = Minecraft.getInstance();
        return mcClient.player != null && mcClient.options.getCameraType().isFirstPerson() && JUtils.getStand(mcClient.player) == stand;
    }

    public static float getAlpha(final StandEntity<?, ?> stand, final float tickDelta) {
        if (!shouldApplyAlpha(stand)) {
            return 1f;
        }

        // If we have an alpha override this tick and had one last tick too, just use that.
        if (stand.hasAlphaOverride() && stand.getPrevAlpha() >= 0) {
            return stand.getAlphaOverride();
        }

        final float a = Mth.clamp((float) stand.distanceToSqr(Minecraft.getInstance().player) / 2f, 0, 1);
        if (!stand.hasAlphaOverride()) {
            return a; // If we don't have an override, use this alpha value.
        }

        // If we do have an override, but didn't last tick, lerp between the previous alpha and the override.
        return Mth.lerp(tickDelta, a, stand.getAlphaOverride());
    }

    protected float getRed(final T stand, final float red, final float alpha) {
        return red;
    }

    protected float getGreen(final T stand, final float green, final float alpha) {
        return green;
    }

    protected float getBlue(final T stand, final float blue, final float alpha) {
        return blue;
    }
}
