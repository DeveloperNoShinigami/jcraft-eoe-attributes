package net.arna.jcraft.forge.capability.impl.living;

import net.arna.jcraft.common.component.impl.CommonVampireComponentImpl;
import net.arna.jcraft.common.component.impl.player.CommonPhComponentImpl;
import net.arna.jcraft.forge.capability.api.JCapability;
import net.arna.jcraft.forge.capability.impl.player.PhCapability;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.network.PacketDistributor;

public class VampireCapability extends CommonVampireComponentImpl implements JCapability {

    public static Capability<VampireCapability> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
    });

    public VampireCapability(LivingEntity living) {
        super(living);
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

    public static LazyOptional<VampireCapability> getCapabilityOptional(Entity entity) {
        return entity.getCapability(CAPABILITY);
    }

    public static VampireCapability getCapability(LivingEntity entity) {
        return entity.getCapability(CAPABILITY).orElse(new VampireCapability(entity));
    }
}
