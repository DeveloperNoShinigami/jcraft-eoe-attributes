package net.arna.jcraft.common.network.s2c;

import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.arna.jcraft.api.registry.JPacketRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

public class StoneMaskClenchPacket {
    public static void sendStoneMaskClench(ServerPlayer victim) {
        if (victim == null || !victim.isAlive()) {
            return;
        }

        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeVarInt(victim.getId());

        NetworkManager.sendToPlayers(victim.serverLevel().players(), JPacketRegistry.S2C_STONE_MASK_CLENCH, buf);
    }
}
