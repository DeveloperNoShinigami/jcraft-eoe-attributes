package net.arna.jcraft.mixin.client.gravity;

import net.arna.jcraft.common.gravity.api.GravityChangerAPI;
import net.arna.jcraft.common.gravity.util.RotationUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Map;
import java.util.UUID;

@Mixin(ClientPacketListener.class)
public abstract class ClientPlayNetworkHandlerMixin {
    @Shadow
    @Final
    private Minecraft minecraft;

    @Shadow
    @Final
    private Map<UUID, PlayerInfo> playerInfoMap;

    @Redirect(
            method = "handleGameEvent",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/player/Player;getEyeY()D"
            )
    )
    private double redirect_onGameStateChange_getEyeY_0(Player playerEntity) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(playerEntity);
        if (gravityDirection == Direction.DOWN) {
            return playerEntity.getEyeY();
        }

        return playerEntity.getEyePosition().y;
    }

    @Redirect(
            method = "handleGameEvent",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/player/Player;getX()D"
            )
    )
    private double redirect_onGameStateChange_getX_0(Player playerEntity) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(playerEntity);
        if (gravityDirection == Direction.DOWN) {
            return playerEntity.getX();
        }

        return playerEntity.getEyePosition().x;
    }

    @Redirect(
            method = "handleGameEvent",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/player/Player;getZ()D"
            )
    )
    private double redirect_onGameStateChange_getZ_0(Player playerEntity) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(playerEntity);
        if (gravityDirection == Direction.DOWN) {
            return playerEntity.getZ();
        }

        return playerEntity.getEyePosition().z;
    }

    @Redirect(
            method = "handleExplosion",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/phys/Vec3;add(DDD)Lnet/minecraft/world/phys/Vec3;"
            )
    )
    private Vec3 redirect_onExplosion_add_0(Vec3 vec3d, double x, double y, double z) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(minecraft.player);
        if (gravityDirection == Direction.DOWN) {
            return vec3d.add(x, y, z);
        }

        return vec3d.add(RotationUtil.vecWorldToPlayer(x, y, z, gravityDirection));
    }
}
