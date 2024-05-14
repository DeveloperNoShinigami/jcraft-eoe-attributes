package net.arna.jcraft.forge.capability.impl.entity;

import net.arna.jcraft.common.component.impl.entity.CommonGrabComponentImpl;
import net.arna.jcraft.common.component.impl.living.CommonBombTrackerComponentImpl;
import net.arna.jcraft.forge.capability.api.JCapability;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.LazyOptional;

public class GrabCapability extends CommonGrabComponentImpl implements JCapability {

    public static Capability<GrabCapability> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
    });

    public GrabCapability(Entity entity) {
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

    public static LazyOptional<GrabCapability> getCapabilityOptional(Entity entity) {
        return entity.getCapability(CAPABILITY);
    }

    public static GrabCapability getCapability(Entity entity) {
        return entity.getCapability(CAPABILITY).orElse(new GrabCapability(entity));
    }
}