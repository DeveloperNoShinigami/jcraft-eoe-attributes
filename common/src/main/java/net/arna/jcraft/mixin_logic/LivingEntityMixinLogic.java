package net.arna.jcraft.mixin_logic;

import net.arna.jcraft.common.gravity.api.GravityChangerAPI;
import net.arna.jcraft.common.gravity.util.RotationUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class LivingEntityMixinLogic {

    public static double redirect_travel_getY_0(LivingEntity livingEntity) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(livingEntity);
        if (gravityDirection == Direction.DOWN) {
            return livingEntity.getY();
        }

        return RotationUtil.vecWorldToPlayer(livingEntity.getPos(), gravityDirection).y;
    }

    public static Vec3d modify_travel_Vec3d_2(Entity entity, Vec3d vec3d) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(entity);
        if (gravityDirection == Direction.DOWN) {
            return vec3d;
        }

        return RotationUtil.vecWorldToPlayer(vec3d, gravityDirection);
    }

    public static BlockPos modify_playBlockFallSound_getBlockState_0(Entity entity, BlockPos blockPos, Vec3d thisVec3d) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(entity);
        if (gravityDirection == Direction.DOWN) {
            return blockPos;
        }

        return BlockPos.ofFloored(thisVec3d.add(RotationUtil.vecPlayerToWorld(0, -0.20000000298023224D, 0, gravityDirection)));
    }

    public static Vec3d redirect_canSee_new_0(Entity entity, double x, double y, double z, Vec3d eyePos) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(entity);
        if (gravityDirection == Direction.DOWN) {
            return new Vec3d(x, y, z);
        }

        return eyePos;
    }
}
