package net.arna.jcraft.common.attack.core;

import lombok.Getter;

@Getter
public enum BlockableType {
    BLOCKABLE(false, false),
    NON_BLOCKABLE(true, true),
    NON_BLOCKABLE_EFFECTS_ONLY(false, true);

    private final boolean nonBlockable, nonBlockableEffects;

    BlockableType(final boolean nonBlockable, final boolean nonBlockableEffects) {
        this.nonBlockable = nonBlockable;
        this.nonBlockableEffects = nonBlockableEffects;
    }
}
