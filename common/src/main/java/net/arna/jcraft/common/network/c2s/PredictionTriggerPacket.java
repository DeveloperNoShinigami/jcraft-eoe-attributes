package net.arna.jcraft.common.network.c2s;

import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

public class PredictionTriggerPacket {
    private static final Set<ServerPlayerEntity> subscribers = Collections.newSetFromMap(new WeakHashMap<>());

    public static PacketByteBuf write(boolean enable) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeBoolean(enable);
        return buf;
    }

    public static void handle(PacketByteBuf buf, NetworkManager.PacketContext context) {
        ServerPlayerEntity player = (ServerPlayerEntity) context.getPlayer();
        MinecraftServer server = context.getPlayer().getServer();

        boolean enable = buf.readBoolean();
        server.execute(() -> {
            if (enable) subscribers.add(player);
            else subscribers.remove(player);
        });
    }

    public static Set<ServerPlayerEntity> getSubscribers() {
        return subscribers;
    }

    public static boolean isSubscribed(ServerPlayerEntity player) {
        return subscribers.contains(player);
    }
}
