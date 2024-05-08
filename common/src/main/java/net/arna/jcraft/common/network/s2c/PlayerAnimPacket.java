package net.arna.jcraft.common.network.s2c;

import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.arna.jcraft.registry.JPacketRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

public class PlayerAnimPacket {

    /**
     * Animates player (from) on player (to)'s end, while updating spec values
     *
     * @param from ServerPlayerEntity to animate
     * @param to   ServerPlayerEntity that views animation
     */
    public static void sendSpec(PlayerEntity from, ServerPlayerEntity to, String animID, int moveStun, float animationSpeed) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeInt(from.getId());
        buf.writeString(animID);
        buf.writeBoolean(true);

        buf.writeInt(moveStun);
        buf.writeFloat(animationSpeed);

        NetworkManager.sendToPlayer(to, JPacketRegistry.S2C_PLAYER_ANIMATION, buf);
    }

    /**
     * @param from ServerPlayerEntity to animate
     * @param to   ServerPlayerEntity that views animation
     */
    public static void send(PlayerEntity from, ServerPlayerEntity to, String animID) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeInt(from.getId());
        buf.writeString(animID);
        buf.writeBoolean(false);

        NetworkManager.sendToPlayer(to, JPacketRegistry.S2C_PLAYER_ANIMATION, buf);
    }
}
