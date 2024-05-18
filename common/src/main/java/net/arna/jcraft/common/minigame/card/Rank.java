package net.arna.jcraft.common.minigame.card;

import java.util.Comparator;

public enum Rank {
    // DON'T change this order
    AS,
    TWO,
    THREE,
    FOUR,
    FIVE,
    SIX,
    SEVEN,
    EIGHT,
    NINE,
    TEN,
    JACK,
    QUEEN,
    KING;

    /**
     * Puts the {@link #AS} at the end of the rank evaluation instead of the beginning.
     */
    public int pokerValue() {
        if (this == AS) {
            return 13;
        }
        return this.ordinal();
    }

    /**
     * Returns a <code>null</code>-unsafe comparator for the rank of cards, from lowest to highest.
     *
     * @param asHighest If {@link #AS} is to be seen as the highest card.
     */
    public static Comparator<Card> getComparator(final boolean asHighest) {
        if (!asHighest) {
            return Comparator.comparing(Card::rank);
        }
        return Comparator.comparing(card -> card.rank() == AS ? 13 : card.rank().ordinal());
    }

}
