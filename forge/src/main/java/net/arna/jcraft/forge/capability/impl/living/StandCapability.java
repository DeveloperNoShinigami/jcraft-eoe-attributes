package net.arna.jcraft.forge.capability.impl.living;

import net.arna.jcraft.common.component.impl.living.CommonBombTrackerComponentImpl;
import net.arna.jcraft.common.component.impl.living.CommonStandComponentImpl;
import net.arna.jcraft.forge.capability.api.JCapability;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.LazyOptional;

public class StandCapability extends CommonStandComponentImpl implements JCapability {

    public static Capability<StandCapability> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
    });

    public StandCapability(LivingEntity living) {
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

    public static LazyOptional<StandCapability> getCapabilityOptional(Entity entity) {
        return entity.getCapability(CAPABILITY);
    }

    public static StandCapability getCapability(LivingEntity entity) {
        return entity.getCapability(CAPABILITY).orElse(new StandCapability(entity));
    }
}