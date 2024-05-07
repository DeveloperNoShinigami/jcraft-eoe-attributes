package net.arna.jcraft.mixin.client.gravity;

import net.arna.jcraft.common.gravity.RotationAnimation;
import net.arna.jcraft.common.gravity.api.GravityChangerAPI;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(Camera.class)
public abstract class CameraMixin {
    @Shadow protected abstract void setPos(double x, double y, double z);

    @Shadow private Entity focusedEntity;

    @Shadow @Final private Quaternionf rotation;

    @Shadow private float lastCameraY;

    @Shadow private float cameraY;

    private float storedTickDelta = 0.f;

    @Inject(method="update", at=@At("HEAD"))
    private void inject_update(BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo ci){
        storedTickDelta = tickDelta;
    }

    @Redirect(
            method = "update",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/Camera;setPos(DDD)V",
                    ordinal = 0
            )
    )
    private void redirect_update_setPos_0(Camera camera, double x, double y, double z, BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(focusedEntity);
        Optional<RotationAnimation> animationOptional = GravityChangerAPI.getGravityAnimation(focusedEntity);
        if(animationOptional.isEmpty()){
            this.setPos(x, y, z);
            return;
        }
        RotationAnimation animation = animationOptional.get();
        if (gravityDirection == Direction.DOWN && !animation.isInAnimation()) {
            this.setPos(x, y, z);
            return;
        }
        long timeMs = focusedEntity.getWorld().getTime()*50+(long)(storedTickDelta*50);
        Quaternionf gravityRotation = new Quaternionf(animation.getCurrentGravityRotation(gravityDirection, timeMs));
        gravityRotation.conjugate();

        double entityX = MathHelper.lerp(tickDelta, focusedEntity.prevX, focusedEntity.getX());
        double entityY = MathHelper.lerp(tickDelta, focusedEntity.prevY, focusedEntity.getY());
        double entityZ = MathHelper.lerp(tickDelta, focusedEntity.prevZ, focusedEntity.getZ());

        double currentCameraY = MathHelper.lerp(tickDelta, this.lastCameraY, this.cameraY);

        Vector3f eyeOffset = new Vector3f(0, (float) currentCameraY, 0);
        eyeOffset.rotate(gravityRotation);

        this.setPos(
                entityX + eyeOffset.x(),
                entityY + eyeOffset.y(),
                entityZ + eyeOffset.z()
        );
    }

    @Inject(
            method = "setRotation",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/joml/Quaternionf;rotationYXZ(FFF)Lorg/joml/Quaternionf;"
            )
    )
    private void inject_setRotation(CallbackInfo ci) {
        if(this.focusedEntity !=null) {
            Direction gravityDirection = GravityChangerAPI.getGravityDirection(this.focusedEntity);
            Optional<RotationAnimation> animationOptional = GravityChangerAPI.getGravityAnimation(focusedEntity);
            if(animationOptional.isEmpty()) return;
            RotationAnimation animation = animationOptional.get();
            if (gravityDirection == Direction.DOWN && !animation.isInAnimation()) return;
            long timeMs = focusedEntity.getWorld().getTime()*50+(long)(storedTickDelta*50);
            Quaternionf rotation = new Quaternionf(animation.getCurrentGravityRotation(gravityDirection, timeMs));
            rotation.conjugate();
            rotation.mul(this.rotation);
            this.rotation.set(rotation.x(), rotation.y(), rotation.z(), rotation.w());
        }
    }
}
