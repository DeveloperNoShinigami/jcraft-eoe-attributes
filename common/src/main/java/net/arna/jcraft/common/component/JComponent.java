package net.arna.jcraft.common.component;

import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;

public interface JComponent {
    void readFromNbt(final @NotNull CompoundTag tag);

    void writeToNbt(final @NotNull CompoundTag tag);
}
