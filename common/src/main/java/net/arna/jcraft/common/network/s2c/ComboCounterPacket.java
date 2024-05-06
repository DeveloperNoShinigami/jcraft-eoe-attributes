package net.arna.jcraft.common.network.s2c;

import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.arna.jcraft.registry.JPacketRegistry;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

public class ComboCounterPacket {
    public static void send(ServerPlayerEntity player, int comboCount, float damageScaling) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());

        buf.writeInt(comboCount);
        buf.writeFloat(damageScaling);

        NetworkManager.sendToPlayer(player, JPacketRegistry.S2C_COMBO_COUNTER, buf);
    }
}
