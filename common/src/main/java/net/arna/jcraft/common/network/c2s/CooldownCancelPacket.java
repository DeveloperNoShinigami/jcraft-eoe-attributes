package net.arna.jcraft.common.network.c2s;

import dev.architectury.networking.NetworkManager;
import net.arna.jcraft.platform.JComponentPlatformUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

public class CooldownCancelPacket {

    public static void handle(FriendlyByteBuf buf, NetworkManager.PacketContext context) {
        ServerPlayer player = (ServerPlayer) context.getPlayer();

        if (player.isCreative()) {
            JComponentPlatformUtils.getCooldowns(player).cooldownCancel();
        }
    }
}
