package net.arna.jcraft.forge;

import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.arna.jcraft.forge.capability.api.JCapability;
import net.arna.jcraft.forge.capability.impl.entity.GrabCapability;
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


    public static void init() {
        GrabCapability.initNetwork();
        TimeStopCapability.initNetwork();

        BombTrackerCapability.initNetwork();
        CooldownsCapability.initNetwork();
        HitPropertyCapability.initNetwork();
        MiscCapability.initNetwork();
        StandCapability.initNetwork();
        VampireCapability.initNetwork();
        PhCapability.initNetwork();
        SpecCapability.initNetwork();
        ShockwaveHandlerCapability.initNetwork();
    }

    public static <T extends JCapability> void sendPackets(Entity entity, ResourceLocation s2c, ResourceLocation c2s, T cap) {
        if (entity instanceof ServerPlayer serverPlayer) {
            FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
            buf.writeInt(entity.getId());
            buf.writeNbt(cap.serializeNBT());
            NetworkManager.sendToPlayer(serverPlayer, s2c, buf);
        } else if (entity.level() != null && entity.level().isClientSide) {
            FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
            buf.writeInt(entity.getId());
            buf.writeNbt(cap.serializeNBT());
            NetworkManager.sendToServer(c2s, buf);
        }
    }

    public static <T extends JCapability> void sendPlayerPackets(Entity entity, ResourceLocation s2c, ResourceLocation c2s, T cap) {
        if (entity instanceof ServerPlayer serverPlayer) {
            FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
            buf.writeUUID(entity.getUUID());
            buf.writeNbt(cap.serializeNBT());
            NetworkManager.sendToPlayer(serverPlayer, s2c, buf);
        } else if (entity.level() != null && entity.level().isClientSide) {
            FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
            buf.writeUUID(entity.getUUID());
            buf.writeNbt(cap.serializeNBT());
            NetworkManager.sendToServer(c2s, buf);
        }
    }
}
