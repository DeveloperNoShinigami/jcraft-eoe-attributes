package net.arna.jcraft.api.component.living;

import net.arna.jcraft.api.component.JComponent;
import net.arna.jcraft.api.stand.StandType;
import net.arna.jcraft.api.stand.StandEntity;
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
