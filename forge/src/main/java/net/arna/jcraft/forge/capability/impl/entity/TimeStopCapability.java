package net.arna.jcraft.forge.capability.impl.entity;

import net.arna.jcraft.common.component.impl.entity.CommonGrabComponentImpl;
import net.arna.jcraft.common.component.impl.entity.CommonTimeStopComponentImpl;
import net.arna.jcraft.forge.JNetworkingForge;
import net.arna.jcraft.forge.capability.api.JCapability;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.player.PlayerEvent;

import static net.arna.jcraft.JCraft.MOD_ID;

public class TimeStopCapability extends CommonTimeStopComponentImpl implements JCapability {

    public static Identifier TIME_S2C = new Identifier(MOD_ID, "time_s2c");
    public static Identifier TIME_C2S = new Identifier(MOD_ID, "time_c2s");

    public static Capability<TimeStopCapability> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
    });

    public TimeStopCapability(Entity entity) {
        super(entity);
    }

    @Override
    public void sync(Entity entity) {
        super.sync(entity);
        TimeStopCapability.syncEntityCapability(entity);
    }

    private static void syncEntityCapability(Entity entity) {
        if (entity instanceof LivingEntity living) {
            JNetworkingForge.sendPackets(living, TIME_S2C, TIME_C2S, getCapability(living));
        }
    }

    public static void syncEntityCapability(PlayerEvent.StartTracking event) {
        if (event.getTarget() instanceof LivingEntity livingEntity) {
            if (livingEntity.getWorld() instanceof ServerWorld) {
                syncEntityCapability(livingEntity);
            }
        }
    }

    @Override
    public NbtCompound serializeNBT() {
        NbtCompound tag = new NbtCompound();
        super.writeToNbt(tag);
        return tag;
    }

    @Override
    public void deserializeNBT(NbtCompound tag) {
        super.readFromNbt(tag);
    }

    public static LazyOptional<TimeStopCapability> getCapabilityOptional(Entity entity) {
        return entity.getCapability(CAPABILITY);
    }

    public static TimeStopCapability getCapability(Entity entity) {
        return entity.getCapability(CAPABILITY).orElse(new TimeStopCapability(entity));
    }
}