package net.arna.jcraft.common.component.impl.world;

import net.arna.jcraft.common.component.world.CommonExclusiveStandsComponent;
import net.arna.jcraft.common.config.JServerConfig;
import net.arna.jcraft.common.entity.stand.StandType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.Set;

public class CommonExclusiveStandsComponentImpl implements CommonExclusiveStandsComponent {
    protected final Set<StandType> usedStands = EnumSet.noneOf(StandType.class);

    @Override
    public boolean isStandUsed(final StandType standType) {
        return JServerConfig.EXCLUSIVE_STANDS.getValue() && usedStands.contains(standType);
    }

    @Override
    public boolean switchStand(final @Nullable StandType prev, final @Nullable StandType curr) {
        if (!JServerConfig.EXCLUSIVE_STANDS.getValue()) {
            return true;
        }

        // If the current stand is already used, return false.
        if (isStandUsed(curr)) {
            return false;
        }

        // If the previous stand is not null, remove it from the used stands.
        if (prev != null) {
            usedStands.remove(prev);
        }

        // If the current stand is not null, add it to the used stands.
        if (curr != null) {
            usedStands.add(curr);
        }

        return true;
    }

    public void readFromNbt(final CompoundTag tag) {
        usedStands.clear();
        ListTag used = tag.getList("Used", Tag.TAG_STRING);
        for (Tag t : used) {
            StandType standType = StandType.valueOf(t.getAsString());
            usedStands.add(standType);
        }
    }

    public void writeToNbt(final @NotNull CompoundTag tag) {
        ListTag used = new ListTag();
        for (StandType standType : usedStands) {
            used.add(StringTag.valueOf(standType.name()));
        }
        tag.put("Used", used);
    }
}
