package net.arna.jcraft.common.network.s2c;

import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.arna.jcraft.api.registry.JPacketRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

public class MagneticFieldParticlePacket {
    public static void send(ServerPlayer target, double strength, Vec3 pos) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());

        buf.writeDouble(strength);
        buf.writeDouble(pos.x);
        buf.writeDouble(pos.y);
        buf.writeDouble(pos.z);

        NetworkManager.sendToPlayer(target, JPacketRegistry.S2C_MAGNETIC_FIELD_PARTICLE, buf);
    }
}
