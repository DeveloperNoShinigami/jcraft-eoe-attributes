package net.arna.jcraft.common.network.s2c;

import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.arna.jcraft.registry.JPacketRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class TimeStopStatePacket {
    public static FriendlyByteBuf createStartPacket(int timestopperId, Vec3 position, ResourceKey<Level> worldRegistryKey, int duration) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeBoolean(true); // Start packet?
        buf.writeInt(timestopperId);
        buf.writeDouble(position.x);
        buf.writeDouble(position.y);
        buf.writeDouble(position.z);
        buf.writeResourceKey(worldRegistryKey);
        buf.writeInt(duration);
        return buf;
    }

    public static FriendlyByteBuf createStopPacket(int timestopperId) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeBoolean(false); // Start packet?
        buf.writeInt(timestopperId);
        return buf;
    }

    public static void send(ServerPlayer serverPlayerEntity, FriendlyByteBuf buf) {
        NetworkManager.sendToPlayer(serverPlayerEntity, JPacketRegistry.S2C_TIME_STOP, buf);
    }

    public static void send(Iterable<ServerPlayer> serverPlayers, FriendlyByteBuf buf) {
        NetworkManager.sendToPlayers(serverPlayers, JPacketRegistry.S2C_TIME_STOP, buf);
    }
}
