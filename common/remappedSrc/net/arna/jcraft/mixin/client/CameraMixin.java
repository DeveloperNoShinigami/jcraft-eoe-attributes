package net.arna.jcraft.mixin.client;

import net.arna.jcraft.common.entity.stand.StandEntity;
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
    private BlockGetter area;
    @Shadow
    private Vec3 pos;
    @Final
    @Shadow
    private Vector3f verticalPlane;
    @Shadow
    private Entity focusedEntity;
    @Shadow
    private boolean thirdPerson;
    @Shadow
    private float lastCameraY;
    @Shadow
    private float cameraY;

    private double clipToSpaceVertical(double desiredCameraDistance) {
        for (int i = 0; i < 8; ++i) {
            float f = (float) ((i & 1) * 2 - 1);
            float g = (float) ((i >> 1 & 1) * 2 - 1);
            float h = (float) ((i >> 2 & 1) * 2 - 1);
            f *= 0.1F;
            g *= 0.1F;
            h *= 0.1F;
            Vec3 vec3d = pos.add(f, g, h);
            Vec3 vec3d2 = new Vec3(pos.x - (double) verticalPlane.x() * desiredCameraDistance + (double) f + (double) h, pos.y - (double) verticalPlane.y() * desiredCameraDistance + (double) g, pos.z - (double) verticalPlane.z() * desiredCameraDistance + (double) h);
            HitResult hitResult = area.clip(new ClipContext(vec3d, vec3d2, ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, this.focusedEntity));
            if (hitResult.getType() != HitResult.Type.MISS) {
                double d = hitResult.getLocation().distanceTo(this.pos);
                if (d < desiredCameraDistance) {
                    desiredCameraDistance = d;
                }
            }
        }

        return desiredCameraDistance;
    }

    @Inject(method = "update", cancellable = true, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;setPos(DDD)V", shift = At.Shift.BEFORE))
    public void jcraft$prevSetPosUpdate(BlockGetter area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo info) {
        if (focusedEntity instanceof LivingEntity living) {
            if (living.hasEffect(JStatusRegistry.OUTOFBODY.get())) {
                this.thirdPerson = true;
                info.cancel();
            }
        }
    }

    @Inject(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;setPos(DDD)V", shift = At.Shift.AFTER))
    public void jcraft$afterSetPosUpdate(BlockGetter area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo info) {
        StandEntity<?, ?> stand = focusedEntity instanceof LivingEntity living ? JUtils.getStand(living) : null;
        if (stand != null && stand.isRemoteAndControllable()) {
            CameraInvoker cameraInvoker = (CameraInvoker) this;
            cameraInvoker.invokeSetPos(
                    new Vec3(
                            Mth.lerp(tickDelta, stand.xo, stand.getX()),
                            Mth.lerp(tickDelta, stand.yo, stand.getY()) + (double) Mth.lerp(tickDelta, this.lastCameraY, this.cameraY),
                            Mth.lerp(tickDelta, stand.zo, stand.getZ())
                    )
            );
            this.thirdPerson = true;
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
