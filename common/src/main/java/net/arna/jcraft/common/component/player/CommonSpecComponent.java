package net.arna.jcraft.common.component.player;

import lombok.NonNull;
import net.arna.jcraft.api.spec.SpecType;
import net.arna.jcraft.common.component.JComponent;
import net.arna.jcraft.common.spec.JSpec;
import org.jetbrains.annotations.Nullable;

public interface CommonSpecComponent extends JComponent {
    SpecType getType();

    void setType(final @NonNull SpecType type);

    @Nullable
    JSpec<?, ?> getSpec();
}
