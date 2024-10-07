package net.arna.jcraft.forge.capability.api;

import lombok.NonNull;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullSupplier;
import org.jetbrains.annotations.Nullable;

public class JCapabilityProvider<C extends INBTSerializable<CompoundTag>> implements ICapabilityProvider, INBTSerializable<CompoundTag> {
    private final C instance;
    private final LazyOptional<C> capOptional;

    private final Capability<C> capability;

    public JCapabilityProvider(Capability<C> capability, NonNullSupplier<C> capInstance) {
        this.capability = capability;
        this.instance = capInstance.get();
        this.capOptional = LazyOptional.of(() -> this.instance);
    }

    @NonNull
    @Override
    public <T> LazyOptional<T> getCapability(@NonNull Capability<T> cap, @Nullable Direction side) {
        return capability.orEmpty(cap, capOptional);
    }

    @Override
    public CompoundTag serializeNBT() {
        return this.instance.serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.instance.deserializeNBT(nbt);
    }
}
