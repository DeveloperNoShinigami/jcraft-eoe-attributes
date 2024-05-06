package net.arna.jcraft.common.network.s2c;

import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.arna.jcraft.registry.JPacketRegistry;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Vec3d;

import java.util.Set;

public class PredictionUpdatePacket {
    public static void send(ServerPlayerEntity serverPlayerEntity, Set<Pair<Integer, Vec3d>> idPosPairs) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());

        buf.writeInt(idPosPairs.size());

        idPosPairs.forEach(
                idPosPair -> {
                    // Entity ID
                    buf.writeInt(idPosPair.getLeft());
                    // Position
                    Vec3d pos = idPosPair.getRight();
                    buf.writeDouble(pos.x);
                    buf.writeDouble(pos.y);
                    buf.writeDouble(pos.z);
                }
        );

        NetworkManager.sendToPlayer(serverPlayerEntity, JPacketRegistry.S2C_PREDICTION_UPDATE, buf);
    }
}
