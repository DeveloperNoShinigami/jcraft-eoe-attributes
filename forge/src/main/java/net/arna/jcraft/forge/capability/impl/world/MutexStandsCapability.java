package net.arna.jcraft.forge.capability.impl.world;

import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.component.impl.world.CommonMutexStandsComponentImpl;
import net.arna.jcraft.forge.capability.api.JCapability;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.LazyOptional;

import java.util.Objects;

public class MutexStandsCapability extends CommonMutexStandsComponentImpl implements JCapability {
    public static Capability<MutexStandsCapability> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        writeToNbt(tag);
        return tag;
    }

    @Override
    public void deserializeNBT(final CompoundTag arg) {
        readFromNbt(arg);
    }

    public static LazyOptional<MutexStandsCapability> getCapabilityOptional(Level level) {
        Level target = Objects.requireNonNull(level.getServer()).getLevel(Level.OVERWORLD);
        if (target == null) {
            JCraft.LOGGER.warn("Overworld does not exist, mutex stands will likely not work.");
            target = level;
        }

        return target.getCapability(CAPABILITY);
    }

    public static MutexStandsCapability getCapability(Level level) {
        return getCapabilityOptional(level).orElse(new MutexStandsCapability());
    }
}
