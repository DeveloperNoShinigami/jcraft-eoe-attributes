package net.arna.jcraft.forge;

import dev.architectury.networking.NetworkChannel;
import dev.architectury.platform.forge.EventBuses;
import net.arna.jcraft.client.JCraftClient;
import net.arna.jcraft.forge.network.SyncPlayerC2SPacket;
import net.arna.jcraft.forge.network.SyncPlayerS2CPacket;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;

import net.arna.jcraft.JCraft;

import static net.arna.jcraft.JCraft.MOD_ID;

@Mod(MOD_ID)
public final class JCraftForge {

    public static final NetworkChannel CHANNEL = NetworkChannel.create(JCraft.id("networking_channel"));

    public JCraftForge() {
        var modBus = Mod.EventBusSubscriber.Bus.MOD.bus().get();
        var forgeBus = Mod.EventBusSubscriber.Bus.FORGE.bus().get();

        // Submit our event bus to let Architectury API register our content on the right time.
        EventBuses.registerModEventBus(MOD_ID, modBus);

        JCraft.init();

        CHANNEL.register(SyncPlayerC2SPacket.class, SyncPlayerC2SPacket::encode, SyncPlayerC2SPacket::new, SyncPlayerC2SPacket::apply);
        CHANNEL.register(SyncPlayerS2CPacket.class, SyncPlayerS2CPacket::encode, SyncPlayerS2CPacket::new, SyncPlayerS2CPacket::applyClient);

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            JCraftClient.init();
        });
    }
}
