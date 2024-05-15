package net.arna.jcraft.mixin_logic;

import com.google.common.collect.ImmutableList;
import net.arna.jcraft.common.entity.stand.StandEntity;
import net.arna.jcraft.common.gravity.api.GravityChangerAPI;
import net.arna.jcraft.common.gravity.util.RotationUtil;
import net.arna.jcraft.common.util.JUtils;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

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

    public static void doNotPlayDesummonSoundWhenMovingWorld(Entity entity) {
        if (!(entity instanceof LivingEntity living)) {
            return;
        }

        StandEntity<?, ?> stand = JUtils.getStand(living);
        if (stand == null) {
            return;
        }

        stand.setPlayDesummonSound(false);
    }

    public static void inject_calculateBoundingBox(Entity entity, CallbackInfoReturnable<Box> cir) {
        if (entity instanceof ProjectileEntity) {
            return;
        }

        Direction gravityDirection = GravityChangerAPI.getGravityDirection(entity);
        if (gravityDirection == Direction.DOWN) {
            return;
        }

        Box box = cir.getReturnValue().offset(entity.getPos().negate());
        if (gravityDirection.getDirection() == Direction.AxisDirection.POSITIVE) {
            box = box.offset(0.0D, -1.0E-6D, 0.0D);
        }
        cir.setReturnValue(RotationUtil.boxPlayerToWorld(box, gravityDirection).offset(entity.getPos()));
    }

    public static void inject_calculateBoundsForPose(Entity entity, CallbackInfoReturnable<Box> cir) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(entity);
        if (gravityDirection == Direction.DOWN) {
            return;
        }

        Box box = cir.getReturnValue().offset(entity.getPos().negate());
        if (gravityDirection.getDirection() == Direction.AxisDirection.POSITIVE) {
            box = box.offset(0.0D, -1.0E-6D, 0.0D);
        }
        cir.setReturnValue(RotationUtil.boxPlayerToWorld(box, gravityDirection).offset(entity.getPos()));
    }

    public static void inject_getRotationVector(Entity entity, CallbackInfoReturnable<Vec3d> cir) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(entity);
        if (gravityDirection == Direction.DOWN) {
            return;
        }

        cir.setReturnValue(RotationUtil.vecPlayerToWorld(cir.getReturnValue(), gravityDirection));
    }

    public static void inject_getVelocityAffectingPos(Entity entity, CallbackInfoReturnable<BlockPos> cir) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(entity);
        if (gravityDirection == Direction.DOWN) {
            return;
        }

        cir.setReturnValue(BlockPos.ofFloored(entity.getPos().add(Vec3d.of(gravityDirection.getVector()).multiply(0.5000001D))));
    }

    public static void inject_getEyePos(Entity entity, CallbackInfoReturnable<Vec3d> cir) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(entity);
        if (gravityDirection == Direction.DOWN) {
            return;
        }

        cir.setReturnValue(RotationUtil.vecPlayerToWorld(0.0D, entity.getStandingEyeHeight(), 0.0D, gravityDirection).add(entity.getPos()));
    }

    public static void inject_getCameraPosVec(Entity entity, float tickDelta, CallbackInfoReturnable<Vec3d> cir) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(entity);
        if (gravityDirection == Direction.DOWN) {
            return;
        }

        Vec3d vec3d = RotationUtil.vecPlayerToWorld(0.0D, entity.getStandingEyeHeight(), 0.0D, gravityDirection);

        double d = MathHelper.lerp(tickDelta, entity.prevX, entity.getX()) + vec3d.x;
        double e = MathHelper.lerp(tickDelta, entity.prevY, entity.getY()) + vec3d.y;
        double f = MathHelper.lerp(tickDelta, entity.prevZ, entity.getZ()) + vec3d.z;
        cir.setReturnValue(new Vec3d(d, e, f));
    }

    public static void inject_getBrightnessAtFEyes(Entity entity, CallbackInfoReturnable<Float> cir) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(entity);
        if (gravityDirection == Direction.DOWN) {
            return;
        }

        cir.setReturnValue(entity.getWorld().isPosLoaded(entity.getBlockX(), entity.getBlockZ()) ? entity.getWorld().getBrightness(BlockPos.ofFloored(entity.getEyePos())) : 0.0F);
    }

    public static void inject_pushAwayFrom(Entity thisEntity, Entity entity, CallbackInfo ci) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(thisEntity);
        Direction otherGravityDirection = GravityChangerAPI.getGravityDirection(entity);

        if (gravityDirection == Direction.DOWN && otherGravityDirection == Direction.DOWN) {
            return;
        }

        ci.cancel();

        if (!thisEntity.isConnectedThroughVehicle(entity)) {
            if (!entity.noClip && !thisEntity.noClip) {
                Vec3d entityOffset = entity.getBoundingBox().getCenter().subtract(thisEntity.getBoundingBox().getCenter());

                {
                    Vec3d playerEntityOffset = RotationUtil.vecWorldToPlayer(entityOffset, gravityDirection);
                    double dx = playerEntityOffset.x;
                    double dz = playerEntityOffset.z;
                    double f = MathHelper.absMax(dx, dz);
                    if (f >= 0.009999999776482582D) {
                        f = Math.sqrt(f);
                        dx /= f;
                        dz /= f;
                        double g = 1.0D / f;
                        if (g > 1.0D) {
                            g = 1.0D;
                        }

                        dx *= g;
                        dz *= g;
                        dx *= 0.05000000074505806D;
                        dz *= 0.05000000074505806D;
                        if (!thisEntity.hasPassengers()) {
                            thisEntity.addVelocity(-dx, 0.0D, -dz);
                        }
                    }
                }

                {
                    Vec3d entityEntityOffset = RotationUtil.vecWorldToPlayer(entityOffset, otherGravityDirection);
                    double dx = entityEntityOffset.x;
                    double dz = entityEntityOffset.z;
                    double f = MathHelper.absMax(dx, dz);
                    if (f >= 0.009999999776482582D) {
                        f = Math.sqrt(f);
                        dx /= f;
                        dz /= f;
                        double g = 1.0D / f;
                        if (g > 1.0D) {
                            g = 1.0D;
                        }

                        dx *= g;
                        dz *= g;
                        dx *= 0.05000000074505806D;
                        dz *= 0.05000000074505806D;
                        if (!entity.hasPassengers()) {
                            entity.addVelocity(dx, 0.0D, dz);
                        }
                    }
                }
            }
        }
    }

    public static void redirect_adjustMovementForCollisions_adjustMovementForCollisions_0(@Nullable Entity entity, Vec3d movement,
                                                                                           Box entityBoundingBox, World world, List<VoxelShape> collisions, CallbackInfoReturnable<Vec3d> cir,
                                                                                           ImmutableList.Builder<VoxelShape> shapeListBuilder) {
        collisions = shapeListBuilder.build();
        Direction gravityDirection;
        if (entity == null || (gravityDirection = GravityChangerAPI.getGravityDirection(entity)) == Direction.DOWN) {
            return;
        }

        Vec3d playerMovement = RotationUtil.vecWorldToPlayer(movement, gravityDirection);
        double playerMovementX = playerMovement.x;
        double playerMovementY = playerMovement.y;
        double playerMovementZ = playerMovement.z;
        Direction directionX = RotationUtil.dirPlayerToWorld(Direction.EAST, gravityDirection);
        Direction directionY = RotationUtil.dirPlayerToWorld(Direction.UP, gravityDirection);
        Direction directionZ = RotationUtil.dirPlayerToWorld(Direction.SOUTH, gravityDirection);
        if (playerMovementY != 0.0D) {
            playerMovementY = VoxelShapes.calculateMaxOffset(directionY.getAxis(), entityBoundingBox, collisions, playerMovementY * directionY.getDirection().offset()) * directionY.getDirection().offset();
            if (playerMovementY != 0.0D) {
                entityBoundingBox = entityBoundingBox.offset(RotationUtil.vecPlayerToWorld(0.0D, playerMovementY, 0.0D, gravityDirection));
            }
        }

        boolean isZLargerThanX = Math.abs(playerMovementX) < Math.abs(playerMovementZ);
        if (isZLargerThanX && playerMovementZ != 0.0D) {
            playerMovementZ = VoxelShapes.calculateMaxOffset(directionZ.getAxis(), entityBoundingBox, collisions, playerMovementZ * directionZ.getDirection().offset()) * directionZ.getDirection().offset();
            if (playerMovementZ != 0.0D) {
                entityBoundingBox = entityBoundingBox.offset(RotationUtil.vecPlayerToWorld(0.0D, 0.0D, playerMovementZ, gravityDirection));
            }
        }

        if (playerMovementX != 0.0D) {
            playerMovementX = VoxelShapes.calculateMaxOffset(directionX.getAxis(), entityBoundingBox, collisions, playerMovementX * directionX.getDirection().offset()) * directionX.getDirection().offset();
            if (!isZLargerThanX && playerMovementX != 0.0D) {
                entityBoundingBox = entityBoundingBox.offset(RotationUtil.vecPlayerToWorld(playerMovementX, 0.0D, 0.0D, gravityDirection));
            }
        }

        if (!isZLargerThanX && playerMovementZ != 0.0D) {
            playerMovementZ = VoxelShapes.calculateMaxOffset(directionZ.getAxis(), entityBoundingBox, collisions, playerMovementZ * directionZ.getDirection().offset()) * directionZ.getDirection().offset();
        }

        cir.setReturnValue(RotationUtil.vecPlayerToWorld(playerMovementX, playerMovementY, playerMovementZ, gravityDirection));
    }

    public static void inject_spawnSprintingParticles(Entity entity, Random random, EntityDimensions dimensions, CallbackInfo ci) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(entity);
        if (gravityDirection == Direction.DOWN) {
            return;
        }

        ci.cancel();

        Vec3d floorPos = entity.getPos().subtract(RotationUtil.vecPlayerToWorld(0.0D, 0.20000000298023224D, 0.0D, gravityDirection));

        BlockPos blockPos = BlockPos.ofFloored(floorPos);
        BlockState blockState = entity.getWorld().getBlockState(blockPos);
        if (blockState.getRenderType() != BlockRenderType.INVISIBLE) {
            Vec3d particlePos = entity.getPos().add(RotationUtil.vecPlayerToWorld((random.nextDouble() - 0.5D) * (double) dimensions.width, 0.1D, (random.nextDouble() - 0.5D) * (double) dimensions.width, gravityDirection));
            Vec3d playerVelocity = entity.getVelocity();
            Vec3d particleVelocity = RotationUtil.vecPlayerToWorld(playerVelocity.x * -4.0D, 1.5D, playerVelocity.z * -4.0D, gravityDirection);
            entity.getWorld().addParticle(new BlockStateParticleEffect(ParticleTypes.BLOCK, blockState), particlePos.x, particlePos.y, particlePos.z, particleVelocity.x, particleVelocity.y, particleVelocity.z);
        }
    }
}
