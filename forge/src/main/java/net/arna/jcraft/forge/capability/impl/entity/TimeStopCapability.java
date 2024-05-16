package net.arna.jcraft.forge.capability.impl.entity;

import dev.architectury.networking.NetworkManager;
import net.arna.jcraft.common.component.impl.entity.CommonTimeStopComponentImpl;
import net.arna.jcraft.forge.JNetworkingForge;
import net.arna.jcraft.forge.capability.api.JCapability;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.event.entity.player.PlayerEvent;

import java.util.Optional;
import java.util.UUID;

import static net.arna.jcraft.JCraft.MOD_ID;

public class TimeStopCapability extends CommonTimeStopComponentImpl implements JCapability {

    public static ResourceLocation TIME_S2C = new ResourceLocation(MOD_ID, "time_s2c");
    public static ResourceLocation TIME_C2S = new ResourceLocation(MOD_ID, "time_c2s");

    public static Capability<TimeStopCapability> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
    });

    public TimeStopCapability(Entity entity) {
        super(entity);
    }

    @Override
    public void sync(Entity entity) {
        if (entity != null) {
            TimeStopCapability.syncEntityCapability(entity);
        }
    }

    private static void syncEntityCapability(Entity entity) {
        if (entity instanceof LivingEntity living) {
            JNetworkingForge.sendPackets(living, TIME_S2C, TIME_C2S, getCapability(living));
        }
    }

    public static void syncEntityCapability(PlayerEvent.StartTracking event) {
        if (event.getTarget() instanceof LivingEntity livingEntity) {
            if (livingEntity.level() instanceof ServerLevel) {
                syncEntityCapability(livingEntity);
            }
        }
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        super.writeToNbt(tag);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        super.readFromNbt(tag);
    }

    public static Optional<TimeStopCapability> getCapabilityOptional(Entity entity) {
        return entity.getCapability(CAPABILITY).resolve();
    }

    public static TimeStopCapability getCapability(Entity entity) {
        return entity.getCapability(CAPABILITY).orElse(new TimeStopCapability(entity));
    }

    public static void initNetwork(){
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, TIME_S2C, (buf, context) -> {

        });

        NetworkManager.registerReceiver(NetworkManager.Side.C2S, TIME_C2S, (buf, context) -> {
            int id = buf.readInt();
            CompoundTag nbt = buf.readNbt();
            if (Minecraft.getInstance().level != null) {
                TimeStopCapability.getCapabilityOptional(Minecraft.getInstance().level.getEntity(id)).ifPresent(c -> c.deserializeNBT(nbt));
            }
        });
    }
}