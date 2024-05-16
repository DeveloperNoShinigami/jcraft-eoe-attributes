package net.arna.jcraft.common.network.s2c;

import dev.architectury.networking.NetworkManager;
import net.arna.jcraft.registry.JPacketRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

public class ServerChannelFeedbackPacket {
    public static void send(ServerPlayer serverPlayerEntity, FriendlyByteBuf buf) {
        NetworkManager.sendToPlayer(serverPlayerEntity, JPacketRegistry.S2C_SERVER_CHANNEL_FEEDBACK, buf);
    }
}
