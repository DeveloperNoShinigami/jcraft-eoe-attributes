package net.arna.jcraft.forge;

import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.arna.jcraft.forge.capability.api.JCapability;
import net.arna.jcraft.forge.capability.impl.entity.GrabCapability;
import net.arna.jcraft.forge.capability.impl.entity.GravityCapability;
import net.arna.jcraft.forge.capability.impl.living.GravityShiftCapability;
import net.arna.jcraft.forge.capability.impl.entity.TimeStopCapability;
import net.arna.jcraft.forge.capability.impl.living.*;
import net.arna.jcraft.forge.capability.impl.player.PhCapability;
import net.arna.jcraft.forge.capability.impl.player.SpecCapability;
import net.arna.jcraft.forge.capability.impl.world.ShockwaveHandlerCapability;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public class JNetworkingForge {
    public static void initServer() {
        TimeStopCapability.initServer();

        CooldownsCapability.initServer();
        MiscCapability.initServer();
        PhCapability.initServer();
        SpecCapability.initServer();
        GravityCapability.initServer();
    }
    public static void initClient() {
        GrabCapability.initClient();
        TimeStopCapability.initClient();

        BombTrackerCapability.initClient();
        CooldownsCapability.initClient();
        HitPropertyCapability.initClient();
        MiscCapability.initClient();
        StandCapability.initClient();
        VampireCapability.initClient();
        PhCapability.initClient();
        SpecCapability.initClient();
        ShockwaveHandlerCapability.initClient();
        GravityShiftCapability.initClient();
        GravityCapability.initClient();
    }

    public static <T extends JCapability> void sendPackets(Entity entity, ResourceLocation s2c, ResourceLocation c2s, T cap) {
        if (entity instanceof ServerPlayer serverPlayer) {
            FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
            buf.writeInt(entity.getId());
            buf.writeNbt(cap.serializeNBT());
            NetworkManager.sendToPlayer(serverPlayer, s2c, buf);
        } else if (entity.level().isClientSide) {
            FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
            buf.writeInt(entity.getId());
            buf.writeNbt(cap.serializeNBT());
            NetworkManager.sendToServer(c2s, buf);
        }
    }
}
