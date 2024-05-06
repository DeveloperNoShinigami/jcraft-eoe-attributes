package net.arna.jcraft.common.network.s2c;

import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.arna.jcraft.registry.JPacketRegistry;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class TimeStopStatePacket {
    public static PacketByteBuf createStartPacket(int timestopperId, Vec3d position, RegistryKey<World> worldRegistryKey, int duration) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeBoolean(true); // Start packet?
        buf.writeInt(timestopperId);
        buf.writeDouble(position.x);
        buf.writeDouble(position.y);
        buf.writeDouble(position.z);
        buf.writeRegistryKey(worldRegistryKey);
        buf.writeInt(duration);
        return buf;
    }

    public static PacketByteBuf createStopPacket(int timestopperId) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeBoolean(false); // Start packet?
        buf.writeInt(timestopperId);
        return buf;
    }

    public static void send(ServerPlayerEntity serverPlayerEntity, PacketByteBuf buf) {
        NetworkManager.sendToPlayer(serverPlayerEntity, JPacketRegistry.S2C_TIME_STOP, buf);
    }
}
