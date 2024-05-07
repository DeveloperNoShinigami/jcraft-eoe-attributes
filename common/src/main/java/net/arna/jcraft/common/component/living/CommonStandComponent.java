package net.arna.jcraft.common.component.living;

import net.arna.jcraft.common.entity.stand.StandEntity;
import net.arna.jcraft.common.entity.stand.StandType;
import org.jetbrains.annotations.Nullable;

public interface CommonStandComponent {
    @Nullable StandType getType();
    default void setType(@Nullable StandType type) {
        setTypeAndSkin(type, 0);
    }

    void setTypeAndSkin(@Nullable StandType type, int skin);

    int getSkin();
    void setSkin(int skin);

    @Nullable StandEntity<?, ?> getStand();
    void setStand(@Nullable StandEntity<?, ?> stand);
}
