package net.arna.jcraft.forge.capability.impl.world;

import net.arna.jcraft.common.component.impl.world.CommonTexasHoldEmComponentImpl;
import net.arna.jcraft.forge.capability.api.JCapability;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class TexasHoldEmCapability extends CommonTexasHoldEmComponentImpl implements JCapability {

    public static Capability<TexasHoldEmCapability> TEXAS_HOLD_EM = CapabilityManager.get(new CapabilityToken<>() {
    });

    public TexasHoldEmCapability(Level world) {
        super(world);
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        super.writeToNbt(tag);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        super.readFromNbt(tag);
    }

    public static TexasHoldEmCapability getCapability(Level world) {
        return world.getCapability(TEXAS_HOLD_EM).orElse(new TexasHoldEmCapability(world));
    }
}
