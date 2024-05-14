package net.arna.jcraft.forge.capability.impl.player;

import net.arna.jcraft.common.component.impl.player.CommonPhComponentImpl;
import net.arna.jcraft.forge.JCraftForge;
import net.arna.jcraft.forge.capability.api.JCapability;
import net.arna.jcraft.forge.network.SyncPlayerC2SPacket;
import net.arna.jcraft.forge.network.SyncPlayerS2CPacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.network.PacketDistributor;

public class SpecCapability extends CommonPhComponentImpl implements JCapability {

    public static Capability<SpecCapability> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
    });

    public SpecCapability(PlayerEntity player) {
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


    public static LazyOptional<SpecCapability> getCapabilityOptional(PlayerEntity player) {
        return player.getCapability(CAPABILITY);
    }

    public static SpecCapability getCapability(PlayerEntity player) {
        return player.getCapability(CAPABILITY).orElse(new SpecCapability(player));
    }
}
