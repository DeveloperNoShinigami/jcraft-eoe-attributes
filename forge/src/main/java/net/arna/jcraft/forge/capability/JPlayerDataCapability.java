package net.arna.jcraft.forge.capability;

import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.component.impl.living.CommonStandComponentImpl;
import net.arna.jcraft.forge.JCraftForge;
import net.arna.jcraft.forge.network.SyncPlayerC2SPacket;
import net.arna.jcraft.forge.network.SyncPlayerS2CPacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.network.PacketDistributor;

public class JPlayerDataCapability extends CommonStandComponentImpl implements JCapability {

    public static Capability<JPlayerDataCapability> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
    });

    public JPlayerDataCapability(Entity entity) {
        super(entity);
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.register(JPlayerDataCapability.class);
    }

    public static void attachPlayerCapability(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof PlayerEntity player) {
            final JPlayerDataCapability capability = new JPlayerDataCapability(player);
            event.addCapability(JCraft.id("player_data"), new JCapabilityProvider<>(JPlayerDataCapability.CAPABILITY, () -> capability));
        }
    }

    public static void playerJoin(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof ServerPlayerEntity serverPlayer) {

            syncSelf(serverPlayer);
        }
    }

    public static void syncPlayerCapability(PlayerEvent.StartTracking event) {
        if (event.getTarget() instanceof PlayerEntity player) {
            if (player.getWorld() instanceof ServerWorld) {
                syncTracking(player);
            }
        }
    }

    public static void playerTick(TickEvent.PlayerTickEvent event) {
        JPlayerDataCapability.getCapabilityOptional(event.player).ifPresent(c -> {

        });
    }

    public static void playerClone(PlayerEvent.Clone event) {
        event.getOriginal().revive();
        JPlayerDataCapability.getCapabilityOptional(event.getOriginal()).ifPresent(o -> JPlayerDataCapability.getCapabilityOptional(event.getEntity()).ifPresent(c -> {
            c.deserializeNBT(o.serializeNBT());
        }));
    }

    @Override
    public NbtCompound serializeNBT() {
        NbtCompound tag = new NbtCompound();
        return tag;
    }

    @Override
    public void deserializeNBT(NbtCompound tag) {

    }

    public static void syncServer(PlayerEntity player) {
        sync(player, PacketDistributor.SERVER.noArg());
    }

    public static void syncSelf(ServerPlayerEntity player) {
        sync(player, PacketDistributor.PLAYER.with(() -> player));
    }

    public static void syncTrackingAndSelf(PlayerEntity player) {
        sync(player, PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player));
    }

    public static void syncTracking(PlayerEntity player) {
        sync(player, PacketDistributor.TRACKING_ENTITY.with(() -> player));
    }

    public static void sync(PlayerEntity player, PacketDistributor.PacketTarget target) {
        getCapabilityOptional(player).ifPresent(c -> {
            if (player instanceof ServerPlayerEntity serverPlayer) {
                JCraftForge.CHANNEL.sendToPlayer(serverPlayer, new SyncPlayerS2CPacket(serverPlayer.getUuid(), c.serializeNBT()));
            } else {
                JCraftForge.CHANNEL.sendToServer(new SyncPlayerC2SPacket(c.serializeNBT()));
            }
        });
    }

    public static LazyOptional<JPlayerDataCapability> getCapabilityOptional(PlayerEntity player) {
        return player.getCapability(CAPABILITY);
    }

    public static JPlayerDataCapability getCapability(PlayerEntity player) {
        return player.getCapability(CAPABILITY).orElse(new JPlayerDataCapability(player));
    }

}
