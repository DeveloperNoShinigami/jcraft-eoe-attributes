package net.arna.jcraft.client.renderer.entity.stands;

import net.arna.jcraft.common.entity.stand.StandEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.DynamicGeoEntityRenderer;

@Deprecated(forRemoval = true)
public class ExtendedStandEntityRenderer<T extends StandEntity<?, ?>> extends DynamicGeoEntityRenderer<T> {
    protected ItemStack mainHandItem, offHandItem;

    protected static final String LEFT_HAND = "bipedHandLeft";
    protected static final String RIGHT_HAND = "bipedHandRight";

    protected ExtendedStandEntityRenderer(EntityRendererFactory.Context renderManager, GeoModel<T> modelProvider) {
        super(renderManager, modelProvider);
    }

    @Override
    public RenderLayer getRenderType(T animatable, Identifier texture, @Nullable VertexConsumerProvider bufferSource, float partialTick) {
        return StandEntityRenderer.renderTypeOf(animatable, texture);
    }

    @Override
    public void preRender(MatrixStack poseStack, T animatable, BakedGeoModel model, VertexConsumerProvider bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);

        this.mainHandItem = animatable.getMainHandStack();
        this.offHandItem = animatable.getOffHandStack();
    }

/*
    @Override
    public void render(T animatable, float entityYaw, float partialTick, MatrixStack poseStack, VertexConsumerProvider bufferSource, int packedLight) {
        poseStack.push();

        Entity leashHolder = animatable.getHoldingEntity();
        if (leashHolder != null) renderLeash(animatable, partialTick, poseStack, bufferSource, leashHolder);

        //this.dispatchedMat = poseStack.peek().getPositionMatrix().copy();
        float headPitch = MathHelper.lerp(partialTick, animatable.prevPitch, animatable.getPitch());
        float lerpBodyRot = MathHelper.lerpAngleDegrees(partialTick, animatable.prevBodyYaw, animatable.bodyYaw);
        float lerpHeadRot = MathHelper.lerpAngleDegrees(partialTick, animatable.prevHeadYaw, animatable.headYaw);
        float netHeadYaw = lerpHeadRot - lerpBodyRot;
        boolean shouldSit = animatable.hasVehicle() && (animatable.getVehicle() != null);
        EntityModelData entityModelData = new EntityModelData(shouldSit, animatable.isBaby(), -netHeadYaw, -headPitch);



        if (shouldSit && !animatable.isFree() && animatable.getVehicle() instanceof LivingEntity livingentity) {
            lerpBodyRot = MathHelper.lerpAngleDegrees(partialTick, livingentity.prevHeadYaw, livingentity.headYaw);
            netHeadYaw = lerpHeadRot - lerpBodyRot;
            float clampedHeadYaw = MathHelper.clamp(MathHelper.wrapDegrees(netHeadYaw), -85, 85);
            lerpBodyRot = lerpHeadRot - clampedHeadYaw;

            if (clampedHeadYaw * clampedHeadYaw > 2500f)
                lerpBodyRot += clampedHeadYaw * 0.2f;

            netHeadYaw = lerpHeadRot - lerpBodyRot;
        }

        if (animatable.getPose() == EntityPose.SLEEPING) {
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
            limbSwingAmount = MathHelper.lerp(partialTick, animatable.lastLimbDistance, animatable.limbDistance);
            limbSwing = animatable.limbAngle - animatable.limbDistance * (1 - partialTick);

            if (animatable.isBaby())
                limbSwing *= 3f;

            if (limbSwingAmount > 1f)
                limbSwingAmount = 1f;
        }

        AnimationState<T> predicate = new AnimationState<T>(animatable, limbSwing, limbSwingAmount, partialTick,
                (limbSwingAmount <= -getSwingMotionAnimThreshold() || limbSwingAmount > getSwingMotionAnimThreshold()));

        this.model.setCustomAnimations(animatable, getInstanceId(animatable), predicate);

        poseStack.translate(0, 0.01f, 0);
        RenderSystem.setShaderTexture(0, getTextureLocation(animatable));

        Color renderColor = getRenderColor(animatable, partialTick, packedLight);
        RenderLayer renderType = getRenderType(animatable, getTextureLocation(animatable), bufferSource, partialTick);

        if (!animatable.isInvisibleTo(MinecraftClient.getInstance().player)) {
            VertexConsumer glintBuffer = bufferSource.getBuffer(RenderLayer.getDirectEntityGlint());
            VertexConsumer translucentBuffer = bufferSource
                    .getBuffer(RenderLayer.getEntityTranslucentCull(getTextureLocation(animatable)));

            var uni = VertexConsumers.union(glintBuffer, translucentBuffer);
            actuallyRender(model, animatable, partialTick, renderType, poseStack, bufferSource,
                    false,
                    glintBuffer != translucentBuffer ? uni : null,
                    packedLight, getOverlay(animatable, 0), renderColor.getRed() / 255f,
                    renderColor.getGreen() / 255f, renderColor.getBlue() / 255f,
                    renderColor.getAlpha() / 255f);
        }

        if (!animatable.isSpectator()) {
            for (GeoRenderLayer<T> layerRenderer : this.getRenderLayers()) {
                renderLayer(poseStack, bufferSource, packedLight, animatable, limbSwing, limbSwingAmount, partialTick, ageInTicks,
                        netHeadYaw, headPitch, bufferSource, layerRenderer);
            }
        }

        if (FabricLoader.getInstance().isModLoaded("patchouli"))
            PatchouliCompat.patchouliLoaded(poseStack);

        poseStack.pop();
    }

 */

    protected float getSwingMotionAnimThreshold() {
        return 0.15f;
    }

    @Override
    protected int getBlockLight(T stand, BlockPos pos) {
        if (stand.hasUser()) {
            if (stand.isOnFire() || stand.getUserOrThrow().isOnFire()) {
                return 15;
            }
            return stand.getWorld().getLightLevel(LightType.BLOCK, stand.getUserOrThrow().getBlockPos());
        }
        return super.getBlockLight(stand, pos);
    }

    @Override
    protected int getSkyLight(T stand, BlockPos pos) {
        return stand.hasUser() ? stand.getWorld().getLightLevel(LightType.SKY, stand.getUserOrThrow().getBlockPos()) :
                super.getSkyLight(stand, pos);
    }
}
