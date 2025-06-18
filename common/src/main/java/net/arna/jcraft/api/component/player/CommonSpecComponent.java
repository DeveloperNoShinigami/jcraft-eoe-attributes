package net.arna.jcraft.api.component.player;

import net.arna.jcraft.api.spec.SpecType;
import net.arna.jcraft.api.component.JComponent;
import net.arna.jcraft.api.spec.JSpec;
import org.jetbrains.annotations.Nullable;

public interface CommonSpecComponent extends JComponent {
    SpecType getType();

    void setType(final SpecType type);

    @Nullable
    JSpec<?, ?> getSpec();
}
