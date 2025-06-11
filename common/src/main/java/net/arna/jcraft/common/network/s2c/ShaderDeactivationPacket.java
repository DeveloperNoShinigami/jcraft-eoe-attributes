package net.arna.jcraft.common.network.s2c;

import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.arna.jcraft.api.registry.JPacketRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

/**
 * If the shader should be canceled after its activation prior to its natural end, use this packet
 */
public class ShaderDeactivationPacket {

    public static void send(ServerPlayer serverPlayerEntity, ShaderActivationPacket.Type type) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeUtf(type.getSerializedName());
        NetworkManager.sendToPlayer(serverPlayerEntity, JPacketRegistry.S2C_SHADER_DEACTIVATION, buf);
    }
}
