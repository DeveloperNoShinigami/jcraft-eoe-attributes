package net.arna.jcraft.fabric.common.component.impl.world;

import net.arna.jcraft.common.component.impl.world.CommonTexasHoldEmComponentImpl;
import net.arna.jcraft.fabric.common.component.world.TexasHoldEmComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;

public class TexasHoldEmComponentImpl extends CommonTexasHoldEmComponentImpl implements TexasHoldEmComponent {

    public TexasHoldEmComponentImpl(Level world) {
        super(world);
    }

    @Override
    public void readFromNbt(CompoundTag tag) {
        super.readFromNbt(tag);
    }

    @Override
    public void writeToNbt(CompoundTag tag) {
        super.writeToNbt(tag);
    }
}
