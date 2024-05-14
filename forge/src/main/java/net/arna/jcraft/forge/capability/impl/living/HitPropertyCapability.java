package net.arna.jcraft.forge.capability.impl.living;

import net.arna.jcraft.common.component.impl.living.CommonCooldownsComponentImpl;
import net.arna.jcraft.common.component.impl.living.CommonHitPropertyComponentImpl;
import net.arna.jcraft.forge.capability.api.JCapability;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.LazyOptional;

public class HitPropertyCapability extends CommonHitPropertyComponentImpl implements JCapability {

    public static Capability<HitPropertyCapability> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
    });

    public HitPropertyCapability(LivingEntity living) {
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

    public static LazyOptional<HitPropertyCapability> getCapabilityOptional(Entity entity) {
        return entity.getCapability(CAPABILITY);
    }

    public static HitPropertyCapability getCapability(LivingEntity entity) {
        return entity.getCapability(CAPABILITY).orElse(new HitPropertyCapability(entity));
    }
}