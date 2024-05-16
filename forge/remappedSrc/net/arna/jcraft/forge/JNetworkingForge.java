package net.arna.jcraft.forge;

import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.arna.jcraft.forge.capability.api.JCapability;
import net.arna.jcraft.forge.capability.impl.living.StandCapability;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public class JNetworkingForge {


    public static void init() {
        StandCapability.initNetwork();
    }

    public static <T extends JCapability> void sendPackets(Entity entity, ResourceLocation s2c, ResourceLocation c2s, T cap) {
        if (entity instanceof ServerPlayer serverPlayer) {
            FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
            buf.writeUUID(entity.getUUID());
            buf.writeNbt(cap.serializeNBT());
            NetworkManager.sendToPlayer(serverPlayer, s2c, buf);
        } else {
            FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
            buf.writeUUID(entity.getUUID());
            buf.writeNbt(cap.serializeNBT());
            NetworkManager.sendToServer(c2s, buf);
        }
    }
}
