package net.arna.jcraft.mixin_logic;

import net.arna.jcraft.common.entity.stand.StandEntity;
import net.arna.jcraft.common.gravity.api.GravityChangerAPI;
import net.arna.jcraft.common.gravity.util.RotationUtil;
import net.arna.jcraft.common.util.JUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public class EntityMixinLogic {

    public static void jcraft$updatePassengerPosition(Entity thisEntity, Entity passenger, Entity.PositionUpdater positionUpdater, CallbackInfo info) {
        if (passenger instanceof StandEntity<?, ?> stand) {
            if (stand.isFree() && !stand.isRemote()) {
                Vector3f freePos = stand.getFreePos();
                positionUpdater.accept(passenger, freePos.x(), freePos.y(), freePos.z());
                info.cancel();
                return;
            }

            Entity e = (thisEntity);
            double dist = stand.getDistanceOffset();

            float y = e.getYaw() + stand.getRotationOffset();
            y *= (float) Math.PI / 180;

            double heightOffset = stand.shouldOffsetHeight() ? Vec3d.fromPolar(e.getPitch(), e.getYaw()).y : 0;
            Vec3d adjustedOffset = RotationUtil.vecPlayerToWorld(
                    MathHelper.cos(y) * dist,
                    passenger.getHeightOffset() + heightOffset,
                    MathHelper.sin(y) * dist,
                    GravityChangerAPI.getGravityDirection(e)
            );
            positionUpdater.accept(passenger, e.getX() + adjustedOffset.x, e.getY() + adjustedOffset.y, e.getZ() + adjustedOffset.z);
            info.cancel();
        }
    }

    public static void doNotPlayDesummonSoundWhenMovingWorld(Entity entity, ServerWorld destination, CallbackInfoReturnable<Entity> cir) {
        if (!(entity instanceof LivingEntity living)) {
            return;
        }

        StandEntity<?, ?> stand = JUtils.getStand(living);
        if (stand == null) {
            return;
        }

        stand.setPlayDesummonSound(false);
    }
}
