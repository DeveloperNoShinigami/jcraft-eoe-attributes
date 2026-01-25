package net.arna.jcraft.api.component.living;

import lombok.NonNull;
import net.arna.jcraft.api.component.JComponent;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface CommonHamonComponent extends JComponent {

    float getHamonCharge();

    void setHamonCharge(final float charge);

    default void resetHamonCharge() {
        setHamonCharge(0f);
    }

    boolean isHamonizeReady();

    void setHamonizeReady(final boolean ready);

    @Nullable
    UUID getLastZoomPunched();

    int getLastZoomPunchedTick();

    void setLastZoomPunched(final @NonNull UUID lastZoomPunched, int tick);

    void resetLastZoomPunched();

    @Nullable
    UUID getLastSendoed();

    int getLastSendoedTick();

    void setLastSendoed(final @NonNull UUID lastSendoed, int tick);

    void resetLastSendoed();

}
