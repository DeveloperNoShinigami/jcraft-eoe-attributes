package net.arna.jcraft.common.minigame.card.texasholdem;

import net.arna.jcraft.common.minigame.card.Card;
import net.arna.jcraft.common.minigame.card.Rank;
import net.arna.jcraft.common.minigame.card.Suit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Tests {@link TexasHoldEm}.
 */
public class TestTexasHoldEm {

    private static final Collection<Card> STRAIGHT_FLUSH_1 = List.of(
            new Card(Suit.DIAMONDS, Rank.JACK),
            new Card(Suit.DIAMONDS, Rank.TEN),
            new Card(Suit.DIAMONDS, Rank.NINE),
            new Card(Suit.DIAMONDS, Rank.EIGHT),
            new Card(Suit.DIAMONDS, Rank.SEVEN),
            new Card(Suit.DIAMONDS, Rank.SIX),
            new Card(Suit.DIAMONDS, Rank.FIVE)
    );

    private static final Collection<Card> STRAIGHT_FLUSH_2 = List.of(
            new Card(Suit.HEARTS, Rank.JACK),
            new Card(Suit.DIAMONDS, Rank.TEN),
            new Card(Suit.DIAMONDS, Rank.EIGHT),
            new Card(Suit.DIAMONDS, Rank.NINE),
            new Card(Suit.HEARTS, Rank.FIVE),
            new Card(Suit.DIAMONDS, Rank.SEVEN),
            new Card(Suit.DIAMONDS, Rank.SIX)
    );

    private static final Collection<Card> FOUR_OF_A_KIND = List.of(
            new Card(Suit.HEARTS, Rank.JACK),
            new Card(Suit.DIAMONDS, Rank.SEVEN),
            new Card(Suit.SPADES, Rank.SEVEN),
            new Card(Suit.DIAMONDS, Rank.NINE),
            new Card(Suit.HEARTS, Rank.FIVE),
            new Card(Suit.CLUBS, Rank.SEVEN),
            new Card(Suit.HEARTS, Rank.SEVEN)
    );

    private static final Collection<Card> FULL_HOUSE = List.of(
            new Card(Suit.HEARTS, Rank.JACK),
            new Card(Suit.DIAMONDS, Rank.SEVEN),
            new Card(Suit.SPADES, Rank.TWO),
            new Card(Suit.DIAMONDS, Rank.JACK),
            new Card(Suit.HEARTS, Rank.FIVE),
            new Card(Suit.CLUBS, Rank.SEVEN),
            new Card(Suit.HEARTS, Rank.SEVEN)
    );

    private static final Collection<Card> FLUSH = List.of(
            new Card(Suit.HEARTS, Rank.JACK),
            new Card(Suit.DIAMONDS, Rank.TEN),
            new Card(Suit.DIAMONDS, Rank.AS),
            new Card(Suit.DIAMONDS, Rank.NINE),
            new Card(Suit.HEARTS, Rank.FIVE),
            new Card(Suit.DIAMONDS, Rank.SEVEN),
            new Card(Suit.DIAMONDS, Rank.SIX)
    );

    private static final Collection<Card> STRAIGHT = List.of(
            new Card(Suit.HEARTS, Rank.JACK),
            new Card(Suit.DIAMONDS, Rank.TEN),
            new Card(Suit.HEARTS, Rank.EIGHT),
            new Card(Suit.CLUBS, Rank.NINE),
            new Card(Suit.HEARTS, Rank.FIVE),
            new Card(Suit.DIAMONDS, Rank.SEVEN),
            new Card(Suit.DIAMONDS, Rank.SIX)
    );

    private static final Collection<Card> THREE_OF_A_KIND = List.of(
            new Card(Suit.HEARTS, Rank.JACK),
            new Card(Suit.DIAMONDS, Rank.SEVEN),
            new Card(Suit.SPADES, Rank.TWO),
            new Card(Suit.DIAMONDS, Rank.NINE),
            new Card(Suit.HEARTS, Rank.FIVE),
            new Card(Suit.CLUBS, Rank.SEVEN),
            new Card(Suit.HEARTS, Rank.SEVEN)
    );

    private static final Collection<Card> TWO_PAIRS = List.of(
            new Card(Suit.HEARTS, Rank.JACK),
            new Card(Suit.DIAMONDS, Rank.JACK),
            new Card(Suit.SPADES, Rank.TWO),
            new Card(Suit.DIAMONDS, Rank.NINE),
            new Card(Suit.HEARTS, Rank.FIVE),
            new Card(Suit.CLUBS, Rank.SEVEN),
            new Card(Suit.HEARTS, Rank.SEVEN)
    );

