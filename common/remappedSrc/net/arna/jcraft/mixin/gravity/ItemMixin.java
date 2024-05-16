package net.arna.jcraft.mixin.gravity;

import net.arna.jcraft.common.gravity.api.GravityChangerAPI;
import net.arna.jcraft.common.gravity.util.RotationUtil;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Item.class)
public class ItemMixin {
    @Redirect(
            method = "raycast",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/PlayerEntity;getYaw()F",
                    ordinal = 0
            )
    )
    private static float redirect_raycast_getYaw(Player player) {
        Direction direction = GravityChangerAPI.getGravityDirection(player);
        if (direction == Direction.DOWN) {
            return player.getYRot();
        }
        return RotationUtil.rotPlayerToWorld(player.getYRot(), player.getXRot(), direction).x;
    }

    @Redirect(
            method = "raycast",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/PlayerEntity;getPitch()F",
                    ordinal = 0
            )
    )
    private static float redirect_raycast_getPitch(Player player) {
        Direction direction = GravityChangerAPI.getGravityDirection(player);
        if (direction == Direction.DOWN) {
            return player.getXRot();
        }
        return RotationUtil.rotPlayerToWorld(player.getYRot(), player.getXRot(), direction).y;
    }
}
