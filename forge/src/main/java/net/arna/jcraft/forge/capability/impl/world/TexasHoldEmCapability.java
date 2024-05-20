package net.arna.jcraft.forge.capability.impl.world;

import net.arna.jcraft.common.component.impl.world.CommonTexasHoldEmComponentImpl;
import net.arna.jcraft.forge.capability.api.JCapability;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;

public class TexasHoldEmCapability extends CommonTexasHoldEmComponentImpl implements JCapability {

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
}
