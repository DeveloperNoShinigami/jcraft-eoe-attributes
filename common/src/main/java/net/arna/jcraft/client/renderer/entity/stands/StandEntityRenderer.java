package net.arna.jcraft.client.renderer.entity.stands;

import lombok.NonNull;
import mod.azure.azurelib.render.AzRendererPipelineContext;
import mod.azure.azurelib.render.entity.AzEntityRendererConfig;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.api.stand.StandType;
import net.arna.jcraft.client.JClientConfig;
import net.arna.jcraft.client.renderer.entity.AbstractEntityRenderer;
import net.arna.jcraft.api.stand.StandEntity;
import net.arna.jcraft.common.util.JUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.function.Function;
import java.util.function.UnaryOperator;

/**
 * The {@link AbstractEntityRenderer} for stands of any {@link StandType StandType}.
 * @param <T> the entity to render
 */
@Environment(EnvType.CLIENT)
public class StandEntityRenderer<T extends StandEntity<?, ?>> extends AbstractEntityRenderer<T> {

    protected ItemStack mainHandItem, offHandItem;

    protected static final String LEFT_HAND = "bipedHandLeft";
    protected static final String RIGHT_HAND = "bipedHandRight";

    public StandEntityRenderer(final @NonNull EntityRendererProvider.Context context, final @NonNull StandType type) {
        this(context, UnaryOperator.identity(), type);
    }

    protected StandEntityRenderer(final @NonNull EntityRendererProvider.Context context, final @NonNull UnaryOperator<AzEntityRendererConfig.Builder<T>> additionalConfigs, final @NonNull StandType type) {
        super(context, () -> new EntityAnimator<>(type.getId().getPath()),
                (UnaryOperator<AzEntityRendererConfig.Builder<T>>)additionalConfigs.<AzEntityRendererConfig.Builder<T>>compose(
                        b -> b
                                .setRenderType(renderType(type))
                                .setPrerenderEntry(preRenderEntry())),
                type.getId().getPath());
    }

    protected static @NotNull <T extends StandEntity<?,?>> Function<T, RenderType> renderType(final @NotNull StandType type) {
        return stand -> StandEntityRenderer.renderTypeOf(stand, JCraft.id(TEXTURE_STR_TEMPLATE.formatted(type.getId().getPath())));
    }

    protected static @NotNull <T extends StandEntity<?,?>> Function<AzRendererPipelineContext<UUID, T>, AzRendererPipelineContext<UUID, T>> preRenderEntry() {
        return pc -> {
            float a = getAlpha(pc.animatable(), pc.partialTick());
            a *= pc.alpha();
            if (a > 0.01f) {
                pc.setAlpha(a);
            }
            return pc;
        };
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

    /*
    @Override
    public RenderType getRenderType(final T animatable, final ResourceLocation texture, final @Nullable MultiBufferSource bufferSource, final float partialTick) {
        return renderTypeOf(animatable, texture);
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
        *//*
        AnimationProcessor<?> animationProcessor = this.standEntityModel.getAnimationProcessor();
        CoreGeoBone torso = animationProcessor.getBone("torso");
        if (torso != null) torso.setRotX(standEntityModel.prevTorsoPitch);
        CoreGeoBone head = animationProcessor.getBone("head");
        if (head != null) head.setRotX(standEntityModel.prevHeadPitch);
        CoreGeoBone base = animationProcessor.getBone("base");
        if (base != null) base.setRotX(standEntityModel.prevBasePitch);
         *//*
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
    }*/

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

        final JClientConfig config = JClientConfig.getInstance();
        final float alphaMult = config.getFirstPersonStandOpacityMult();

        final float a =
                config.isDynamicFirstPersonStandOpacity() ?
                        alphaMult * Mth.clamp((float) stand.distanceToSqr(Minecraft.getInstance().player) / 2f, 0, 1) :
                        alphaMult;

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
