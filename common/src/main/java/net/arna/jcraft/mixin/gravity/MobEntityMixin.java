package net.arna.jcraft.mixin.gravity;


import net.arna.jcraft.common.gravity.api.GravityChangerAPI;
import net.arna.jcraft.common.gravity.util.RotationUtil;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Mob.class)
public abstract class MobEntityMixin {
    @Redirect(
            method = "doHurtTarget",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/Mob;getYRot()F",
                    ordinal = 0
            )
    )
    private float redirect_tryAttack_getYaw_0(Mob attacker, Entity target) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(target);
        if (gravityDirection == Direction.DOWN) {
            return attacker.getYRot();
        }

        return RotationUtil.rotWorldToPlayer(attacker.getYRot(), attacker.getXRot(), gravityDirection).x;
    }

    @Redirect(
            method = "doHurtTarget",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/Mob;getYRot()F",
                    ordinal = 1
            )
    )
    private float redirect_tryAttack_getYaw_1(Mob attacker, Entity target) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(target);
        if (gravityDirection == Direction.DOWN) {
            return attacker.getYRot();
        }

        return RotationUtil.rotWorldToPlayer(attacker.getYRot(), attacker.getXRot(), gravityDirection).x;
    }

    @Redirect(
            method = "lookAt",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;getEyeY()D",
                    ordinal = 0
            )
    )
    private double redirect_lookAtEntity_getEyeY_0(LivingEntity livingEntity) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(livingEntity);
        if (gravityDirection == Direction.DOWN) {
            return livingEntity.getEyeY();
        }

        return livingEntity.getEyePosition().y;
    }

    @Redirect(
            method = "lookAt",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/Entity;getX()D",
                    ordinal = 0
            )
    )
    private double redirect_lookAtEntity_getX_0(Entity entity) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(entity);
        if (gravityDirection == Direction.DOWN) {
            return entity.getX();
        }

        return entity.getEyePosition().x;
    }

    @Redirect(
            method = "lookAt",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/Entity;getZ()D",
                    ordinal = 0
            )
    )
    private double redirect_lookAtEntity_getZ_0(Entity entity) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(entity);
        if (gravityDirection == Direction.DOWN) {
            return entity.getZ();
        }

        return entity.getEyePosition().z;
    }
}
