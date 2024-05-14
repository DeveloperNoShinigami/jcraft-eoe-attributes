package net.arna.jcraft.forge.capability.api;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullSupplier;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public class JCapabilityProvider<C extends INBTSerializable<NbtCompound>> implements ICapabilityProvider, INBTSerializable<NbtCompound> {
    private final C instance;
    private final LazyOptional<C> capOptional;

    private final Capability<C> capability;

    public JCapabilityProvider(Capability<C> capability, NonNullSupplier<C> capInstance) {
        this.capability = capability;
        this.instance = capInstance.get();
        this.capOptional = LazyOptional.of(capInstance);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return capability.orEmpty(cap, capOptional);
    }

    @Override
    public NbtCompound serializeNBT() {
        return this.instance.serializeNBT();
    }

    @Override
    public void deserializeNBT(NbtCompound nbt) {
        this.instance.deserializeNBT(nbt);
    }
}
