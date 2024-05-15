package net.arna.jcraft.mixin.gravity;

import net.arna.jcraft.common.gravity.api.GravityChangerAPI;
import net.arna.jcraft.common.gravity.util.RotationUtil;
import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {
    @Shadow
    @Final
    private PlayerAbilities abilities;

    @Shadow
    public abstract EntityDimensions getDimensions(EntityPose pose);

    @Shadow
    protected abstract boolean clipAtLedge();

    @Shadow
    protected abstract boolean method_30263();

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Redirect(
            method = "travel",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/PlayerEntity;getRotationVector()Lnet/minecraft/util/math/Vec3d;",
                    ordinal = 0
            )
    )
    private Vec3d redirect_travel_getRotationVector_0(PlayerEntity playerEntity) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(playerEntity);
        if (gravityDirection == Direction.DOWN) {
            return playerEntity.getRotationVector();
        }

        return RotationUtil.vecWorldToPlayer(playerEntity.getRotationVector(), gravityDirection);
    }


    @ModifyArg(
            method = "travel",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/World;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;"
            )
    )
    private BlockPos modify_move_multiply_0(BlockPos pos) {
        Vec3d rotate = new Vec3d(0.0D, 1.0D - 0.1D, 0.0D);
        rotate = RotationUtil.vecPlayerToWorld(rotate, GravityChangerAPI.getGravityDirection(this));
        return BlockPos.ofFloored(pos.getX() - rotate.x, pos.getY() - rotate.y + (1.0D - 0.1D), pos.getZ()- rotate.z);
    }


    @Redirect(
            method = "dropItem(Lnet/minecraft/item/ItemStack;ZZ)Lnet/minecraft/entity/ItemEntity;",
            at = @At(
                    value = "NEW",
                    target = "net/minecraft/entity/ItemEntity",
                    ordinal = 0
            )
    )
    private ItemEntity redirect_dropItem_new_0(World world, double x, double y, double z, ItemStack stack) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection((Entity) (Object) this);
        if (gravityDirection == Direction.DOWN) {
            return new ItemEntity(world, x, y, z, stack);
        }

        Vec3d vec3d = this.getEyePos().subtract(RotationUtil.vecPlayerToWorld(0.0D, 0.30000001192092896D, 0.0D, gravityDirection));

        return new ItemEntity(world, vec3d.x, vec3d.y, vec3d.z, stack);
    }

    @Redirect(
            method = "dropItem(Lnet/minecraft/item/ItemStack;ZZ)Lnet/minecraft/entity/ItemEntity;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/ItemEntity;setVelocity(DDD)V"
            )
    )
    private void redirect_dropItem_setVelocity(ItemEntity itemEntity, double x, double y, double z) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection((Entity) (Object) this);
        if (gravityDirection == Direction.DOWN) {
            itemEntity.setVelocity(x, y, z);
            return;
        }

        itemEntity.setVelocity(RotationUtil.vecPlayerToWorld(x, y, z, gravityDirection));
    }

    @Inject(
            method = "adjustMovementForSneaking",
            at = @At("HEAD"),
            cancellable = true
    )
    private void inject_adjustMovementForSneaking(Vec3d movement, MovementType type, CallbackInfoReturnable<Vec3d> cir) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection((Entity) (Object) this);
        if (gravityDirection == Direction.DOWN) {
            return;
        }

        Vec3d playerMovement = RotationUtil.vecWorldToPlayer(movement, gravityDirection);

        if (!this.abilities.flying && (type == MovementType.SELF || type == MovementType.PLAYER) && this.clipAtLedge() && this.method_30263()) {
            double d = playerMovement.x;
            double e = playerMovement.z;
            double var7 = 0.05D;

            while (d != 0.0D && this.getWorld().isSpaceEmpty(this, this.getBoundingBox().offset(RotationUtil.vecPlayerToWorld(d, (double) (-this.getStepHeight()), 0.0D, gravityDirection)))) {
                if (d < 0.05D && d >= -0.05D) {
                    d = 0.0D;
                } else if (d > 0.0D) {
                    d -= 0.05D;
                } else {
                    d += 0.05D;
                }
            }

            while (e != 0.0D && this.getWorld().isSpaceEmpty(this, this.getBoundingBox().offset(RotationUtil.vecPlayerToWorld(0.0D, (double) (-this.getStepHeight()), e, gravityDirection)))) {
                if (e < 0.05D && e >= -0.05D) {
                    e = 0.0D;
                } else if (e > 0.0D) {
                    e -= 0.05D;
                } else {
                    e += 0.05D;
                }
            }

            while (d != 0.0D && e != 0.0D && this.getWorld().isSpaceEmpty(this, this.getBoundingBox().offset(RotationUtil.vecPlayerToWorld(d, (double) (-this.getStepHeight()), e, gravityDirection)))) {
                if (d < 0.05D && d >= -0.05D) {
                    d = 0.0D;
                } else if (d > 0.0D) {
                    d -= 0.05D;
                } else {
                    d += 0.05D;
                }

                if (e < 0.05D && e >= -0.05D) {
                    e = 0.0D;
                } else if (e > 0.0D) {
                    e -= 0.05D;
                } else {
                    e += 0.05D;
                }
            }

            cir.setReturnValue(RotationUtil.vecPlayerToWorld(d, playerMovement.y, e, gravityDirection));
        } else {
            cir.setReturnValue(movement);
        }
    }

    @Redirect(
            method = "method_30263",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/Box;offset(DDD)Lnet/minecraft/util/math/Box;",
                    ordinal = 0
            )
    )
    private Box redirect_method_30263_offset_0(Box box, double x, double y, double z) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection((Entity) (Object) this);
        if (gravityDirection == Direction.DOWN) {
            return box.offset(x, y, z);
        }

        return box.offset(RotationUtil.vecPlayerToWorld(x, y, z, gravityDirection));
    }

    @Redirect(
            method = "attack",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/PlayerEntity;getYaw()F",
                    ordinal = 0
            )
    )
    private float redirect_attack_getYaw_0(PlayerEntity attacker, Entity target) {
        Direction targetGravityDirection = GravityChangerAPI.getGravityDirection(target);
        Direction attackerGravityDirection = GravityChangerAPI.getGravityDirection(attacker);
        if (targetGravityDirection == attackerGravityDirection) {
            return attacker.getYaw();
        }

        return RotationUtil.rotWorldToPlayer(RotationUtil.rotPlayerToWorld(attacker.getYaw(), attacker.getPitch(), attackerGravityDirection), targetGravityDirection).x;
    }

    @Redirect(
            method = "attack",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/PlayerEntity;getYaw()F",
                    ordinal = 1
            )
    )
    private float redirect_attack_getYaw_1(PlayerEntity attacker, Entity target) {
        Direction targetGravityDirection = GravityChangerAPI.getGravityDirection(target);
        Direction attackerGravityDirection = GravityChangerAPI.getGravityDirection(attacker);
        if (targetGravityDirection == attackerGravityDirection) {
            return attacker.getYaw();
        }

        return RotationUtil.rotWorldToPlayer(RotationUtil.rotPlayerToWorld(attacker.getYaw(), attacker.getPitch(), attackerGravityDirection), targetGravityDirection).x;
    }

    @Redirect(
            method = "attack",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/PlayerEntity;getYaw()F",
                    ordinal = 2
            )
    )
    private float redirect_attack_getYaw_2(PlayerEntity attacker) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(attacker);
        if (gravityDirection == Direction.DOWN) {
            return attacker.getYaw();
        }

        return RotationUtil.rotPlayerToWorld(attacker.getYaw(), attacker.getPitch(), gravityDirection).x;
    }

    @Redirect(
            method = "attack",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/PlayerEntity;getYaw()F",
                    ordinal = 3
            )
    )
    private float redirect_attack_getYaw_3(PlayerEntity attacker) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(attacker);
        if (gravityDirection == Direction.DOWN) {
            return attacker.getYaw();
        }

        return RotationUtil.rotPlayerToWorld(attacker.getYaw(), attacker.getPitch(), gravityDirection).x;
    }

    @ModifyArg(
            method = "spawnParticles",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/World;addParticle(Lnet/minecraft/particle/ParticleEffect;DDDDDD)V",
                    ordinal = 0
            ),
            index = 1
    )
    private double modify_addDeathParticless_addParticle_0(double x) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection((Entity) (Object) this);
        if (gravityDirection == Direction.DOWN) {
            return x;
        }
        Vec3d vec3d = this.getPos().subtract(RotationUtil.vecPlayerToWorld(this.getPos().subtract(this.getParticleX(1.0), this.getRandomBodyY() + 1.0, this.getParticleZ(1.0)), gravityDirection));
        return vec3d.x;
    }

    @ModifyArg(
            method = "spawnParticles",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/World;addParticle(Lnet/minecraft/particle/ParticleEffect;DDDDDD)V",
                    ordinal = 0
            ),
            index = 1
    )
    private double modify_addDeathParticless_addParticle_1(double y) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection((Entity) (Object) this);
        if (gravityDirection == Direction.DOWN) {
            return y;
        }
        Vec3d vec3d = this.getPos().subtract(RotationUtil.vecPlayerToWorld(this.getPos().subtract(this.getParticleX(1.0), this.getRandomBodyY() + 1.0, this.getParticleZ(1.0)), gravityDirection));
        return vec3d.y;
    }

    @ModifyArg(
            method = "spawnParticles",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/World;addParticle(Lnet/minecraft/particle/ParticleEffect;DDDDDD)V",
                    ordinal = 0
            ),
            index = 1
    )
    private double modify_addDeathParticless_addParticle_2(double z) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection((Entity) (Object) this);
        if (gravityDirection == Direction.DOWN) {
            return z;
        }
        Vec3d vec3d = this.getPos().subtract(RotationUtil.vecPlayerToWorld(this.getPos().subtract(this.getParticleX(1.0), this.getRandomBodyY() + 1.0, this.getParticleZ(1.0)), gravityDirection));
        return vec3d.z;
    }

    @ModifyArg(
            method = "tickMovement",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/Box;expand(DDD)Lnet/minecraft/util/math/Box;",
                    ordinal = 0
            ),
            index = 1
    )
    private double modify_tickMovement_expand_0(double x) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection((Entity) (Object) this);
        if (gravityDirection == Direction.DOWN) {
            return x;
        }

        Vec3d vec3d = RotationUtil.maskPlayerToWorld(1, 0, 1, gravityDirection);
        return vec3d.x;
    }

    @ModifyArg(
            method = "tickMovement",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/Box;expand(DDD)Lnet/minecraft/util/math/Box;",
                    ordinal = 0
            ),
            index = 1
    )
    private double modify_tickMovement_expand_1(double y) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection((Entity) (Object) this);
        if (gravityDirection == Direction.DOWN) {
            return y;
        }

        Vec3d vec3d = RotationUtil.maskPlayerToWorld(1, 0, 1, gravityDirection);
        return vec3d.y;
    }

    @ModifyArg(
            method = "tickMovement",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/Box;expand(DDD)Lnet/minecraft/util/math/Box;",
                    ordinal = 0
            ),
            index = 1
    )
    private double modify_tickMovement_expand_2(double z) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection((Entity) (Object) this);
        if (gravityDirection == Direction.DOWN) {
            return z;
        }

        Vec3d vec3d = RotationUtil.maskPlayerToWorld(1, 0, 1, gravityDirection);
        return vec3d.z;
    }

    @ModifyArg(
            method = "tickMovement",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/Box;expand(DDD)Lnet/minecraft/util/math/Box;",
                    ordinal = 1
            ),
            index = 1
    )
    private double modify_tickMovement_expand_3(double x) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection((Entity) (Object) this);
        if (gravityDirection == Direction.DOWN) {
            return x;
        }

        Vec3d vec3d = RotationUtil.maskPlayerToWorld(1, 0, 1, gravityDirection);
        return vec3d.x;
    }

    @ModifyArg(
            method = "tickMovement",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/Box;expand(DDD)Lnet/minecraft/util/math/Box;",
                    ordinal = 1
            ),
            index = 1
    )
    private double modify_tickMovement_expand_4(double y) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection((Entity) (Object) this);
        if (gravityDirection == Direction.DOWN) {
            return y;
        }

        Vec3d vec3d = RotationUtil.maskPlayerToWorld(1, 0, 1, gravityDirection);
        return vec3d.y;
    }

    @ModifyArg(
            method = "tickMovement",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/Box;expand(DDD)Lnet/minecraft/util/math/Box;",
                    ordinal = 1
            ),
            index = 1
    )
    private double modify_tickMovement_expand_5(double z) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection((Entity) (Object) this);
        if (gravityDirection == Direction.DOWN) {
            return z;
        }

        Vec3d vec3d = RotationUtil.maskPlayerToWorld(1, 0, 1, gravityDirection);
        return vec3d.z;
    }
}
