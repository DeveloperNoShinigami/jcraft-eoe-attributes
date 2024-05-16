package net.arna.jcraft.common.network.c2s;

import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

public class PredictionTriggerPacket {
    private static final Set<ServerPlayer> subscribers = Collections.newSetFromMap(new WeakHashMap<>());

    public static FriendlyByteBuf write(boolean enable) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeBoolean(enable);
        return buf;
    }

    public static void handle(FriendlyByteBuf buf, NetworkManager.PacketContext context) {
        ServerPlayer player = (ServerPlayer) context.getPlayer();
        MinecraftServer server = context.getPlayer().getServer();

        boolean enable = buf.readBoolean();
        server.execute(() -> {
            if (enable) {
                subscribers.add(player);
            } else {
                subscribers.remove(player);
            }
        });
    }

    public static Set<ServerPlayer> getSubscribers() {
        return subscribers;
    }

    public static boolean isSubscribed(ServerPlayer player) {
        return subscribers.contains(player);
    }
}
