package net.arna.jcraft.common.network.s2c;

import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.arna.jcraft.registry.JPacketRegistry;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * If the shader should be canceled after its activation prior to its natural end, use this packet
 */
public class ShaderDeactivationPacket {

    public static void send(ServerPlayerEntity serverPlayerEntity, ShaderActivationPacket.Type type) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeString(type.asString());
        NetworkManager.sendToPlayer(serverPlayerEntity, JPacketRegistry.S2C_SHADER_DEACTIVATION, buf);
    }
}
