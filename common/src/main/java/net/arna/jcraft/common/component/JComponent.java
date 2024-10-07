package net.arna.jcraft.common.component;

import lombok.NonNull;
import net.minecraft.nbt.CompoundTag;

public interface JComponent {
    void readFromNbt(final @NonNull CompoundTag tag);

    void writeToNbt(final @NonNull CompoundTag tag);
}
