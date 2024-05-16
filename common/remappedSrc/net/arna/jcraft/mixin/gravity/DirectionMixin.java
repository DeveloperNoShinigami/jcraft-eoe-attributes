package net.arna.jcraft.mixin.gravity;


import net.arna.jcraft.common.gravity.api.GravityChangerAPI;
import net.arna.jcraft.common.gravity.util.RotationUtil;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Direction.class)
public abstract class DirectionMixin {
    @Redirect(
            method = "getEntityFacingOrder",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;getYaw(F)F",
                    ordinal = 0
            )
    )
    private static float redirect_getEntityFacingOrder_getYaw_0(Entity entity, float tickDelta) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(entity);
        if (gravityDirection == Direction.DOWN) {
            return entity.getViewYRot(tickDelta);
        }

        return RotationUtil.rotPlayerToWorld(entity.getViewYRot(tickDelta), entity.getViewXRot(tickDelta), gravityDirection).x;
    }

    @Redirect(
            method = "getEntityFacingOrder",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;getPitch(F)F",
                    ordinal = 0
            )
    )
    private static float redirect_getEntityFacingOrder_getPitch_0(Entity entity, float tickDelta) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(entity);
        if (gravityDirection == Direction.DOWN) {
            return entity.getViewXRot(tickDelta);
        }

        return RotationUtil.rotPlayerToWorld(entity.getViewYRot(tickDelta), entity.getViewXRot(tickDelta), gravityDirection).y;
    }

    @Redirect(
            method = "getLookDirectionForAxis",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;getYaw(F)F",
                    ordinal = 0
            )
    )
    private static float redirect_getLookDirectionForAxis_getYaw_0(Entity entity, float tickDelta) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(entity);
        if (gravityDirection == Direction.DOWN) {
            return entity.getViewYRot(tickDelta);
        }

        return RotationUtil.rotPlayerToWorld(entity.getViewYRot(tickDelta), entity.getViewXRot(tickDelta), gravityDirection).x;
    }

    @Redirect(
            method = "getLookDirectionForAxis",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;getYaw(F)F",
                    ordinal = 1
            )
    )
    private static float redirect_getLookDirectionForAxis_getYaw_1(Entity entity, float tickDelta) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(entity);
        if (gravityDirection == Direction.DOWN) {
            return entity.getViewYRot(tickDelta);
        }

        return RotationUtil.rotPlayerToWorld(entity.getViewYRot(tickDelta), entity.getViewXRot(tickDelta), gravityDirection).x;
    }

    @Redirect(
            method = "getLookDirectionForAxis",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;getPitch(F)F",
                    ordinal = 0
            )
    )
    private static float redirect_getLookDirectionForAxis_getPitch_0(Entity entity, float tickDelta) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(entity);
        if (gravityDirection == Direction.DOWN) {
            return entity.getViewXRot(tickDelta);
        }

        return RotationUtil.rotPlayerToWorld(entity.getViewYRot(tickDelta), entity.getViewXRot(tickDelta), gravityDirection).y;
    }
}
