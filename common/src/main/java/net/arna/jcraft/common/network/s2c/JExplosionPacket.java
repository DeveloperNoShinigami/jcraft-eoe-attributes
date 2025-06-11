package net.arna.jcraft.common.network.s2c;

import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.arna.jcraft.common.util.JExplosionModifier;
import net.arna.jcraft.api.registry.JPacketRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Explosion;

public class JExplosionPacket {
    public static void send(ServerPlayer player, double x, double y, double z, float power, Explosion explosion, JExplosionModifier modifier) {
        ClientboundExplodePacket nativePacket = new ClientboundExplodePacket(x, y, z, power, explosion.getToBlow(), explosion.getHitPlayers().get(player));

        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        nativePacket.write(buf);
        if (modifier == null) {
            buf.writeBoolean(false);
        } else {
            buf.writeBoolean(true);
            modifier.write(buf, player.level().random);
        }

        NetworkManager.sendToPlayer(player, JPacketRegistry.S2C_J_EXPLOSION, buf);
    }
}
