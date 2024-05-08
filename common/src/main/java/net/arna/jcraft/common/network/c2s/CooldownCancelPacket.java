package net.arna.jcraft.common.network.c2s;

import dev.architectury.networking.NetworkManager;
import net.arna.jcraft.platform.JComponentPlatformUtils;
import net.arna.jcraft.registry.JStatusRegistry;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

public class CooldownCancelPacket {

    public static void handle(PacketByteBuf buf, NetworkManager.PacketContext context) {
        ServerPlayerEntity player = (ServerPlayerEntity) context.getPlayer();

        if (player.isCreative() || !player.hasStatusEffect(JStatusRegistry.DAZED)) {
            JComponentPlatformUtils.getCooldowns(player).cooldownCancel();
        }
    }
}