    private static final Collection<Card> PAIR = List.of(
            new Card(Suit.HEARTS, Rank.JACK),
            new Card(Suit.DIAMONDS, Rank.JACK),
            new Card(Suit.SPADES, Rank.TWO),
            new Card(Suit.DIAMONDS, Rank.NINE),
            new Card(Suit.HEARTS, Rank.FIVE),
            new Card(Suit.CLUBS, Rank.EIGHT),
            new Card(Suit.HEARTS, Rank.SEVEN)
    );

    private static final Collection<Card> HIGH_CARD = List.of(
            new Card(Suit.HEARTS, Rank.JACK),
            new Card(Suit.DIAMONDS, Rank.THREE),
            new Card(Suit.SPADES, Rank.TWO),
            new Card(Suit.DIAMONDS, Rank.NINE),
            new Card(Suit.HEARTS, Rank.FIVE),
            new Card(Suit.CLUBS, Rank.EIGHT),
            new Card(Suit.HEARTS, Rank.SEVEN)
    );

    /**
     * Tests {@link TexasHoldEm.HandEvaluator}.
     */
    @Test
    public void testHandEvaluator() {
        // test with a high card
        Assertions.assertEquals(List.of(Rank.JACK, Rank.NINE, Rank.EIGHT, Rank.SEVEN, Rank.FIVE), TexasHoldEm.HandEvaluator.HIGH_CARD.apply(HIGH_CARD).get());
        Assertions.assertEquals(Optional.empty(), TexasHoldEm.HandEvaluator.PAIR.apply(HIGH_CARD));
        Assertions.assertEquals(Optional.empty(), TexasHoldEm.HandEvaluator.TWO_PAIRS.apply(HIGH_CARD));
        Assertions.assertEquals(Optional.empty(), TexasHoldEm.HandEvaluator.THREE_OF_A_KIND.apply(HIGH_CARD));
        Assertions.assertEquals(Optional.empty(), TexasHoldEm.HandEvaluator.STRAIGHT.apply(HIGH_CARD));
        Assertions.assertEquals(Optional.empty(), TexasHoldEm.HandEvaluator.FLUSH.apply(HIGH_CARD));
        Assertions.assertEquals(Optional.empty(), TexasHoldEm.HandEvaluator.FULL_HOUSE.apply(HIGH_CARD));
        Assertions.assertEquals(Optional.empty(), TexasHoldEm.HandEvaluator.FOUR_OF_A_KIND.apply(HIGH_CARD));
        Assertions.assertEquals(Optional.empty(), TexasHoldEm.HandEvaluator.STRAIGHT_FLUSH.apply(HIGH_CARD));
        // test with a pair
        Assertions.assertEquals(List.of(Rank.JACK, Rank.JACK, Rank.NINE, Rank.EIGHT, Rank.SEVEN), TexasHoldEm.HandEvaluator.HIGH_CARD.apply(PAIR).get());
        Assertions.assertEquals(List.of(Rank.JACK, Rank.NINE, Rank.EIGHT, Rank.SEVEN), TexasHoldEm.HandEvaluator.PAIR.apply(PAIR).get());
        Assertions.assertEquals(Optional.empty(), TexasHoldEm.HandEvaluator.TWO_PAIRS.apply(PAIR));
        Assertions.assertEquals(Optional.empty(), TexasHoldEm.HandEvaluator.THREE_OF_A_KIND.apply(PAIR));
        Assertions.assertEquals(Optional.empty(), TexasHoldEm.HandEvaluator.STRAIGHT.apply(PAIR));
        Assertions.assertEquals(Optional.empty(), TexasHoldEm.HandEvaluator.FLUSH.apply(PAIR));
        Assertions.assertEquals(Optional.empty(), TexasHoldEm.HandEvaluator.FULL_HOUSE.apply(PAIR));
        Assertions.assertEquals(Optional.empty(), TexasHoldEm.HandEvaluator.FOUR_OF_A_KIND.apply(PAIR));
        Assertions.assertEquals(Optional.empty(), TexasHoldEm.HandEvaluator.STRAIGHT_FLUSH.apply(PAIR));
        // test with two pairs
        Assertions.assertEquals(List.of(Rank.JACK, Rank.JACK, Rank.NINE, Rank.SEVEN, Rank.SEVEN), TexasHoldEm.HandEvaluator.HIGH_CARD.apply(TWO_PAIRS).get());
        Assertions.assertEquals(List.of(Rank.JACK, Rank.NINE, Rank.SEVEN, Rank.SEVEN), TexasHoldEm.HandEvaluator.PAIR.apply(TWO_PAIRS).get());
        Assertions.assertEquals(List.of(Rank.JACK, Rank.SEVEN, Rank.NINE), TexasHoldEm.HandEvaluator.TWO_PAIRS.apply(TWO_PAIRS).get());
        Assertions.assertEquals(Optional.empty(), TexasHoldEm.HandEvaluator.THREE_OF_A_KIND.apply(TWO_PAIRS));
        Assertions.assertEquals(Optional.empty(), TexasHoldEm.HandEvaluator.STRAIGHT.apply(TWO_PAIRS));
        Assertions.assertEquals(Optional.empty(), TexasHoldEm.HandEvaluator.FLUSH.apply(TWO_PAIRS));
        Assertions.assertEquals(Optional.empty(), TexasHoldEm.HandEvaluator.FULL_HOUSE.apply(TWO_PAIRS));
        Assertions.assertEquals(Optional.empty(), TexasHoldEm.HandEvaluator.FOUR_OF_A_KIND.apply(TWO_PAIRS));
        Assertions.assertEquals(Optional.empty(), TexasHoldEm.HandEvaluator.STRAIGHT_FLUSH.apply(TWO_PAIRS));
        // test with three of a kind
        Assertions.assertEquals(List.of(Rank.JACK, Rank.NINE, Rank.SEVEN, Rank.SEVEN, Rank.SEVEN), TexasHoldEm.HandEvaluator.HIGH_CARD.apply(THREE_OF_A_KIND).get());
        Assertions.assertEquals(List.of(Rank.SEVEN, Rank.JACK, Rank.NINE, Rank.SEVEN), TexasHoldEm.HandEvaluator.PAIR.apply(THREE_OF_A_KIND).get());
        Assertions.assertEquals(Optional.empty(), TexasHoldEm.HandEvaluator.TWO_PAIRS.apply(THREE_OF_A_KIND));
        Assertions.assertEquals(List.of(Rank.SEVEN, Rank.JACK, Rank.NINE), TexasHoldEm.HandEvaluator.THREE_OF_A_KIND.apply(THREE_OF_A_KIND).get());
        Assertions.assertEquals(Optional.empty(), TexasHoldEm.HandEvaluator.STRAIGHT.apply(THREE_OF_A_KIND));
        Assertions.assertEquals(Optional.empty(), TexasHoldEm.HandEvaluator.FLUSH.apply(THREE_OF_A_KIND));
        Assertions.assertEquals(Optional.empty(), TexasHoldEm.HandEvaluator.FULL_HOUSE.apply(THREE_OF_A_KIND));
        Assertions.assertEquals(Optional.empty(), TexasHoldEm.HandEvaluator.FOUR_OF_A_KIND.apply(THREE_OF_A_KIND));
        Assertions.assertEquals(Optional.empty(), TexasHoldEm.HandEvaluator.STRAIGHT_FLUSH.apply(THREE_OF_A_KIND));
        // test with straight
        Assertions.assertEquals(List.of(Rank.JACK, Rank.TEN, Rank.NINE, Rank.EIGHT, Rank.SEVEN), TexasHoldEm.HandEvaluator.HIGH_CARD.apply(STRAIGHT).get());
        Assertions.assertEquals(Optional.empty(), TexasHoldEm.HandEvaluator.PAIR.apply(STRAIGHT));
        Assertions.assertEquals(Optional.empty(), TexasHoldEm.HandEvaluator.TWO_PAIRS.apply(STRAIGHT));
        Assertions.assertEquals(Optional.empty(), TexasHoldEm.HandEvaluator.THREE_OF_A_KIND.apply(STRAIGHT));
        Assertions.assertEquals(List.of(Rank.JACK), TexasHoldEm.HandEvaluator.STRAIGHT.apply(STRAIGHT).get());
        Assertions.assertEquals(Optional.empty(), TexasHoldEm.HandEvaluator.FLUSH.apply(STRAIGHT));
        Assertions.assertEquals(Optional.empty(), TexasHoldEm.HandEvaluator.FULL_HOUSE.apply(STRAIGHT));
        Assertions.assertEquals(Optional.empty(), TexasHoldEm.HandEvaluator.FOUR_OF_A_KIND.apply(STRAIGHT));
        Assertions.assertEquals(Optional.empty(), TexasHoldEm.HandEvaluator.STRAIGHT_FLUSH.apply(STRAIGHT));
        // test with flush
        Assertions.assertEquals(List.of(Rank.AS, Rank.JACK, Rank.TEN, Rank.NINE, Rank.SEVEN), TexasHoldEm.HandEvaluator.HIGH_CARD.apply(FLUSH).get());
        Assertions.assertEquals(Optional.empty(), TexasHoldEm.HandEvaluator.PAIR.apply(FLUSH));
        Assertions.assertEquals(Optional.empty(), TexasHoldEm.HandEvaluator.TWO_PAIRS.apply(FLUSH));
        Assertions.assertEquals(Optional.empty(), TexasHoldEm.HandEvaluator.THREE_OF_A_KIND.apply(FLUSH));
        Assertions.assertEquals(Optional.empty(), TexasHoldEm.HandEvaluator.STRAIGHT.apply(FLUSH));
        Assertions.assertEquals(List.of(Rank.AS, Rank.TEN, Rank.NINE, Rank.SEVEN, Rank.SIX), TexasHoldEm.HandEvaluator.FLUSH.apply(FLUSH).get());
        Assertions.assertEquals(Optional.empty(), TexasHoldEm.HandEvaluator.FULL_HOUSE.apply(FLUSH));
        Assertions.assertEquals(Optional.empty(), TexasHoldEm.HandEvaluator.FOUR_OF_A_KIND.apply(FLUSH));
        Assertions.assertEquals(Optional.empty(), TexasHoldEm.HandEvaluator.STRAIGHT_FLUSH.apply(FLUSH));
        // test with full house
        Assertions.assertEquals(List.of(Rank.JACK, Rank.JACK, Rank.SEVEN, Rank.SEVEN, Rank.SEVEN), TexasHoldEm.HandEvaluator.HIGH_CARD.apply(FULL_HOUSE).get());
        Assertions.assertEquals(List.of(Rank.JACK, Rank.SEVEN, Rank.SEVEN, Rank.SEVEN), TexasHoldEm.HandEvaluator.PAIR.apply(FULL_HOUSE).get());
        Assertions.assertEquals(List.of(Rank.JACK, Rank.SEVEN, Rank.SEVEN), TexasHoldEm.HandEvaluator.TWO_PAIRS.apply(FULL_HOUSE).get());
        Assertions.assertEquals(List.of(Rank.SEVEN, Rank.JACK, Rank.JACK), TexasHoldEm.HandEvaluator.THREE_OF_A_KIND.apply(FULL_HOUSE).get());
        Assertions.assertEquals(Optional.empty(), TexasHoldEm.HandEvaluator.STRAIGHT.apply(FULL_HOUSE));
        Assertions.assertEquals(Optional.empty(), TexasHoldEm.HandEvaluator.FLUSH.apply(FULL_HOUSE));
        Assertions.assertEquals(List.of(Rank.SEVEN, Rank.JACK), TexasHoldEm.HandEvaluator.FULL_HOUSE.apply(FULL_HOUSE).get());
        Assertions.assertEquals(Optional.empty(), TexasHoldEm.HandEvaluator.FOUR_OF_A_KIND.apply(FULL_HOUSE));
        Assertions.assertEquals(Optional.empty(), TexasHoldEm.HandEvaluator.STRAIGHT_FLUSH.apply(FULL_HOUSE));
        // test with four of a kind
        Assertions.assertEquals(List.of(Rank.JACK, Rank.NINE, Rank.SEVEN, Rank.SEVEN, Rank.SEVEN), TexasHoldEm.HandEvaluator.HIGH_CARD.apply(FOUR_OF_A_KIND).get());
        Assertions.assertEquals(List.of(Rank.SEVEN, Rank.JACK, Rank.NINE, Rank.SEVEN), TexasHoldEm.HandEvaluator.PAIR.apply(FOUR_OF_A_KIND).get());
        Assertions.assertEquals(List.of(Rank.SEVEN, Rank.SEVEN, Rank.JACK), TexasHoldEm.HandEvaluator.TWO_PAIRS.apply(FOUR_OF_A_KIND).get());
        Assertions.assertEquals(List.of(Rank.SEVEN, Rank.JACK, Rank.NINE), TexasHoldEm.HandEvaluator.THREE_OF_A_KIND.apply(FOUR_OF_A_KIND).get());
        Assertions.assertEquals(Optional.empty(), TexasHoldEm.HandEvaluator.STRAIGHT.apply(FOUR_OF_A_KIND));
        Assertions.assertEquals(Optional.empty(), TexasHoldEm.HandEvaluator.FLUSH.apply(FOUR_OF_A_KIND));
        Assertions.assertEquals(Optional.empty(), TexasHoldEm.HandEvaluator.FULL_HOUSE.apply(FOUR_OF_A_KIND));
        Assertions.assertEquals(List.of(Rank.SEVEN, Rank.JACK), TexasHoldEm.HandEvaluator.FOUR_OF_A_KIND.apply(FOUR_OF_A_KIND).get());
        Assertions.assertEquals(Optional.empty(), TexasHoldEm.HandEvaluator.STRAIGHT_FLUSH.apply(FOUR_OF_A_KIND));
        // test with straight flush 1
        Assertions.assertEquals(List.of(Rank.JACK, Rank.TEN, Rank.NINE, Rank.EIGHT, Rank.SEVEN), TexasHoldEm.HandEvaluator.HIGH_CARD.apply(STRAIGHT_FLUSH_1).get());
        Assertions.assertEquals(Optional.empty(), TexasHoldEm.HandEvaluator.PAIR.apply(STRAIGHT_FLUSH_1));
        Assertions.assertEquals(Optional.empty(), TexasHoldEm.HandEvaluator.TWO_PAIRS.apply(STRAIGHT_FLUSH_1));
        Assertions.assertEquals(Optional.empty(), TexasHoldEm.HandEvaluator.THREE_OF_A_KIND.apply(STRAIGHT_FLUSH_1));
        Assertions.assertEquals(List.of(Rank.JACK), TexasHoldEm.HandEvaluator.STRAIGHT.apply(STRAIGHT_FLUSH_1).get());
        Assertions.assertEquals(List.of(Rank.JACK, Rank.TEN, Rank.NINE, Rank.EIGHT, Rank.SEVEN), TexasHoldEm.HandEvaluator.FLUSH.apply(STRAIGHT_FLUSH_1).get());
        Assertions.assertEquals(Optional.empty(), TexasHoldEm.HandEvaluator.FULL_HOUSE.apply(STRAIGHT_FLUSH_1));
        Assertions.assertEquals(Optional.empty(), TexasHoldEm.HandEvaluator.FOUR_OF_A_KIND.apply(STRAIGHT_FLUSH_1));
        Assertions.assertEquals(List.of(Rank.JACK), TexasHoldEm.HandEvaluator.STRAIGHT_FLUSH.apply(STRAIGHT_FLUSH_1).get());
        // test with straight flush 2
        Assertions.assertEquals(List.of(Rank.JACK, Rank.TEN, Rank.NINE, Rank.EIGHT, Rank.SEVEN), TexasHoldEm.HandEvaluator.HIGH_CARD.apply(STRAIGHT_FLUSH_2).get());
        Assertions.assertEquals(Optional.empty(), TexasHoldEm.HandEvaluator.PAIR.apply(STRAIGHT_FLUSH_2));
        Assertions.assertEquals(Optional.empty(), TexasHoldEm.HandEvaluator.TWO_PAIRS.apply(STRAIGHT_FLUSH_2));
        Assertions.assertEquals(Optional.empty(), TexasHoldEm.HandEvaluator.THREE_OF_A_KIND.apply(STRAIGHT_FLUSH_2));
        Assertions.assertEquals(List.of(Rank.JACK), TexasHoldEm.HandEvaluator.STRAIGHT.apply(STRAIGHT_FLUSH_2).get());
        Assertions.assertEquals(List.of(Rank.TEN, Rank.NINE, Rank.EIGHT, Rank.SEVEN, Rank.SIX), TexasHoldEm.HandEvaluator.FLUSH.apply(STRAIGHT_FLUSH_2).get());
        Assertions.assertEquals(Optional.empty(), TexasHoldEm.HandEvaluator.FULL_HOUSE.apply(STRAIGHT_FLUSH_2));
        Assertions.assertEquals(Optional.empty(), TexasHoldEm.HandEvaluator.FOUR_OF_A_KIND.apply(STRAIGHT_FLUSH_2));
        Assertions.assertEquals(List.of(Rank.TEN), TexasHoldEm.HandEvaluator.STRAIGHT_FLUSH.apply(STRAIGHT_FLUSH_2).get());
    }

