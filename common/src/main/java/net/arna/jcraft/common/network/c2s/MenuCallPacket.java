package net.arna.jcraft.common.network.c2s;

import dev.architectury.networking.NetworkManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;

public class MenuCallPacket {

    public static void handle(final FriendlyByteBuf buf, final NetworkManager.PacketContext context) {
        context.getPlayer().displayClientMessage(Component.literal("Menu Call Packet was perceived."), false);
        context.getPlayer().displayClientMessage(Component.literal("JCraft menu not available yet, sorry!"), false);
    }

}
