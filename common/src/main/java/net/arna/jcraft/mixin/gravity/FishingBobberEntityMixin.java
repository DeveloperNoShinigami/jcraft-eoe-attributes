package net.arna.jcraft.mixin.gravity;


import net.arna.jcraft.common.gravity.api.GravityChangerAPI;
import net.arna.jcraft.common.gravity.util.RotationUtil;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FishingHook.class)
public abstract class FishingBobberEntityMixin extends Entity {

    public FishingBobberEntityMixin(EntityType<?> type, Level world) {
        super(type, world);
    }


    @Redirect(
            method = "<init>(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/level/Level;II)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/projectile/FishingHook;moveTo(DDDFF)V"
            )
    )
    private void redirect_init_(FishingHook fishingBobberEntity, double x, double y, double z, float yaw, float pitch, Player thrower, Level world, int lureLevel, int luckOfTheSeaLevel) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(thrower);
        if (gravityDirection == Direction.DOWN) {
            fishingBobberEntity.moveTo(x, y, z, yaw, pitch);
            return;
        }

        Vec3 pos = thrower.getEyePosition();
        Vec2 rot = RotationUtil.rotPlayerToWorld(yaw, pitch, gravityDirection);
        fishingBobberEntity.moveTo(pos.x, pos.y, pos.z, rot.x, rot.y);
    }

    @ModifyVariable(
            method = "<init>(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/level/Level;II)V",
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "Lnet/minecraft/world/phys/Vec3;multiply(DDD)Lnet/minecraft/world/phys/Vec3;"
            ),
            ordinal = 0
    )
    private Vec3 modify_init_Vec3d_1(Vec3 vec3d, Player thrower, Level world, int lureLevel, int luckOfTheSeaLevel) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(thrower);
        if (gravityDirection == Direction.DOWN) {
            return vec3d;
        }

        return RotationUtil.vecPlayerToWorld(vec3d, gravityDirection);
    }
}
