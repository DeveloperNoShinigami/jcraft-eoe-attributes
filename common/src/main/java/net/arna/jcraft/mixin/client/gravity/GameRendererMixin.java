package net.arna.jcraft.mixin.client.gravity;

import net.arna.jcraft.common.gravity.RotationAnimation;
import net.arna.jcraft.common.gravity.api.GravityChangerAPI;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Optional;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Shadow
    @Final
    private Camera mainCamera;

    @Inject(
            method = "renderLevel",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/vertex/PoseStack;mulPose(Lorg/joml/Quaternionf;)V",
                    ordinal = 1,
                    shift = At.Shift.AFTER
            ),
            // Slice is here to ensure the correct call is targeted on Forge.
            // Forge adds an extra mulpose call before the one we want to target for roll.
            slice = @Slice(
                    from = @At(
                            value = "FIELD",
                            target = "Lcom/mojang/math/Axis;XP:Lcom/mojang/math/Axis;"
                    )
            )
    )
    private void inject_renderWorld(float tickDelta, long limitTime, PoseStack matrix, CallbackInfo ci) {
        Entity focusedEntity = this.mainCamera.getEntity();
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(focusedEntity);
        Optional<RotationAnimation> animationOptional = GravityChangerAPI.getGravityAnimation(focusedEntity);
        if (animationOptional.isEmpty()) {
            return;
        }
        RotationAnimation animation = animationOptional.get();
        long timeMs = focusedEntity.level().getGameTime() * 50 + (long) (tickDelta * 50);
        Quaternionf currentGravityRotation = animation.getCurrentGravityRotation(gravityDirection, timeMs);
        matrix.mulPose(currentGravityRotation);
    }
}
