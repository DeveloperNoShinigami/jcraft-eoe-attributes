package net.arna.jcraft.common.entity.spec;

import net.arna.jcraft.api.spec.JSpec;
import net.arna.jcraft.common.util.SpecAnimationState;

public interface JSpecHolder {
    void setSpec(JSpec<?, ?> spec);
    JSpec<?, ?> getSpec();
    void setAnimation(String animationID, float animationSpeed);
    SpecAnimationState<?> getState();
}
