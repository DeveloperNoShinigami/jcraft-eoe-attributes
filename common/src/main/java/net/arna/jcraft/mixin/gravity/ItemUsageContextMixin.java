package net.arna.jcraft.mixin.gravity;


import net.arna.jcraft.common.gravity.api.GravityChangerAPI;
import net.arna.jcraft.common.gravity.util.RotationUtil;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(UseOnContext.class)
public abstract class ItemUsageContextMixin {
    @Redirect(
            method = "getRotation",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/player/Player;getYRot()F",
                    ordinal = 0
            )
    )
    private float redirect_getPlayerYaw_getYaw_0(Player entity) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(entity);
        if (gravityDirection == Direction.DOWN) {
            return entity.getYRot();
        }

        return RotationUtil.rotPlayerToWorld(entity.getYRot(), entity.getXRot(), gravityDirection).x;
    }
}
