package net.arna.jcraft.common.network.s2c;

import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.arna.jcraft.api.registry.JPacketRegistry;
import net.arna.jcraft.common.data.AttackerDataLoader;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

public class AttackerDataPacket {
    public static void send(Iterable<ServerPlayer> players) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        AttackerDataLoader.writeToBuffer(buf);
        NetworkManager.sendToPlayers(players, JPacketRegistry.S2C_ATTACKER_DATA, buf);
    }
}
