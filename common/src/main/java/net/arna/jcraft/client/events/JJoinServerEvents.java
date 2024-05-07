package net.arna.jcraft.client.events;

import dev.architectury.event.events.client.ClientLifecycleEvent;
import dev.architectury.event.events.client.ClientPlayerEvent;
import dev.architectury.networking.NetworkManager;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.client.JClientConfig;
import net.arna.jcraft.common.network.c2s.PredictionTriggerPacket;
import net.arna.jcraft.registry.JPacketRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;

public class JJoinServerEvents{

    public static void init(){
        ClientPlayerEvent.CLIENT_PLAYER_JOIN.register(player -> {
            if (player == null) {
                JCraft.LOGGER.fatal("onPlayReady was called with invalid client player!");
                return;
            }

            // Sync initial prediction option
            NetworkManager.sendToServer(JPacketRegistry.C2S_PREDICTION_TRIGGER,
                    PredictionTriggerPacket.write(JClientConfig.getInstance().isClientsidePrediction()));
        });
    }
}
