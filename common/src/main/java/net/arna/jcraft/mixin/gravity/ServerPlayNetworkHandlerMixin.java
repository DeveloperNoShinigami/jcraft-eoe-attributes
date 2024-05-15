package net.arna.jcraft.mixin.gravity;


import net.arna.jcraft.common.gravity.api.GravityChangerAPI;
import net.arna.jcraft.common.gravity.util.RotationUtil;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkHandlerMixin {
    @Unique
    private static double onPlayerMove_playerMovementY;

    @Shadow
    public ServerPlayerEntity player;

    @Shadow
    private static double clampHorizontal(double d) {
        return 0;
    }

    @Shadow
    private static double clampVertical(double d) {
        return 0;
    }

    @Shadow
    private double updatedX;

    @Shadow
    private double updatedY;

    @Shadow
    private double updatedZ;

    @Redirect(
            method = "onPlayerMove",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/network/ServerPlayerEntity;getY()D",
                    ordinal = 3
            )
    )
    private double redirect_onPlayerMove_getY_3(ServerPlayerEntity serverPlayerEntity) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(serverPlayerEntity);
        if (gravityDirection == Direction.DOWN) {
            return serverPlayerEntity.getY();
        }

        return RotationUtil.vecWorldToPlayer(serverPlayerEntity.getPos(), gravityDirection).y;
    }

    @Redirect(
            method = "onPlayerMove",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/network/ServerPlayerEntity;getY()D",
                    ordinal = 7
            )
    )
    private double redirect_onPlayerMove_getY_7(ServerPlayerEntity serverPlayerEntity) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(serverPlayerEntity);
        if (gravityDirection == Direction.DOWN) {
            return serverPlayerEntity.getY();
        }

        return RotationUtil.vecWorldToPlayer(serverPlayerEntity.getPos(), gravityDirection).y;
    }

    @ModifyVariable(
            method = "onPlayerMove",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/network/ServerPlayerEntity;isOnGround()Z",
                    ordinal = 0
            ),
            ordinal = 0
    )
    private boolean modify_onPlayerMove_boolean_0(boolean value, PlayerMoveC2SPacket packet) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(this.player);
        if (gravityDirection == Direction.DOWN) {
            return value;
        }

        onPlayerMove_playerMovementY = RotationUtil.vecWorldToPlayer(
                clampHorizontal(packet.getX(this.player.getX())) - this.updatedX,
                clampVertical(packet.getY(this.player.getY())) - this.updatedY,
                clampHorizontal(packet.getZ(this.player.getZ())) - this.updatedZ,
                gravityDirection
        ).y;
        return onPlayerMove_playerMovementY > 0.0D;
    }

    @ModifyVariable(
            method = "onPlayerMove",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/network/ServerPlayerEntity;getX()D",
                    ordinal = 5
            ),
            ordinal = 10
    )
    private double modify_onPlayerMove_double_12(double value) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(this.player);
        if (gravityDirection == Direction.DOWN) {
            return value;
        }

        return onPlayerMove_playerMovementY;
    }

    @ModifyArg(
            method = "onPlayerMove",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/network/ServerPlayerEntity;move(Lnet/minecraft/entity/MovementType;Lnet/minecraft/util/math/Vec3d;)V",
                    ordinal = 0
            ),
            index = 1
    )
    private Vec3d modify_onPlayerMove_move_0(Vec3d vec3d) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(this.player);
        if (gravityDirection == Direction.DOWN) {
            return vec3d;
        }

        return RotationUtil.vecWorldToPlayer(vec3d, gravityDirection);
    }

    //@Redirect(
    //        method = "onVehicleMove",
    //        at = @At(
    //                value = "INVOKE",
    //                target = "Lnet/minecraft/entity/Entity;getY()D",
    //                ordinal = 0
    //        )
    //)
    //private double redirect_onVehicleMove_getY_0(Entity instance) {
    //    Direction gravityDirection = ((EntityAccessor) instance).gravitychanger$getAppliedGravityDirection();
    //    if(gravityDirection == Direction.DOWN) {
    //        return instance.getY();
    //    }
//
    //    return RotationUtil.vecWorldToPlayer(instance.getPos(), gravityDirection).y;
    //}
//
    //@Redirect(
    //        method = "onVehicleMove",
    //        at = @At(
    //                value = "INVOKE",
    //                target = "Lnet/minecraft/entity/Entity;getY()D",
    //                ordinal = 2
    //        )
    //)
    //private double redirect_onVehicleMove_getY_2(Entity instance) {
    //    Direction gravityDirection = ((EntityAccessor) instance).gravitychanger$getAppliedGravityDirection();
    //    if(gravityDirection == Direction.DOWN) {
    //        return instance.getY();
    //    }
//
    //    return RotationUtil.vecWorldToPlayer(instance.getPos(), gravityDirection).y;
    //}

    @ModifyArg(
            method = "onVehicleMove",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;move(Lnet/minecraft/entity/MovementType;Lnet/minecraft/util/math/Vec3d;)V"
            ),
            index = 1
    )
    private Vec3d modify_onVehicleMove_move_0(Vec3d vec3d) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(this.player);
        if (gravityDirection == Direction.DOWN) {
            return vec3d;
        }

        return RotationUtil.vecWorldToPlayer(vec3d, gravityDirection);
    }

    //@ModifyVariable(
    //        method = "onVehicleMove",
    //        at = @At(
    //                value = "INVOKE",
    //                target = "Lnet/minecraft/entity/Entity;getX()D",
    //                ordinal = 1
    //        ),ordinal = 0
    //)
    //private double modify_onVehicleMove_double_12(double value) {
    //    Direction gravityDirection = GravityChangerAPI.getGravityDirection(this.player);
    //    if(gravityDirection == Direction.DOWN) {
    //        return value;
    //    }
//
    //    return gravitychanger$onPlayerMove_playerMovementY;
    //}


    @Unique private double xx;
    @Unique private double yy;
    @Unique private double zz;

    @ModifyArg(
            method = "isEntityOnAir",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/Box;stretch(DDD)Lnet/minecraft/util/math/Box;"
            ),
            index = 0
    )
    private double modify_onVehicleMove_move_0(double x) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(this.player);
        this.xx = x;
        Vec3d argVec = new Vec3d(xx, yy, zz);
        argVec = RotationUtil.vecWorldToPlayer(argVec, gravityDirection);
        return argVec.x;
    }

    @ModifyArg(
            method = "isEntityOnAir",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/Box;stretch(DDD)Lnet/minecraft/util/math/Box;"
            ),
            index = 0
    )
    private double modify_onVehicleMove_move_1(double y) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(this.player);
        this.yy = y;
        Vec3d argVec = new Vec3d(xx, yy, zz);
        argVec = RotationUtil.vecWorldToPlayer(argVec, gravityDirection);
        return argVec.y;
    }
    @ModifyArg(
            method = "isEntityOnAir",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/Box;stretch(DDD)Lnet/minecraft/util/math/Box;"
            ),
            index = 0
    )
    private double modify_onVehicleMove_move_2(double z) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(this.player);
        this.zz = z;
        Vec3d argVec = new Vec3d(xx, yy, zz);
        argVec = RotationUtil.vecWorldToPlayer(argVec, gravityDirection);
        return argVec.z;
    }
}
