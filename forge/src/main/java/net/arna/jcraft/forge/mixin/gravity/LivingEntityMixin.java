package net.arna.jcraft.forge.mixin.gravity;


import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.arna.jcraft.common.gravity.api.GravityChangerAPI;
import net.arna.jcraft.common.gravity.util.RotationUtil;
import net.arna.jcraft.mixin_logic.LivingEntityMixinLogic;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(value = LivingEntity.class, remap = false)
public abstract class LivingEntityMixin extends Entity {
    @Shadow
    public abstract void readAdditionalSaveData(CompoundTag nbt);

    @Shadow
    public abstract EntityDimensions getDimensions(Pose pose);

    @Shadow
    public abstract float getViewYRot(float tickDelta);


    public LivingEntityMixin(EntityType<?> type, Level world) {
        super(type, world);
    }

    @Redirect(
            method = "travel",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;getY()D",
                    ordinal = 0
            )
    )
    private double redirect_travel_getY_0(LivingEntity livingEntity) {
        return LivingEntityMixinLogic.redirect_travel_getY_0(livingEntity);
    }

    @Redirect(
            method = "travel",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;getY()D",
                    ordinal = 1
            )
    )
    private double redirect_travel_getY_1(LivingEntity livingEntity) {
        return LivingEntityMixinLogic.redirect_travel_getY_0(livingEntity);
    }

    @Redirect(
            method = "travel",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;getY()D",
                    ordinal = 2
            )
    )
    private double redirect_travel_getY_2(LivingEntity livingEntity) {
        return LivingEntityMixinLogic.redirect_travel_getY_0(livingEntity);
    }

    @Redirect(
            method = "travel",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;getY()D",
                    ordinal = 3
            )
    )
    private double redirect_travel_getY_3(LivingEntity livingEntity) {
        return LivingEntityMixinLogic.redirect_travel_getY_0(livingEntity);
    }

    @ModifyVariable(
            method = "travel",
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "Lnet/minecraft/world/entity/LivingEntity;handleRelativeFrictionAndCalculateMovement(Lnet/minecraft/world/phys/Vec3;F)Lnet/minecraft/world/phys/Vec3;"
            ),
            argsOnly = true)
    private Vec3 modify_travel_Vec3d_2(Vec3 vec3d) {
        return LivingEntityMixinLogic.modify_travel_Vec3d_2(this, vec3d);
    }

    @ModifyArg(
            method = "playBlockFallSound",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;",
                    ordinal = 0
            ),
            index = 0
    )
    private BlockPos modify_playBlockFallSound_getBlockState_0(BlockPos blockPos) {
        return LivingEntityMixinLogic.modify_playBlockFallSound_getBlockState_0(this, blockPos, this.position());
    }

    @Redirect(
            method = "hasLineOfSight",
            at = @At(
                    value = "NEW",
                    target = "(DDD)Lnet/minecraft/world/phys/Vec3;",
                    ordinal = 0
            )
    )
    private Vec3 redirect_canSee_new_0(double x, double y, double z) {
        return LivingEntityMixinLogic.redirect_canSee_new_0(this, x, y, z, this.getEyePosition());
    }

    @Redirect(
            method = "hasLineOfSight",
            at = @At(
                    value = "NEW",
                    target = "(DDD)Lnet/minecraft/world/phys/Vec3;",
                    ordinal = 1
            )
    )
    private Vec3 redirect_canSee_new_1(double x, double y, double z, Entity entity) {
        return LivingEntityMixinLogic.redirect_canSee_new_0(this, x, y, z, this.getEyePosition());
    }

    @Inject(
            method = "getLocalBoundsForPose",
            at = @At("RETURN"),
            cancellable = true
    )
    private void inject_getBoundingBox(Pose pose, CallbackInfoReturnable<AABB> cir) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection((Entity) this);
        if (gravityDirection == Direction.DOWN) {
            return;
        }

        AABB box = cir.getReturnValue();
        if (gravityDirection.getAxisDirection() == Direction.AxisDirection.POSITIVE) {
            box = box.move(0.0D, -1.0E-6D, 0.0D);
        }
        cir.setReturnValue(RotationUtil.boxPlayerToWorld(box, gravityDirection));
    }

    @Inject(
            method = "calculateEntityAnimation",
            at = @At("HEAD"),
            cancellable = true
    )
    private void inject_updateLimbs(boolean flutter, CallbackInfo ci) {
        LivingEntity entity = LivingEntity.class.cast(this);

        Direction gravityDirection = GravityChangerAPI.getGravityDirection(entity);
        if (gravityDirection == Direction.DOWN) {
            return;
        }

        ci.cancel();

        Vec3 playerPosDelta = RotationUtil.vecWorldToPlayer(entity.getX() - entity.xo, entity.getY() - entity.yo, entity.getZ() - entity.zo, gravityDirection);

        //TODO ? entity.lastLimbDistance = entity.limbDistance;
        double d = playerPosDelta.x;
        double e = flutter ? playerPosDelta.y : 0.0D;
        double f = playerPosDelta.z;
        float g = (float) Math.sqrt(d * d + e * e + f * f) * 4.0F;
        if (g > 1.0F) {
            g = 1.0F;
        }

        //TODO ? entity.limbDistance += (g - entity.limbDistance) * 0.4F;
        //TODO ? entity.limbAngle += entity.limbDistance;
    }

    @Redirect(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;getX()D",
                    ordinal = 0
            )
    )
    private double redirect_tick_getX_0(LivingEntity livingEntity) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(livingEntity);
        if (gravityDirection == Direction.DOWN) {
            return livingEntity.getX();
        }

        return RotationUtil.vecWorldToPlayer(livingEntity.getX() - livingEntity.xo, livingEntity.getY() - livingEntity.yo, livingEntity.getZ() - livingEntity.zo, gravityDirection).x + livingEntity.xo;
    }

    @Redirect(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;getZ()D",
                    ordinal = 0
            )
    )
    private double redirect_tick_getZ_0(LivingEntity livingEntity) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(livingEntity);
        if (gravityDirection == Direction.DOWN) {
            return livingEntity.getZ();
        }

        return RotationUtil.vecWorldToPlayer(livingEntity.getX() - livingEntity.xo, livingEntity.getY() - livingEntity.yo, livingEntity.getZ() - livingEntity.zo, gravityDirection).z + livingEntity.zo;
    }

    @Redirect(
            method = "hurt",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/Entity;getX()D",
                    ordinal = 0
            )
    )
    private double redirect_damage_getX_0(Entity attacker) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection((Entity) this);
        if (gravityDirection == Direction.DOWN) {
            if (GravityChangerAPI.getGravityDirection(attacker) == Direction.DOWN) {
                return attacker.getX();
            } else {
                return attacker.getEyePosition().x;
            }
        }

        return RotationUtil.vecWorldToPlayer(attacker.getEyePosition(), gravityDirection).x;
    }



    @Redirect(
            method = "hurt",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/Entity;getZ()D",
                    ordinal = 0
            )
    )
    private double redirect_damage_getZ_0(Entity attacker) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection((Entity) this);
        if (gravityDirection == Direction.DOWN) {
            if (GravityChangerAPI.getGravityDirection(attacker) == Direction.DOWN) {
                return attacker.getZ();
            } else {
                return attacker.getEyePosition().z;
            }
        }

        return RotationUtil.vecWorldToPlayer(attacker.getEyePosition(), gravityDirection).z;
    }

    @Redirect(
            method = "hurt",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;getX()D",
                    ordinal = 0
            )
    )
    private double redirect_damage_getX_0(LivingEntity target) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(target);
        if (gravityDirection == Direction.DOWN) {
            return target.getX();
        }

        return RotationUtil.vecWorldToPlayer(target.position(), gravityDirection).x;
    }

    @Redirect(
            method = "hurt",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;getZ()D",
                    ordinal = 0
            )
    )
    private double redirect_damage_getZ_0(LivingEntity target) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(target);
        if (gravityDirection == Direction.DOWN) {
            return target.getZ();
        }

        return RotationUtil.vecWorldToPlayer(target.position(), gravityDirection).z;
    }
