package net.arna.jcraft.common.network.s2c;

import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.arna.jcraft.api.registry.JPacketRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

public class ComboCounterPacket {
    public static void send(ServerPlayer player, int comboCount, float damageScaling) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());

        buf.writeInt(comboCount);
        buf.writeFloat(damageScaling);

        NetworkManager.sendToPlayer(player, JPacketRegistry.S2C_COMBO_COUNTER, buf);
    }
}
