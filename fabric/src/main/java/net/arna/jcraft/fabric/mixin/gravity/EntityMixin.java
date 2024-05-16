package net.arna.jcraft.fabric.mixin.gravity;

import com.google.common.collect.ImmutableList;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.gravity.api.GravityChangerAPI;
import net.arna.jcraft.common.gravity.util.RotationUtil;
import net.arna.jcraft.mixin_logic.EntityMixinLogic;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.List;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Shadow
    @Final
    protected RandomSource random;

    @Shadow public abstract void lerpTo(double x, double y, double z, float yRot, float xRot, int lerpSteps, boolean teleport);

    @Shadow public abstract Vec3 position();

    @Shadow public abstract float getXRot();

    @Shadow private EntityDimensions dimensions;

    @Shadow private Level level;

    @Shadow public abstract double getY();

    @Shadow protected abstract void onBelowWorld();

    @Shadow public abstract Vec3 getEyePosition();

    @Inject(
            method = "makeBoundingBox",
            at = @At("RETURN"),
            cancellable = true
    )
    private void inject_calculateBoundingBox(CallbackInfoReturnable<AABB> cir) {
        EntityMixinLogic.inject_calculateBoundingBox((Entity) (Object) this, cir);
    }

    @Inject(
            method = "getBoundingBoxForPose",
            at = @At("RETURN"),
            cancellable = true
    )
    private void inject_calculateBoundsForPose(Pose pos, CallbackInfoReturnable<AABB> cir) {
        EntityMixinLogic.inject_calculateBoundsForPose((Entity) (Object) this, cir);
    }

    @Inject(
            method = "calculateViewVector",
            at = @At("RETURN"),
            cancellable = true
    )
    private void inject_getRotationVector(CallbackInfoReturnable<Vec3> cir) {
        EntityMixinLogic.inject_getRotationVector((Entity) (Object) this, cir);
    }

    @Inject(
            method = "getBlockPosBelowThatAffectsMyMovement",
            at = @At("HEAD"),
            cancellable = true
    )
    private void inject_getVelocityAffectingPos(CallbackInfoReturnable<BlockPos> cir) {
        EntityMixinLogic.inject_getVelocityAffectingPos((Entity) (Object) this, cir);
    }

    @Inject(
            method = "getEyePosition",
            at = @At("HEAD"),
            cancellable = true
    )
    private void inject_getEyePos(CallbackInfoReturnable<Vec3> cir) {
        EntityMixinLogic.inject_getEyePos((Entity) (Object) this, cir);
    }

    @Inject(
            method = "getEyePosition(F)Lnet/minecraft/world/phys/Vec3;",
            at = @At("HEAD"),
            cancellable = true
    )
    private void inject_getCameraPosVec(float tickDelta, CallbackInfoReturnable<Vec3> cir) {
        EntityMixinLogic.inject_getCameraPosVec((Entity) (Object) this, tickDelta, cir);
    }

    @Inject(
            method = "getLightLevelDependentMagicValue",
            at = @At("HEAD"),
            cancellable = true
    )
    private void inject_getBrightnessAtFEyes(CallbackInfoReturnable<Float> cir) {
        EntityMixinLogic.inject_getBrightnessAtFEyes((Entity) (Object) this, cir);
    }

    @ModifyVariable(
            method = "move",
            at = @At("HEAD"),
            ordinal = 0
    )
    private Vec3 modify_move_Vec3d_0_0(Vec3 vec3d) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection((Entity) (Object) this);
        if (gravityDirection == Direction.DOWN) {
            return vec3d;
        }

        return RotationUtil.vecPlayerToWorld(vec3d, gravityDirection);
    }


    @ModifyArg(
            method = "move",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/phys/Vec3;multiply(Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/Vec3;"
            ),
            index = 0
    )
    private Vec3 modify_move_multiply_0(Vec3 vec3d) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection((Entity) (Object) this);
        if (gravityDirection == Direction.DOWN) {
            return vec3d;
        }

        return RotationUtil.maskPlayerToWorld(vec3d, gravityDirection);
    }

    @ModifyVariable(
            method = "move",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/profiling/ProfilerFiller;pop()V"
            ),
            ordinal = 0
    )
    private Vec3 modify_move_Vec3d_0_1(Vec3 vec3d) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection((Entity) (Object) this);
        if (gravityDirection == Direction.DOWN) {
            return vec3d;
        }

        return RotationUtil.vecWorldToPlayer(vec3d, gravityDirection);
    }

    @ModifyVariable(
            method = "move",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/profiling/ProfilerFiller;pop()V"
            ),
            ordinal = 1
    )
    private Vec3 modify_move_Vec3d_1(Vec3 vec3d) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection((Entity) (Object) this);
        if (gravityDirection == Direction.DOWN) {
            return vec3d;
        }

        return RotationUtil.vecWorldToPlayer(vec3d, gravityDirection);
    }

    @Inject(
            method = "getOnPosLegacy",
            at = @At("HEAD"),
            cancellable = true
    )
    private void inject_getLandingPos(CallbackInfoReturnable<BlockPos> cir) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection((Entity) (Object) this);
        if (gravityDirection == Direction.DOWN) {
            return;
        }
        BlockPos blockPos = BlockPos.containing(RotationUtil.vecPlayerToWorld(0.0D, -0.20000000298023224D, 0.0D, gravityDirection).add(this.position()));
        cir.setReturnValue(blockPos);
    }

    @ModifyVariable(
            method = "collide",
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "Lnet/minecraft/world/level/Level;getEntityCollisions(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/AABB;)Ljava/util/List;"
            ),
            ordinal = 0
    )
    private Vec3 modify_adjustMovementForCollisions_Vec3d_0(Vec3 vec3d) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection((Entity) (Object) this);
        if (gravityDirection == Direction.DOWN) {
            return vec3d;
        }

        return RotationUtil.vecWorldToPlayer(vec3d, gravityDirection);
    }

    @Inject(
            method = "collide",
            at = @At("RETURN"),
            cancellable = true
    )
    private void inject_adjustMovementForCollisions(CallbackInfoReturnable<Vec3> cir) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection((Entity) (Object) this);
        if (gravityDirection == Direction.DOWN) {
            return;
        }

        cir.setReturnValue(RotationUtil.vecPlayerToWorld(cir.getReturnValue(), gravityDirection));
    }

    @ModifyArgs(
            method = "collide",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/phys/AABB;expandTowards(DDD)Lnet/minecraft/world/phys/AABB;"
            )
    )
    private void redirect_adjustMovementForCollisions_stretch_0(Args args) {
        Vec3 rotate = new Vec3(args.get(0), args.get(1), args.get(2));
        rotate = RotationUtil.vecPlayerToWorld(rotate, GravityChangerAPI.getGravityDirection((Entity) (Object) this));
        args.set(0, rotate.x);
        args.set(1, rotate.y);
        args.set(2, rotate.z);
    }

    @ModifyArgs(
            method = "collide",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/phys/AABB;move(Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/AABB;"
            )
    )
    private void redirect_adjustMovementForCollisions_offset_0(Args args) {
        Vec3 rotate = args.get(0);
        rotate = RotationUtil.vecPlayerToWorld(rotate, GravityChangerAPI.getGravityDirection((Entity) (Object) this));
        args.set(0, rotate);
    }

    @ModifyArgs(
            method = "collide",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/phys/AABB;move(Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/AABB;",
                    ordinal = 1
            )
    )
    private void redirect_adjustMovementForCollisions_offset_1(Args args) {
        Vec3 rotate = args.get(0);
        rotate = RotationUtil.vecPlayerToWorld(rotate, GravityChangerAPI.getGravityDirection((Entity) (Object) this));
        args.set(0, rotate);
    }

    @ModifyVariable(
            method = "collideBoundingBox",
            at = @At("HEAD"),
            ordinal = 0
    )
    private static Vec3 modify_adjustMovementForCollisions_Vec3d_0(Vec3 vec3d, Entity entity) {
        if (entity == null) {
            return vec3d;
        }

        Direction gravityDirection = GravityChangerAPI.getGravityDirection(entity);
        if (gravityDirection == Direction.DOWN) {
            return vec3d;
        }

        return RotationUtil.vecPlayerToWorld(vec3d, gravityDirection);
    }

    @Inject(
            method = "collideBoundingBox",
            at = @At("RETURN"),
            cancellable = true
    )
    private static void inject_adjustMovementForCollisions(Entity entity, Vec3 movement, AABB entityBoundingBox, Level world, List<VoxelShape> collisions, CallbackInfoReturnable<Vec3> cir) {
        if (entity == null) {
            return;
        }

        Direction gravityDirection = GravityChangerAPI.getGravityDirection(entity);
        if (gravityDirection == Direction.DOWN) {
            return;
        }

        cir.setReturnValue(RotationUtil.vecWorldToPlayer(cir.getReturnValue(), gravityDirection));
    }

    @SuppressWarnings("ParameterCanBeLocal")
    @Inject(
            method = "collideBoundingBox",
            at = @At("RETURN"),
            cancellable = true,
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private static void redirect_adjustMovementForCollisions_adjustMovementForCollisions_0(@Nullable Entity entity, Vec3 movement,
                                                                                           AABB entityBoundingBox, Level world, List<VoxelShape> collisions, CallbackInfoReturnable<Vec3> cir,
                                                                                           ImmutableList.Builder<VoxelShape> shapeListBuilder) {
        EntityMixinLogic.redirect_adjustMovementForCollisions_adjustMovementForCollisions_0(entity, movement, entityBoundingBox, world, collisions, cir, shapeListBuilder);
    }

    @ModifyArgs(
            method = "isInWall",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/phys/AABB;ofSize(Lnet/minecraft/world/phys/Vec3;DDD)Lnet/minecraft/world/phys/AABB;"
            )
    )
    private void modify_isInsideWall_of_0(Args args) {
        Vec3 rotate = new Vec3(args.get(1), args.get(2), args.get(3));
        rotate = RotationUtil.vecPlayerToWorld(rotate, GravityChangerAPI.getGravityDirection((Entity) (Object) this));
        args.set(1, rotate.x);
        args.set(2, rotate.y);
        args.set(3, rotate.z);
    }

    @ModifyArg(
            method = "getDirection",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/core/Direction;fromYRot(D)Lnet/minecraft/core/Direction;"
            )
    )
    private double redirect_getHorizontalFacing_getYaw_0(double rotation) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection((Entity) (Object) this);
        if (gravityDirection == Direction.DOWN) {
            return rotation;
        }

        return RotationUtil.rotPlayerToWorld((float) rotation, this.getXRot(), gravityDirection).x;
    }

    @Inject(
            method = "spawnSprintParticle",
            at = @At("HEAD"),
            cancellable = true
    )
    private void inject_spawnSprintingParticles(CallbackInfo ci) {
        EntityMixinLogic.inject_spawnSprintingParticles((Entity) (Object) this, this.random, this.dimensions, ci);
    }

    @ModifyVariable(
            method = "updateFluidHeightAndDoFluidPushing",
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "Lnet/minecraft/world/entity/Entity;getDeltaMovement()Lnet/minecraft/world/phys/Vec3;"
            ),
            ordinal = 0
    )
    private Vec3 modify_updateMovementInFluid_Vec3d_0(Vec3 vec3d) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection((Entity) (Object) this);
        if (gravityDirection == Direction.DOWN) {
            return vec3d;
        }

        return RotationUtil.vecPlayerToWorld(vec3d, gravityDirection);
    }

    @ModifyArg(
            method = "updateFluidHeightAndDoFluidPushing",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/phys/Vec3;add(Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/Vec3;",
                    ordinal = 1
            ),
            index = 0
    )
    private Vec3 modify_updateMovementInFluid_add_0(Vec3 vec3d) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection((Entity) (Object) this);
        if (gravityDirection == Direction.DOWN) {
            return vec3d;
        }

        return RotationUtil.vecWorldToPlayer(vec3d, gravityDirection);
    }


    @Inject(
            method = "push",
            at = @At("HEAD"),
            cancellable = true
    )
    private void inject_pushAwayFrom(Entity entity, CallbackInfo ci) {
        EntityMixinLogic.inject_pushAwayFrom((Entity) (Object) this, entity, ci);
    }

    @Inject(
            method = "checkBelowWorld",
            at = @At("HEAD")
    )
    private void inject_attemptTickInVoid(CallbackInfo ci) {
        if (JCraft.gravityConfig.voidDamageAboveWorld && this.getY() > (double) (this.level.getMaxBuildHeight() + 256)) {
            this.onBelowWorld();
        }
    }

    @ModifyArgs(
            method = "isFree(DDD)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/phys/AABB;move(DDD)Lnet/minecraft/world/phys/AABB;"
            )
    )
    private void redirect_doesNotCollide_offset_0(Args args) {
        Vec3 rotate = new Vec3(args.get(0), args.get(1), args.get(2));
        rotate = RotationUtil.vecPlayerToWorld(rotate, GravityChangerAPI.getGravityDirection((Entity) (Object) this));
        args.set(0, rotate.x);
        args.set(1, rotate.y);
        args.set(2, rotate.z);
    }


    @SuppressWarnings("ParameterCanBeLocal")
    @ModifyVariable(
            method = "updateFluidOnEyes",
            at = @At(
                    value = "STORE"
            ),
            ordinal = 0
    )
    private double submergedInWaterEyeFix(double d) {
        d = this.getEyePosition().y();
        return d;
    }

    @SuppressWarnings("ParameterCanBeLocal")
    @ModifyVariable(
            method = "updateFluidOnEyes",
            at = @At(
                    value = "STORE"
            ),
            ordinal = 0
    )
    private BlockPos submergedInWaterPosFix(BlockPos blockpos) {
        blockpos = BlockPos.containing(this.getEyePosition());
        return blockpos;
    }


}
