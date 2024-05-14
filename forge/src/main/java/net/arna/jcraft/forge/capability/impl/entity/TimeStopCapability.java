package net.arna.jcraft.forge.capability.impl.entity;

import net.arna.jcraft.common.component.impl.entity.CommonGrabComponentImpl;
import net.arna.jcraft.common.component.impl.entity.CommonTimeStopComponentImpl;
import net.arna.jcraft.forge.capability.api.JCapability;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.LazyOptional;

public class TimeStopCapability extends CommonTimeStopComponentImpl implements JCapability {

    public static Capability<TimeStopCapability> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
    });

    public TimeStopCapability(Entity entity) {
        super(entity);
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