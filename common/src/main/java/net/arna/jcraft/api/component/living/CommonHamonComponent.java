package net.arna.jcraft.api.component.living;

import lombok.NonNull;
import net.arna.jcraft.api.component.JComponent;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
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

    void setLastZoomPunched(final @NonNull UUID lastZoomPunched);

    int getLastZoomPunchedTick();

    void increaseLastZoomPunchedTick();

    void resetLastZoomPunched();

    @Nullable
    UUID getLastSendoed();

    void setLastSendoed(final @NonNull UUID lastSendoed);

    int getLastSendoedTick();

    void increaseLastSendoedTick();

    void resetLastSendoed();

    @Nullable
    UUID getLastSendoAired();

    void setLastSendoAired(final @NonNull UUID lastSendoAired);

    int getLastSendoAiredTick();

    void increaseLastSendoAiredTick();

    void resetLastSendoAired();

    @Nullable
    Set<UUID> getLastWaved();

    void setLastWaved(final @NonNull Set<UUID> lastSendoWaved);

    int getLastWavedTick();

    void increaseLastWavedTick();

    void resetLastWaved();

}
