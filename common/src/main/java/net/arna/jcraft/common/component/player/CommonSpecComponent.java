package net.arna.jcraft.common.component.player;

import lombok.NonNull;
import net.arna.jcraft.api.spec.SpecType2;
import net.arna.jcraft.common.component.JComponent;
import net.arna.jcraft.common.spec.JSpec;
import org.jetbrains.annotations.Nullable;

public interface CommonSpecComponent extends JComponent {
    SpecType2 getType();

    void setType(final @NonNull SpecType2 type);

    @Nullable
    JSpec<?, ?> getSpec();
}
