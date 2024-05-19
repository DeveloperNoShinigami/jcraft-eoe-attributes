package net.arna.jcraft.common.minigame.card.texasholdem;

/**
 * Phases of the {@link Game}. Pre phases are for betting (only big and small blind in case of {@link #PRE_POCKET})
 * and to ensure proper serialization.
 */
public enum Phase {

    PRE_POCKET,
    POCKET,
    PRE_FLOP,
    FLOP,
    PRE_TURN,
    TURN,
    PRE_RIVER,
    RIVER,
    PRE_END,
    END

}
