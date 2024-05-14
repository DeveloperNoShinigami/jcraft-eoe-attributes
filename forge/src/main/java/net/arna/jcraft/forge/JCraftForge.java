package net.arna.jcraft.forge;

import dev.architectury.networking.NetworkChannel;
import dev.architectury.platform.forge.EventBuses;
import net.arna.jcraft.client.JCraftClient;
import net.arna.jcraft.forge.capability.api.JCapability;
import net.arna.jcraft.forge.capability.api.JCapabilityProvider;
import net.arna.jcraft.forge.capability.impl.entity.GrabCapability;
import net.arna.jcraft.forge.capability.impl.entity.TimeStopCapability;
import net.arna.jcraft.forge.capability.impl.living.*;
import net.arna.jcraft.forge.capability.impl.player.PhCapability;
import net.arna.jcraft.forge.capability.impl.player.SpecCapability;
import net.arna.jcraft.forge.capability.impl.world.ShockwaveHandlerCapability;
import net.arna.jcraft.forge.network.SyncPlayerC2SPacket;
import net.arna.jcraft.forge.network.SyncPlayerS2CPacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;

import net.arna.jcraft.JCraft;
import net.minecraftforge.network.PacketDistributor;
import org.stringtemplate.v4.misc.Misc;

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

        //CHANNEL.register(SyncPlayerC2SPacket.class, SyncPlayerC2SPacket::encode, SyncPlayerC2SPacket::new, SyncPlayerC2SPacket::apply);
        //CHANNEL.register(SyncPlayerS2CPacket.class, SyncPlayerS2CPacket::encode, SyncPlayerS2CPacket::new, SyncPlayerS2CPacket::applyClient);

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> JCraftClient::init);
    }


    public static <T extends JCapability> void syncServer(LazyOptional<T> jCapability, PlayerEntity player) {
        sync((LazyOptional<JCapability>) jCapability, player, PacketDistributor.SERVER.noArg());
    }

    public static <T extends JCapability> void syncSelf(LazyOptional<T> jCapability, ServerPlayerEntity player) {
        sync((LazyOptional<JCapability>) jCapability, player, PacketDistributor.PLAYER.with(() -> player));
    }

    public static <T extends JCapability> void syncTrackingAndSelf(LazyOptional<T> jCapability, PlayerEntity player) {
        sync((LazyOptional<JCapability>) jCapability, player, PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player));
    }

    public static <T extends JCapability> void syncTracking(LazyOptional<T> jCapability, PlayerEntity player) {
        sync((LazyOptional<JCapability>) jCapability, player, PacketDistributor.TRACKING_ENTITY.with(() -> player));
    }

    public static void sync(LazyOptional<JCapability> jCapability, PlayerEntity player, PacketDistributor.PacketTarget target) {
        jCapability.ifPresent(c -> {
            if (player instanceof ServerPlayerEntity serverPlayer) {
                JCraftForge.CHANNEL.sendToPlayer(serverPlayer, new SyncPlayerS2CPacket(serverPlayer.getUuid(), c.serializeNBT()));
            } else {
                JCraftForge.CHANNEL.sendToServer(new SyncPlayerC2SPacket(c.serializeNBT()));
            }
        });
    }
}
