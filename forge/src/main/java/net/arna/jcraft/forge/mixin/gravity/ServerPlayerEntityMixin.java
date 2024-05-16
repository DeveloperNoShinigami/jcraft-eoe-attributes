package net.arna.jcraft.forge.mixin.gravity;

import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.gravity.api.GravityChangerAPI;
import net.arna.jcraft.common.gravity.api.RotationParameters;
import net.arna.jcraft.mixin_logic.ServerPlayerEntityMixinLogic;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ServerPlayer.class, remap = false)
public abstract class ServerPlayerEntityMixin {
/* TODO potential forge crash
    @Inject(
            method = "changeDimension",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;send(Lnet/minecraft/network/protocol/Packet;)V",
                    ordinal = 1,
                    shift = At.Shift.AFTER
            )
    )
    private void inject_moveToWorld_sendPacket_1(CallbackInfoReturnable<ServerPlayer> cir) {
        ServerPlayerEntityMixinLogic.inject_moveToWorld_sendPacket_1((ServerPlayer) (Object) this);
    }

    @Inject(
            method = "teleportTo(Lnet/minecraft/server/level/ServerLevel;DDDFF)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;send(Lnet/minecraft/network/protocol/Packet;)V",
                    ordinal = 0,
                    shift = At.Shift.AFTER
            )
    )
    private void inject_teleport_sendPacket_0(CallbackInfo ci) {
        ServerPlayerEntityMixinLogic.inject_moveToWorld_sendPacket_1((ServerPlayer) (Object) this);
    }

    @Inject(
            method = "restoreFrom",
            at = @At("TAIL")
    )
    private void inject_copyFrom(ServerPlayer oldPlayer, boolean alive, CallbackInfo ci) {
        if (JCraft.gravityConfig.resetGravityOnRespawn) {
        } else {
            GravityChangerAPI.setDefaultGravityDirection((ServerPlayer) (Object) this, GravityChangerAPI.getDefaultGravityDirection(oldPlayer), new RotationParameters().rotationTime(0));
        }
    }

 */
}
