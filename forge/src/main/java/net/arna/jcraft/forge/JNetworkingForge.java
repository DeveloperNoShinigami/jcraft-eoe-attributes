package net.arna.jcraft.forge;

import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.arna.jcraft.forge.capability.api.JCapability;
import net.arna.jcraft.forge.capability.impl.living.StandCapability;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class JNetworkingForge {


    public static void init() {
        StandCapability.initNetwork();
    }

    public static <T extends JCapability> void sendPackets(Entity entity, Identifier s2c, Identifier c2s, T cap) {
        if (entity instanceof ServerPlayerEntity serverPlayer) {
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            buf.writeUuid(entity.getUuid());
            buf.writeNbt(cap.serializeNBT());
            NetworkManager.sendToPlayer(serverPlayer, s2c, buf);
        } else {
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            buf.writeUuid(entity.getUuid());
            buf.writeNbt(cap.serializeNBT());
            NetworkManager.sendToServer(c2s, buf);
        }
    }
}
