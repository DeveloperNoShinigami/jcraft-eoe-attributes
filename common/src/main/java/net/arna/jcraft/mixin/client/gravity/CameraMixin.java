package net.arna.jcraft.mixin.client.gravity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.arna.jcraft.common.gravity.RotationAnimation;
import net.arna.jcraft.common.gravity.api.GravityChangerAPI;
import net.arna.jcraft.common.gravity.util.CompatMath;
import net.minecraft.client.Camera;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(value = Camera.class, priority = 1001)
public abstract class CameraMixin {
    @Shadow protected abstract void setPosition(double x, double y, double z);

    @Shadow private Entity entity;

    @Shadow @Final private Quaternionf rotation;

    @Shadow private float eyeHeightOld; // lastCameraY

    @Shadow private float eyeHeight; // cameraY

    private float storedTickDelta = 0.f;

    @Inject(method="setup", at=@At("HEAD"))
    private void inject_update(BlockGetter area, Entity entity, boolean detached, boolean thirdPersonReverse, float tickDelta, CallbackInfo ci){
        storedTickDelta = tickDelta;
    }

    @WrapOperation(
            method = "setup",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/Camera;setPosition(DDD)V",
                    ordinal = 0
            )
    )
    private void wrapOperation_update_setPos_0(Camera camera, double x, double y, double z, Operation<Void> original, BlockGetter area, Entity entity, boolean thirdPerson, boolean inverseView, float tickDelta) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(entity);
        Optional<RotationAnimation> animationOptional = GravityChangerAPI.getGravityAnimation(entity);
        if(animationOptional.isEmpty()){
            original.call(this, x, y, z);
            return;
        }
        RotationAnimation animation = animationOptional.get();
        if (gravityDirection == Direction.DOWN && !animation.isInAnimation()) {
            original.call(this, x, y, z);
            return;
        }
        long timeMs = entity.level().getGameTime()*50+(long)(storedTickDelta*50);
        Quaternionf gravityRotation = animation.getCurrentGravityRotation(gravityDirection, timeMs).conjugate();

        double entityX = Mth.lerp(tickDelta, entity.xOld, entity.getX());
        double entityY = Mth.lerp(tickDelta, entity.yOld, entity.getY());
        double entityZ = Mth.lerp(tickDelta, entity.zOld, entity.getZ());

        double currentCameraY = Mth.lerp(tickDelta, this.eyeHeightOld, this.eyeHeight);

        Vector3f eyeOffset = new Vector3f(0, (float) currentCameraY, 0);
        eyeOffset.rotate(gravityRotation);

        original.call(
                this,
                entityX + eyeOffset.x(),
                entityY + eyeOffset.y(),
                entityZ + eyeOffset.z()
        );
    }

    @Inject(
            method = "setRotation",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/joml/Quaternionf;rotationYXZ(FFF)Lorg/joml/Quaternionf;",
                    shift = At.Shift.AFTER
            )
    )
    private void inject_setRotation(CallbackInfo ci) {
        if(this.entity !=null) {
            Direction gravityDirection = GravityChangerAPI.getGravityDirection(this.entity);
            Optional<RotationAnimation> animationOptional = GravityChangerAPI.getGravityAnimation(entity);
            if(animationOptional.isEmpty()) return;
            RotationAnimation animation = animationOptional.get();
            if (gravityDirection == Direction.DOWN && !animation.isInAnimation()) return;
            long timeMs = entity.level().getGameTime()*50+(long)(storedTickDelta*50);
            Quaternionf rotation = animation.getCurrentGravityRotation(gravityDirection, timeMs).conjugate();
            Quaternionf product = CompatMath.hamiltonProduct(rotation,this.rotation);
            this.rotation.set(product.x(), product.y(), product.z(), product.w());
        }
    }
}