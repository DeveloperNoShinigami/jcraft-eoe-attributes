package net.arna.jcraft.common.minigame.card;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public record Card(Suit suit, Rank rank) {

    public Card(final Suit suit, final Rank rank) {
        this.suit = Objects.requireNonNull(suit);
        this.rank = Objects.requireNonNull(rank);
    }

    /**
     * Transforms the card into an int value between 0 (inclusive) and 52 (exclusive).
     */
    public int encode() {
        return suit.ordinal() * 13 + rank().ordinal();
    }

    /**
     * Transform the int value (which has to be between 0 (inclusive) and 52 (exclusive)) to a card.
     *
     * @throws ArrayIndexOutOfBoundsException If the int value is out of range.
     */
    public static Card decode(int value) {
        return new Card(Suit.values()[value / 13], Rank.values()[value % 13]);
    }

    public static List<Card> createPokerDeck() {
        final List<Card> deck = new ArrayList<>(52);
        for (final Suit suit : Suit.values()) {
            for (final Rank rank : Rank.values()) {
                deck.add(new Card(suit, rank));
            }
        }
        return deck;
    }

}
