package net.arna.jcraft.common.util;

import net.arna.jcraft.api.spec.JSpec;

public interface SpecAnimationState<S extends JSpec<S, ?>> {
    String getKey(S spec);
}
