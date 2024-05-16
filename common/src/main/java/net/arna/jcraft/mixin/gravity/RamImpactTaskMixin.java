package net.arna.jcraft.mixin.gravity;


import net.arna.jcraft.common.gravity.api.GravityChangerAPI;
import net.arna.jcraft.common.gravity.util.RotationUtil;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.RamTarget;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(RamTarget.class)
public abstract class RamImpactTaskMixin {
    @Shadow
    private Vec3 ramDirection;

    @Redirect(
            method = "tick(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/animal/goat/Goat;J)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;knockback(DDD)V",
                    ordinal = 0
            )
    )
    private void redirect_keepRunning_takeKnockback_0(LivingEntity target, double strength, double x, double z) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(target);
        if (gravityDirection == Direction.DOWN) {
            target.knockback(strength, x, z);
            return;
        }

        Vec3 direction = RotationUtil.vecWorldToPlayer(this.ramDirection, gravityDirection);
        target.knockback(strength, direction.x, direction.z);
    }
}
