package net.arna.jcraft.common.component.living;

import net.arna.jcraft.common.component.JComponent;
import net.arna.jcraft.common.data.StandType2;
import net.arna.jcraft.common.entity.stand.StandEntity;
import org.jetbrains.annotations.Nullable;

public interface CommonStandComponent extends JComponent {
    @Nullable
    StandType2 getType();

    default void setType(final @Nullable StandType2 type) {
        setTypeAndSkin(type, 0);
    }

    void setTypeAndSkin(final @Nullable StandType2 type, final int skin);

    int getSkin();

    void setSkin(final int skin);

    boolean isTagged();

    void setTagged(boolean tagged);

    @Nullable
    StandEntity<?, ?> getStand();

    void setStand(final @Nullable StandEntity<?, ?> stand);
}
