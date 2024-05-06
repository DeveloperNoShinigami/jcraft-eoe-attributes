package net.arna.jcraft.common.network.s2c;

import dev.architectury.networking.NetworkManager;
import net.arna.jcraft.registry.JPacketRegistry;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

public class ServerChannelFeedbackPacket {
    public static void send(ServerPlayerEntity serverPlayerEntity, PacketByteBuf buf) {
        NetworkManager.sendToPlayer(serverPlayerEntity, JPacketRegistry.S2C_SERVER_CHANNEL_FEEDBACK, buf);
    }
}
