package net.arna.jcraft.mixin.client;

import net.arna.jcraft.api.stand.StandEntity;
import net.arna.jcraft.common.util.JUtils;
import net.arna.jcraft.registry.JStatusRegistry;
import net.minecraft.client.Camera;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class CameraMixin {
    @Shadow
    private BlockGetter level;
    @Shadow
    private Vec3 position;
    @Final
    @Shadow
    private Vector3f up;
    @Shadow
    private Entity entity;
    @Shadow
    private boolean detached;
    @Shadow
    private float eyeHeightOld;
    @Shadow
    private float eyeHeight;

    private double clipToSpaceVertical(double desiredCameraDistance) {
        for (int i = 0; i < 8; ++i) {
            float f = (float) ((i & 1) * 2 - 1);
            float g = (float) ((i >> 1 & 1) * 2 - 1);
            float h = (float) ((i >> 2 & 1) * 2 - 1);
            f *= 0.1F;
            g *= 0.1F;
            h *= 0.1F;
            Vec3 vec3d = position.add(f, g, h);
            Vec3 vec3d2 = new Vec3(position.x - (double) up.x() * desiredCameraDistance + (double) f + (double) h, position.y - (double) up.y() * desiredCameraDistance + (double) g, position.z - (double) up.z() * desiredCameraDistance + (double) h);
            HitResult hitResult = level.clip(new ClipContext(vec3d, vec3d2, ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, this.entity));
            if (hitResult.getType() != HitResult.Type.MISS) {
                double d = hitResult.getLocation().distanceTo(this.position);
                if (d < desiredCameraDistance) {
                    desiredCameraDistance = d;
                }
            }
        }

        return desiredCameraDistance;
    }

    @Inject(method = "setup", cancellable = true, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;setPosition(DDD)V", shift = At.Shift.BEFORE))
    public void jcraft$prevSetPosUpdate(BlockGetter area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo info) {
        if (focusedEntity instanceof LivingEntity living) {
            if (living.hasEffect(JStatusRegistry.OUTOFBODY.get())) {
                this.detached = true;
                info.cancel();
            }
        }
    }

    @Inject(method = "setup", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;setPosition(DDD)V", shift = At.Shift.AFTER))
    public void jcraft$afterSetPosUpdate(BlockGetter area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo info) {
        StandEntity<?, ?> stand = focusedEntity instanceof LivingEntity living ? JUtils.getStand(living) : null;
        if (stand != null && stand.isRemoteAndControllable()) {
            CameraInvoker cameraInvoker = (CameraInvoker) this;
            cameraInvoker.invokeSetPos(
                    new Vec3(
                            Mth.lerp(tickDelta, stand.xo, stand.getX()),
                            Mth.lerp(tickDelta, stand.yo, stand.getY()) + (double) Mth.lerp(tickDelta, this.eyeHeightOld, this.eyeHeight),
                            Mth.lerp(tickDelta, stand.zo, stand.getZ())
                    )
            );
            this.detached = true;
        }

        /*
        if (!inverseView) {
            CameraInvoker cameraInvoker = (CameraInvoker) this;
            cameraInvoker.invokeMoveBy(-cameraInvoker.invokeClipToSpace(2.5D), 0, 0);
            cameraInvoker.invokeMoveBy(0, clipToSpaceVertical(0.75D), 0);
            info.cancel();
        }*/
    }
}
