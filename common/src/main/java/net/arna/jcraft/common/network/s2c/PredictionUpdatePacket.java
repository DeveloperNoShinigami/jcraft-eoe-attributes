package net.arna.jcraft.common.network.s2c;

import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.arna.jcraft.api.registry.JPacketRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Tuple;
import net.minecraft.world.phys.Vec3;
import java.util.Set;

public class PredictionUpdatePacket {
    public static void send(ServerPlayer serverPlayerEntity, Set<Tuple<Integer, Vec3>> idPosPairs) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());

        buf.writeInt(idPosPairs.size());

        idPosPairs.forEach(
                idPosPair -> {
                    // Entity ID
                    buf.writeInt(idPosPair.getA());
                    // Position
                    Vec3 pos = idPosPair.getB();
                    buf.writeDouble(pos.x);
                    buf.writeDouble(pos.y);
                    buf.writeDouble(pos.z);
                }
        );

        NetworkManager.sendToPlayer(serverPlayerEntity, JPacketRegistry.S2C_PREDICTION_UPDATE, buf);
    }
}
