package net.arna.jcraft.mixin.gravity;


import com.llamalad7.mixinextras.sugar.Local;
import net.arna.jcraft.common.gravity.api.GravityChangerAPI;
import net.arna.jcraft.common.gravity.util.RotationUtil;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.thrown.ThrownEntity;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(ThrownEntity.class)
public abstract class ThrownEntityMixin {

    @Shadow
    protected abstract float getGravity();

    /*@Override
    public Direction gravitychanger$getAppliedGravityDirection() {
        return GravityChangerAPI.getGravityDirection((ThrownEntity)(Object)this);
    }*/

    @ModifyVariable(
            method = "tick",
            at = @At(
                    value = "STORE"
            )
            , ordinal = 0
    )
    public Vec3d tick(Vec3d modify) {
        //if(this instanceof RotatableEntityAccessor) {
        modify = new Vec3d(modify.x, modify.y + this.getGravity(), modify.z);
        modify = RotationUtil.vecWorldToPlayer(modify, GravityChangerAPI.getGravityDirection((ThrownEntity) (Object) this));
        modify = new Vec3d(modify.x, modify.y - this.getGravity(), modify.z);
        modify = RotationUtil.vecPlayerToWorld(modify, GravityChangerAPI.getGravityDirection((ThrownEntity) (Object) this));
        // }
        return modify;
    }

    @ModifyArg(
            method = "<init>(Lnet/minecraft/entity/EntityType;Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/world/World;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/projectile/thrown/ThrownEntity;<init>(Lnet/minecraft/entity/EntityType;DDDLnet/minecraft/world/World;)V",
                    ordinal = 0
            ),
            index = 1
    )
    private static double modifyargs_init_init_0(double x, @Local LivingEntity owner) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(owner);
        if (gravityDirection == Direction.DOWN) {
            return x;
        }

        Vec3d pos = owner.getEyePos().subtract(RotationUtil.vecPlayerToWorld(0.0D, 0.10000000149011612D, 0.0D, gravityDirection));
        return pos.x;
    }

    @ModifyArg(
            method = "<init>(Lnet/minecraft/entity/EntityType;Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/world/World;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/projectile/thrown/ThrownEntity;<init>(Lnet/minecraft/entity/EntityType;DDDLnet/minecraft/world/World;)V",
                    ordinal = 0
            ),
            index = 1
    )
    private static double modifyargs_init_init_1(double y, @Local LivingEntity owner) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(owner);
        if (gravityDirection == Direction.DOWN) {
            return y;
        }

        Vec3d pos = owner.getEyePos().subtract(RotationUtil.vecPlayerToWorld(0.0D, 0.10000000149011612D, 0.0D, gravityDirection));
        return pos.y;
    }

    @ModifyArg(
            method = "<init>(Lnet/minecraft/entity/EntityType;Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/world/World;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/projectile/thrown/ThrownEntity;<init>(Lnet/minecraft/entity/EntityType;DDDLnet/minecraft/world/World;)V",
                    ordinal = 0
            ),
            index = 1
    )
    private static double modifyargs_init_init_2(double z, @Local LivingEntity owner) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(owner);
        if (gravityDirection == Direction.DOWN) {
            return z;
        }

        Vec3d pos = owner.getEyePos().subtract(RotationUtil.vecPlayerToWorld(0.0D, 0.10000000149011612D, 0.0D, gravityDirection));
        return pos.z;
    }

}
