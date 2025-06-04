package net.arna.jcraft.common.component.living;

import net.arna.jcraft.common.component.JComponent;
import net.arna.jcraft.api.StandType;
import net.arna.jcraft.common.entity.stand.StandEntity;
import org.jetbrains.annotations.Nullable;

public interface CommonStandComponent extends JComponent {
    @Nullable
    StandType getType();

    default void setType(final @Nullable StandType type) {
        setTypeAndSkin(type, 0);
    }

    void setTypeAndSkin(final @Nullable StandType type, final int skin);

    int getSkin();

    void setSkin(final int skin);

    boolean isTagged();

    void setTagged(boolean tagged);

    @Nullable
    StandEntity<?, ?> getStand();

    void setStand(final @Nullable StandEntity<?, ?> stand);
}