    /**
     * Tests {@link TexasHoldEm#HAND_COMPARATOR}.
     */
    @Test
    public void testHandComparator() {
        // cmp. straight flush 1
        Assertions.assertEquals(0, TexasHoldEm.HAND_COMPARATOR.compare(STRAIGHT_FLUSH_1, STRAIGHT_FLUSH_1));
        Assertions.assertTrue(0 > TexasHoldEm.HAND_COMPARATOR.compare(STRAIGHT_FLUSH_1, STRAIGHT_FLUSH_2));
        Assertions.assertTrue(0 > TexasHoldEm.HAND_COMPARATOR.compare(STRAIGHT_FLUSH_1, FOUR_OF_A_KIND));
        Assertions.assertTrue(0 > TexasHoldEm.HAND_COMPARATOR.compare(STRAIGHT_FLUSH_1, FULL_HOUSE));
        Assertions.assertTrue(0 > TexasHoldEm.HAND_COMPARATOR.compare(STRAIGHT_FLUSH_1, FLUSH));
        Assertions.assertTrue(0 > TexasHoldEm.HAND_COMPARATOR.compare(STRAIGHT_FLUSH_1, STRAIGHT));
        Assertions.assertTrue(0 > TexasHoldEm.HAND_COMPARATOR.compare(STRAIGHT_FLUSH_1, THREE_OF_A_KIND));
        Assertions.assertTrue(0 > TexasHoldEm.HAND_COMPARATOR.compare(STRAIGHT_FLUSH_1, TWO_PAIRS));
        Assertions.assertTrue(0 > TexasHoldEm.HAND_COMPARATOR.compare(STRAIGHT_FLUSH_1, PAIR));
        Assertions.assertTrue(0 > TexasHoldEm.HAND_COMPARATOR.compare(STRAIGHT_FLUSH_1, HIGH_CARD));
        // cmp. straight flush 2
        Assertions.assertTrue(0 < TexasHoldEm.HAND_COMPARATOR.compare(STRAIGHT_FLUSH_2, STRAIGHT_FLUSH_1));
        Assertions.assertEquals(0, TexasHoldEm.HAND_COMPARATOR.compare(STRAIGHT_FLUSH_2, STRAIGHT_FLUSH_2));
        Assertions.assertTrue(0 > TexasHoldEm.HAND_COMPARATOR.compare(STRAIGHT_FLUSH_2, FOUR_OF_A_KIND));
        Assertions.assertTrue(0 > TexasHoldEm.HAND_COMPARATOR.compare(STRAIGHT_FLUSH_2, FULL_HOUSE));
        Assertions.assertTrue(0 > TexasHoldEm.HAND_COMPARATOR.compare(STRAIGHT_FLUSH_2, FLUSH));
        Assertions.assertTrue(0 > TexasHoldEm.HAND_COMPARATOR.compare(STRAIGHT_FLUSH_2, STRAIGHT));
        Assertions.assertTrue(0 > TexasHoldEm.HAND_COMPARATOR.compare(STRAIGHT_FLUSH_2, THREE_OF_A_KIND));
        Assertions.assertTrue(0 > TexasHoldEm.HAND_COMPARATOR.compare(STRAIGHT_FLUSH_2, TWO_PAIRS));
        Assertions.assertTrue(0 > TexasHoldEm.HAND_COMPARATOR.compare(STRAIGHT_FLUSH_2, PAIR));
        Assertions.assertTrue(0 > TexasHoldEm.HAND_COMPARATOR.compare(STRAIGHT_FLUSH_2, HIGH_CARD));
        // cmp. four of a kind
        Assertions.assertTrue(0 < TexasHoldEm.HAND_COMPARATOR.compare(FOUR_OF_A_KIND, STRAIGHT_FLUSH_1));
        Assertions.assertTrue(0 < TexasHoldEm.HAND_COMPARATOR.compare(FOUR_OF_A_KIND, STRAIGHT_FLUSH_2));
        Assertions.assertEquals(0, TexasHoldEm.HAND_COMPARATOR.compare(FOUR_OF_A_KIND, FOUR_OF_A_KIND));
        Assertions.assertTrue(0 > TexasHoldEm.HAND_COMPARATOR.compare(FOUR_OF_A_KIND, FULL_HOUSE));
        Assertions.assertTrue(0 > TexasHoldEm.HAND_COMPARATOR.compare(FOUR_OF_A_KIND, FLUSH));
        Assertions.assertTrue(0 > TexasHoldEm.HAND_COMPARATOR.compare(FOUR_OF_A_KIND, STRAIGHT));
        Assertions.assertTrue(0 > TexasHoldEm.HAND_COMPARATOR.compare(FOUR_OF_A_KIND, THREE_OF_A_KIND));
        Assertions.assertTrue(0 > TexasHoldEm.HAND_COMPARATOR.compare(FOUR_OF_A_KIND, TWO_PAIRS));
        Assertions.assertTrue(0 > TexasHoldEm.HAND_COMPARATOR.compare(FOUR_OF_A_KIND, PAIR));
        Assertions.assertTrue(0 > TexasHoldEm.HAND_COMPARATOR.compare(FOUR_OF_A_KIND, HIGH_CARD));
        // cmp. full house
        Assertions.assertTrue(0 < TexasHoldEm.HAND_COMPARATOR.compare(FULL_HOUSE, STRAIGHT_FLUSH_1));
        Assertions.assertTrue(0 < TexasHoldEm.HAND_COMPARATOR.compare(FULL_HOUSE, STRAIGHT_FLUSH_2));
        Assertions.assertTrue(0 < TexasHoldEm.HAND_COMPARATOR.compare(FULL_HOUSE, FOUR_OF_A_KIND));
        Assertions.assertEquals(0, TexasHoldEm.HAND_COMPARATOR.compare(FULL_HOUSE, FULL_HOUSE));
        Assertions.assertTrue(0 > TexasHoldEm.HAND_COMPARATOR.compare(FULL_HOUSE, FLUSH));
        Assertions.assertTrue(0 > TexasHoldEm.HAND_COMPARATOR.compare(FULL_HOUSE, STRAIGHT));
        Assertions.assertTrue(0 > TexasHoldEm.HAND_COMPARATOR.compare(FULL_HOUSE, THREE_OF_A_KIND));
        Assertions.assertTrue(0 > TexasHoldEm.HAND_COMPARATOR.compare(FULL_HOUSE, TWO_PAIRS));
        Assertions.assertTrue(0 > TexasHoldEm.HAND_COMPARATOR.compare(FULL_HOUSE, PAIR));
        Assertions.assertTrue(0 > TexasHoldEm.HAND_COMPARATOR.compare(FULL_HOUSE, HIGH_CARD));
        // cmp. flush
        Assertions.assertTrue(0 < TexasHoldEm.HAND_COMPARATOR.compare(FLUSH, STRAIGHT_FLUSH_1));
        Assertions.assertTrue(0 < TexasHoldEm.HAND_COMPARATOR.compare(FLUSH, STRAIGHT_FLUSH_2));
        Assertions.assertTrue(0 < TexasHoldEm.HAND_COMPARATOR.compare(FLUSH, FOUR_OF_A_KIND));
        Assertions.assertTrue(0 < TexasHoldEm.HAND_COMPARATOR.compare(FLUSH, FULL_HOUSE));
        Assertions.assertEquals(0, TexasHoldEm.HAND_COMPARATOR.compare(FLUSH, FLUSH));
        Assertions.assertTrue(0 > TexasHoldEm.HAND_COMPARATOR.compare(FLUSH, STRAIGHT));
        Assertions.assertTrue(0 > TexasHoldEm.HAND_COMPARATOR.compare(FLUSH, THREE_OF_A_KIND));
        Assertions.assertTrue(0 > TexasHoldEm.HAND_COMPARATOR.compare(FLUSH, TWO_PAIRS));
        Assertions.assertTrue(0 > TexasHoldEm.HAND_COMPARATOR.compare(FLUSH, PAIR));
        Assertions.assertTrue(0 > TexasHoldEm.HAND_COMPARATOR.compare(FLUSH, HIGH_CARD));
        // cmp. straight
        Assertions.assertTrue(0 < TexasHoldEm.HAND_COMPARATOR.compare(STRAIGHT, STRAIGHT_FLUSH_1));
        Assertions.assertTrue(0 < TexasHoldEm.HAND_COMPARATOR.compare(STRAIGHT, STRAIGHT_FLUSH_2));
        Assertions.assertTrue(0 < TexasHoldEm.HAND_COMPARATOR.compare(STRAIGHT, FOUR_OF_A_KIND));
        Assertions.assertTrue(0 < TexasHoldEm.HAND_COMPARATOR.compare(STRAIGHT, FULL_HOUSE));
        Assertions.assertTrue(0 < TexasHoldEm.HAND_COMPARATOR.compare(STRAIGHT, FLUSH));
        Assertions.assertEquals(0, TexasHoldEm.HAND_COMPARATOR.compare(STRAIGHT, STRAIGHT));
        Assertions.assertTrue(0 > TexasHoldEm.HAND_COMPARATOR.compare(STRAIGHT, THREE_OF_A_KIND));
        Assertions.assertTrue(0 > TexasHoldEm.HAND_COMPARATOR.compare(STRAIGHT, TWO_PAIRS));
        Assertions.assertTrue(0 > TexasHoldEm.HAND_COMPARATOR.compare(STRAIGHT, PAIR));
        Assertions.assertTrue(0 > TexasHoldEm.HAND_COMPARATOR.compare(STRAIGHT, HIGH_CARD));
        // cmp. three of a kind
        Assertions.assertTrue(0 < TexasHoldEm.HAND_COMPARATOR.compare(THREE_OF_A_KIND, STRAIGHT_FLUSH_1));
        Assertions.assertTrue(0 < TexasHoldEm.HAND_COMPARATOR.compare(THREE_OF_A_KIND, STRAIGHT_FLUSH_2));
        Assertions.assertTrue(0 < TexasHoldEm.HAND_COMPARATOR.compare(THREE_OF_A_KIND, FOUR_OF_A_KIND));
        Assertions.assertTrue(0 < TexasHoldEm.HAND_COMPARATOR.compare(THREE_OF_A_KIND, FULL_HOUSE));
        Assertions.assertTrue(0 < TexasHoldEm.HAND_COMPARATOR.compare(THREE_OF_A_KIND, FLUSH));
        Assertions.assertTrue(0 < TexasHoldEm.HAND_COMPARATOR.compare(THREE_OF_A_KIND, STRAIGHT));
        Assertions.assertEquals(0, TexasHoldEm.HAND_COMPARATOR.compare(THREE_OF_A_KIND, THREE_OF_A_KIND));
        Assertions.assertTrue(0 > TexasHoldEm.HAND_COMPARATOR.compare(THREE_OF_A_KIND, TWO_PAIRS));
        Assertions.assertTrue(0 > TexasHoldEm.HAND_COMPARATOR.compare(THREE_OF_A_KIND, PAIR));
        Assertions.assertTrue(0 > TexasHoldEm.HAND_COMPARATOR.compare(THREE_OF_A_KIND, HIGH_CARD));
        // cmp. two pairs
        Assertions.assertTrue(0 < TexasHoldEm.HAND_COMPARATOR.compare(TWO_PAIRS, STRAIGHT_FLUSH_1));
        Assertions.assertTrue(0 < TexasHoldEm.HAND_COMPARATOR.compare(TWO_PAIRS, STRAIGHT_FLUSH_2));
        Assertions.assertTrue(0 < TexasHoldEm.HAND_COMPARATOR.compare(TWO_PAIRS, FOUR_OF_A_KIND));
        Assertions.assertTrue(0 < TexasHoldEm.HAND_COMPARATOR.compare(TWO_PAIRS, FULL_HOUSE));
        Assertions.assertTrue(0 < TexasHoldEm.HAND_COMPARATOR.compare(TWO_PAIRS, FLUSH));
        Assertions.assertTrue(0 < TexasHoldEm.HAND_COMPARATOR.compare(TWO_PAIRS, STRAIGHT));
        Assertions.assertTrue(0 < TexasHoldEm.HAND_COMPARATOR.compare(TWO_PAIRS, THREE_OF_A_KIND));
        Assertions.assertEquals(0, TexasHoldEm.HAND_COMPARATOR.compare(TWO_PAIRS, TWO_PAIRS));
        Assertions.assertTrue(0 > TexasHoldEm.HAND_COMPARATOR.compare(TWO_PAIRS, PAIR));
        Assertions.assertTrue(0 > TexasHoldEm.HAND_COMPARATOR.compare(TWO_PAIRS, HIGH_CARD));
        // cmp. pair
        Assertions.assertTrue(0 < TexasHoldEm.HAND_COMPARATOR.compare(PAIR, STRAIGHT_FLUSH_1));
        Assertions.assertTrue(0 < TexasHoldEm.HAND_COMPARATOR.compare(PAIR, STRAIGHT_FLUSH_2));
        Assertions.assertTrue(0 < TexasHoldEm.HAND_COMPARATOR.compare(PAIR, FOUR_OF_A_KIND));
        Assertions.assertTrue(0 < TexasHoldEm.HAND_COMPARATOR.compare(PAIR, FULL_HOUSE));
        Assertions.assertTrue(0 < TexasHoldEm.HAND_COMPARATOR.compare(PAIR, FLUSH));
        Assertions.assertTrue(0 < TexasHoldEm.HAND_COMPARATOR.compare(PAIR, STRAIGHT));
        Assertions.assertTrue(0 < TexasHoldEm.HAND_COMPARATOR.compare(PAIR, THREE_OF_A_KIND));
        Assertions.assertTrue(0 < TexasHoldEm.HAND_COMPARATOR.compare(PAIR, TWO_PAIRS));
        Assertions.assertEquals(0, TexasHoldEm.HAND_COMPARATOR.compare(PAIR, PAIR));
        Assertions.assertTrue(0 > TexasHoldEm.HAND_COMPARATOR.compare(PAIR, HIGH_CARD));
        // cmp. high card
        Assertions.assertTrue(0 < TexasHoldEm.HAND_COMPARATOR.compare(HIGH_CARD, STRAIGHT_FLUSH_1));
        Assertions.assertTrue(0 < TexasHoldEm.HAND_COMPARATOR.compare(HIGH_CARD, STRAIGHT_FLUSH_2));
        Assertions.assertTrue(0 < TexasHoldEm.HAND_COMPARATOR.compare(HIGH_CARD, FOUR_OF_A_KIND));
        Assertions.assertTrue(0 < TexasHoldEm.HAND_COMPARATOR.compare(HIGH_CARD, FULL_HOUSE));
        Assertions.assertTrue(0 < TexasHoldEm.HAND_COMPARATOR.compare(HIGH_CARD, FLUSH));
        Assertions.assertTrue(0 < TexasHoldEm.HAND_COMPARATOR.compare(HIGH_CARD, STRAIGHT));
        Assertions.assertTrue(0 < TexasHoldEm.HAND_COMPARATOR.compare(HIGH_CARD, THREE_OF_A_KIND));
        Assertions.assertTrue(0 < TexasHoldEm.HAND_COMPARATOR.compare(HIGH_CARD, TWO_PAIRS));
        Assertions.assertTrue(0 < TexasHoldEm.HAND_COMPARATOR.compare(HIGH_CARD, PAIR));
        Assertions.assertEquals(0, TexasHoldEm.HAND_COMPARATOR.compare(HIGH_CARD, HIGH_CARD));
    }

}
