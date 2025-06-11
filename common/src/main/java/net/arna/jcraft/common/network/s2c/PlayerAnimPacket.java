package net.arna.jcraft.common.network.s2c;

import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import lombok.NonNull;
import net.arna.jcraft.api.registry.JPacketRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class PlayerAnimPacket {

    /**
     * Animates player (from) on player (to)'s end, while updating spec values
     *
     * @param from ServerPlayerEntity to animate
     * @param to   ServerPlayerEntity that views animation
     */
    public static void sendSpec(@NonNull final Player from, @NonNull final Iterable<ServerPlayer> to,
                                final String animID, final int moveStun, final float animationSpeed) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeInt(from.getId());
        buf.writeUtf(animID);
        buf.writeBoolean(true);

        buf.writeInt(moveStun);
        buf.writeFloat(animationSpeed);

        NetworkManager.sendToPlayers(to, JPacketRegistry.S2C_PLAYER_ANIMATION, buf);
    }

    /**
     * @param from ServerPlayerEntity to animate
     * @param to   ServerPlayerEntity that views animation
     */
    public static void send(Player from, ServerPlayer to, String animID) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeInt(from.getId());
        buf.writeUtf(animID);
        buf.writeBoolean(false);

        NetworkManager.sendToPlayer(to, JPacketRegistry.S2C_PLAYER_ANIMATION, buf);
    }
}