/*TODO mojmap
    @Redirect(
            method = "blockedByShield",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;getX()D"
            )
    )
    private double redirect_knockback_getX_0(LivingEntity target) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(target);
        if (gravityDirection == Direction.DOWN) {
            return target.getX();
        }

        return RotationUtil.vecWorldToPlayer(target.position(), gravityDirection).x;
    }


    @Redirect(
            method = "blockedByShield",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;getZ()D",
                    ordinal = 0
            )
    )
    private double redirect_knockback_getZ_0(LivingEntity target) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(target);
        if (gravityDirection == Direction.DOWN) {
            return target.getZ();
        }

        return RotationUtil.vecWorldToPlayer(target.position(), gravityDirection).z;
    }

    @Redirect(
            method = "blockedByShield",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;getX()D",
                    ordinal = 1
            )
    )
    private double redirect_knockback_getX_1(LivingEntity attacker, LivingEntity target) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(target);
        if (gravityDirection == Direction.DOWN) {
            if (GravityChangerAPI.getGravityDirection(attacker) == Direction.DOWN) {
                return attacker.getX();
            } else {
                return attacker.getEyePosition().x;
            }
        }

        return RotationUtil.vecWorldToPlayer(attacker.getEyePosition(), gravityDirection).x;
    }

    @Redirect(
            method = "blockedByShield",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;getZ()D",
                    ordinal = 1
            )
    )
    private double redirect_knockback_getZ_1(LivingEntity attacker, LivingEntity target) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(target);
        if (gravityDirection == Direction.DOWN) {
            if (GravityChangerAPI.getGravityDirection(attacker) == Direction.DOWN) {
                return attacker.getZ();
            } else {
                return attacker.getEyePosition().z;
            }
        }

        return RotationUtil.vecWorldToPlayer(attacker.getEyePosition(), gravityDirection).z;
    }



 */


    @WrapOperation(
            method = "baseTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;blockPosition()Lnet/minecraft/core/BlockPos;"
            )
    )
    private BlockPos redirect_baseTick_new_0(LivingEntity instance, Operation<BlockPos> original) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection((Entity) this);
        if (gravityDirection == Direction.DOWN) {
            return BlockPos.containing(instance.getX(), instance.getY(), instance.getZ());
        }

        return original.call(instance);
    }



    @Redirect(
            method = "spawnItemParticles",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/phys/Vec3;add(DDD)Lnet/minecraft/world/phys/Vec3;",
                    ordinal = 0
            )
    )
    private Vec3 redirect_spawnItemParticles_add_0(Vec3 vec3d, double x, double y, double z) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection((Entity) this);
        if (gravityDirection == Direction.DOWN) {
            return vec3d.add(x, y, z);
        }

        return this.getEyePosition().add(RotationUtil.vecPlayerToWorld(vec3d, gravityDirection));
    }

    @ModifyVariable(
            method = "spawnItemParticles",
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "Lnet/minecraft/world/entity/LivingEntity;getYRot()F",
                    ordinal = 0
            ),
            ordinal = 0
    )
    private Vec3 modify_spawnItemParticles_Vec3d_0(Vec3 vec3d) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection((Entity) this);
        if (gravityDirection == Direction.DOWN) {
            return vec3d;
        }

        return RotationUtil.vecPlayerToWorld(vec3d, gravityDirection);
    }

    @ModifyArg(
            method = "tickEffects",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V",
                    ordinal = 0
            ),
            index = 1
    )
    private double modify_tickStatusEffects_addParticle_0(double x) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection((Entity) this);
        if (gravityDirection == Direction.DOWN) {
            return x;
        }

        Vec3 vec3d = this.position().subtract(RotationUtil.vecPlayerToWorld(this.position().subtract(x, 0, 0), gravityDirection));

        return vec3d.x;
    }

    @ModifyArg(
            method = "tickEffects",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V",
                    ordinal = 0
            ),
            index = 2
    )
    private double modify_tickStatusEffects_addParticle_1(double y) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection((Entity) this);
        if (gravityDirection == Direction.DOWN) {
            return y;
        }

        Vec3 vec3d = this.position().subtract(RotationUtil.vecPlayerToWorld(this.position().subtract(0, y, 0), gravityDirection));

        return vec3d.y;
    }

    @ModifyArg(
            method = "tickEffects",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V",
                    ordinal = 0
            ),
            index = 3
    )
    private double modify_tickStatusEffects_addParticle_2(double z) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection((Entity) this);
        if (gravityDirection == Direction.DOWN) {
            return z;
        }

        Vec3 vec3d = this.position().subtract(RotationUtil.vecPlayerToWorld(this.position().subtract(0, 0, z), gravityDirection));

        return vec3d.z;
    }

    @ModifyArg(
            method = "makePoofParticles",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V",
                    ordinal = 0
            ),
            index = 1
    )
    private double modify_addDeathParticless_addParticle_0(double x) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection((Entity) this);
        if (gravityDirection == Direction.DOWN) {
            return x;
        }

        Vec3 vec3d = this.position().subtract(RotationUtil.vecPlayerToWorld(this.position().subtract(x, 0, 0), gravityDirection));
        return vec3d.x;
    }

    @ModifyArg(
            method = "makePoofParticles",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V",
                    ordinal = 0
            ),
            index = 2
    )
    private double modify_addDeathParticless_addParticle_1(double y) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection((Entity) this);
        if (gravityDirection == Direction.DOWN) {
            return y;
        }

        Vec3 vec3d = this.position().subtract(RotationUtil.vecPlayerToWorld(this.position().subtract(0, y, 0), gravityDirection));
        return vec3d.y;
    }

    @ModifyArg(
            method = "makePoofParticles",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V",
                    ordinal = 0
            ),
            index = 3
    )
    private double modify_addDeathParticless_addParticle_2(double z) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection((Entity) this);
        if (gravityDirection == Direction.DOWN) {
            return z;
        }

        Vec3 vec3d = this.position().subtract(RotationUtil.vecPlayerToWorld(this.position().subtract(0, 0, z), gravityDirection));
        return vec3d.z;
    }

    @ModifyVariable(
            method = "isDamageSourceBlocked",
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "Lnet/minecraft/world/entity/LivingEntity;getViewVector(F)Lnet/minecraft/world/phys/Vec3;",
                    ordinal = 0
            ),
            ordinal = 1
    )
    private Vec3 modify_blockedByShield_Vec3d_1(Vec3 vec3d) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection((Entity) this);
        if (gravityDirection == Direction.DOWN) {
            return vec3d;
        }

        return RotationUtil.vecWorldToPlayer(vec3d, gravityDirection);
    }

    @ModifyArg(
            method = "isDamageSourceBlocked",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/phys/Vec3;vectorTo(Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/Vec3;",
                    ordinal = 0
            ),
            index = 0
    )
    private Vec3 modify_blockedByShield_relativize_0(Vec3 vec3d) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection((Entity) this);
        if (gravityDirection == Direction.DOWN) {
            return vec3d;
        }

        return this.getEyePosition();
    }

    @ModifyVariable(
            method = "isDamageSourceBlocked",
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "Lnet/minecraft/world/phys/Vec3;normalize()Lnet/minecraft/world/phys/Vec3;",
                    ordinal = 0
            ),
            ordinal = 2
    )
    private Vec3 modify_blockedByShield_Vec3d_2(Vec3 vec3d) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection((Entity) this);
        if (gravityDirection == Direction.DOWN) {
            return vec3d;
        }

        return RotationUtil.vecWorldToPlayer(vec3d, gravityDirection);
    }
}
