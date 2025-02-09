package net.arna.jcraft.common.component.world;

import net.arna.jcraft.common.entity.stand.StandType;
import org.jetbrains.annotations.Nullable;

public interface CommonExclusiveStandsComponent {
    /**
     * Returns whether the stand is used.
     * @param standType stand type
     * @return true if the stand is used, false otherwise
     */
    boolean isStandUsed(final StandType standType);

    /**
     * Marks the previous stand as free and the current stand as used.
     * @param prev previous stand
     * @param curr current stand
     * @return true if the stand is switched, false otherwise (i.e., the current stand is already used)
     */
    boolean switchStand(@Nullable final StandType prev, @Nullable final StandType curr);
}
