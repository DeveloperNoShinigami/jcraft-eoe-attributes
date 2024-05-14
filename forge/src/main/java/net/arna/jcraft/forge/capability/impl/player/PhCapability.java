package net.arna.jcraft.forge.capability.impl.player;

import net.arna.jcraft.common.component.impl.player.CommonPhComponentImpl;
import net.arna.jcraft.forge.capability.api.JCapability;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;


public class PhCapability extends CommonPhComponentImpl implements JCapability {

    public static Capability<PhCapability> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
    });

    public PhCapability(PlayerEntity player) {
        super(player);
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

    public static @NotNull LazyOptional<PhCapability> getCapabilityOptional(PlayerEntity player) {
        return player.getCapability(CAPABILITY);
    }

    public static PhCapability getCapability(PlayerEntity player) {
        return player.getCapability(CAPABILITY).orElse(new PhCapability(player));
    }

}
