package net.arna.jcraft.mixin.gravity;

import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerList.class)
public abstract class PlayerManagerMixin {

    @Inject(
            method = "placeNewPlayer",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;send(Lnet/minecraft/network/protocol/Packet;)V",
                    ordinal = 0,
                    shift = At.Shift.AFTER
            )
    )
    private void inject_onPlayerConnect_sendPacket_0(Connection connection, ServerPlayer player, CallbackInfo ci) {
        //Not need because (I think) player gravity is synced when nbt is loaded
        //((ServerPlayerEntityAccessor) player).gravitychanger$sendGravityPacket(GravityChangerAPI.getGravityDirection(player), false);
    }

    // This uses the old player instance but it should be ok as long as the gravity is not changed between new player creation and this
    @Inject(
            method = "respawn",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;send(Lnet/minecraft/network/protocol/Packet;)V",
                    ordinal = 1,
                    shift = At.Shift.AFTER
            )
    )
    private void inject_respawnPlayer_sendPacket_1(ServerPlayer player, boolean alive, CallbackInfoReturnable<ServerPlayer> cir) {
        //Not need because (I think) player gravity is synced when nbt is loaded
        //((ServerPlayerEntityAccessor) player).gravitychanger$sendGravityPacket(GravityChangerAPI.getGravityDirection(player), false);
    }
}
