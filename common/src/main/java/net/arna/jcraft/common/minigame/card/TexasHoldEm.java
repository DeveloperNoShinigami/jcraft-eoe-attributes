package net.arna.jcraft.common.minigame.card;

import net.arna.jcraft.JCraft;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public final class TexasHoldEm {

    /**
     * Compares poker ranks from highest to lowest.
     */
    public static final Comparator<Card> RANK_COMPARATOR = Rank.getComparator(true).reversed();

    /**
     * Evaluates a collection of (7) cards to a specified value with a list of relevant ranks in descending order
     * (lowest two cards after value is determined get dropped out).
     */
    public enum HandEvaluator implements Function<Collection<Card>, Optional<List<Rank>>> {

        /**
         * Highest card win. If tied, second-highest etc.
         *
         * {@link #apply(Collection)} returns the ranks of the 5 highest cards in descending order in an {@link Optional}.
         */
        HIGH_CARD(cards -> Optional.of(cards.stream().sorted(RANK_COMPARATOR).limit(5).map(Card::rank).toList())),

        /**
         * Highest pair wins. If tied, highest card of the rest, etc.
         *
         * {@link #apply(Collection)} returns the rank of the (highest) pair and then the ranks of the 3 highest remaining cards in descending order in an {@link Optional},
         * or an empty optional if there is no pair.
         */
        PAIR(cards -> {
            final List<Card> sorted = new ArrayList<>(cards);
            Collections.sort(sorted, RANK_COMPARATOR);
            final LinkedList<Rank> ranks = new LinkedList<>();
            final Iterator<Card> it = sorted.iterator();
            Card latest = it.next(); // never throws because we have 7 cards
            Card current;
            while (it.hasNext()) {
                current = it.next();
                if (latest.rank() == current.rank()) { // pair found
                    cards2Ranks(sorted, ranks);
                    // remove the pair
                    ranks.remove(latest.rank());
                    ranks.remove(current.rank());
                    // remove the two lowest cards
                    ranks.removeLast();
                    ranks.removeLast();
                    // add the pair rank at the start
                    ranks.addFirst(current.rank());
                    break;
                }
                latest = current;
            }
            if (ranks.isEmpty()) { // no match
                return Optional.empty();
            }
            return Optional.of(ranks);
        }),

        /**
         * Highest pair wins. If tied, second-highest pair, if tied again, highest remaining card.
         *
         * {@link #apply(Collection)} returns the rank of the highest pair, then the second-highest pair and then the rank of the highest remaining card in an {@link Optional},
         * or an empty optional if there is no two pairs.
         */
        TWO_PAIRS(cards -> {
            Optional<List<Rank>> firstPair = PAIR.evaluator.apply(cards);
            if (firstPair.isEmpty()) { // no pair at all
                return Optional.empty();
            }
            List<Rank> ranks = null;
            final Iterator<Rank> it = firstPair.get().iterator();
            final Rank highestPair = it.next();
            Rank latest = it.next(); // never throws because we have 4 ranks
            Rank current;
            while (it.hasNext()) {
                current = it.next();
                if (latest == current) { // pair found (lower or equal in value than the first one)
                    ranks = firstPair.get();
                    if (current == highestPair) { // equal in value, actually Four of A Kind
                        ranks.remove(current); // actually deletes highestPair, but that is fine
                        // e.g. we get from AAA7 (first A indicating the pair)
                        // to AA7 (first A indicating first pair, second A indicating second pair)
                    }
                    else { // lower pair
                        ranks.remove(latest);
                        ranks.remove(current);
                        ranks.add(1, current);
                        // e.g. we go from AKK7 to AK7
                    }
                    break;
                }
                latest = current;
            }
            if (ranks == null) { // no match in the current highest cards left
                // that means we need to check the cases like AKQ7(22) and AKQ7(72)
                // the combined case is AKQ7(77) which we ignore for two pair
                final List<Card> sorted = new ArrayList<>(cards);
                Collections.sort(sorted, RANK_COMPARATOR);
                // both cases
                if (sorted.get(4).rank() == sorted.get(5).rank() || sorted.get(5).rank() == sorted.get(6).rank()) {
                    // it is a coincidence that both cases have the same logic here
                    ranks = firstPair.get();
                    ranks.remove(3);
                    ranks.remove(2);
                    ranks.add(1, sorted.get(5).rank());
                }
            }
            return Optional.ofNullable(ranks);
        }),

        /**
         * Highest three of a kind wins. If tied, highest card of the rest, etc.
         *
         * {@link #apply(Collection)} returns the rank of the (highest) three-of-a-kind and then the ranks of the 2 highest remaining cards in descending order in an {@link Optional},
         * or an empty optional if there is no three-of-a-kind.
         */
        THREE_OF_A_KIND(cards -> {
            final List<Card> sorted = new ArrayList<>(cards);
            Collections.sort(sorted, RANK_COMPARATOR);
            final LinkedList<Rank> ranks = new LinkedList<>();
            final Iterator<Card> it = sorted.iterator();
            Card secondToLatest = it.next();
            Card latest = it.next(); // never throws because we have 7 cards
            Card current;
            while (it.hasNext()) {
                current = it.next();
                if (secondToLatest.rank() == latest.rank() && latest.rank() == current.rank()) { // three of a kind found
                    cards2Ranks(sorted, ranks);
                    // remove the three of a kind
                    ranks.remove(secondToLatest.rank());
                    ranks.remove(latest.rank());
                    ranks.remove(current.rank());
                    // remove the lowest cards
                    ranks.removeLast();
                    ranks.removeLast();
                    // add the three of a kind rank at the start
                    ranks.addFirst(current.rank());
                    break;
                }
                secondToLatest = latest;
                latest = current;
            }
            if (ranks.isEmpty()) { // no match
                return Optional.empty();
            }
            return Optional.of(ranks);
        }),

        /**
         * Highest straight wins. If tied, highest card of the rest, etc.
         *
         * {@link #apply(Collection)} returns the rank of the highest card in the straight in an {@link Optional},
         * or an empty optional if there is no straight.
         */
        STRAIGHT(cards -> {
            final List<Card> sorted = new ArrayList<>(cards);
            Collections.sort(sorted, RANK_COMPARATOR);
            final boolean[] ranks = new boolean[13]; // initialized with false
            // find all ranks once
            for (final Card card : sorted) {
                ranks[card.rank().pokerValue()-1] = true;
            }
            // find highest straight
            int highest = ranks.length-1;
            while (highest > 3) {
                boolean match = true;
                for (int i = 0; i < 5; i++) {
                    match &= ranks[highest-i];
                }
                if (match) {
                    break;
                }
                highest--;
            }
            if (highest <= 3) { // no match
                return Optional.empty();
            }
            if (highest == 12) {
                return Optional.of(List.of(Rank.AS));
            }
            return Optional.of(List.of(Rank.values()[highest+1]));
        }),

        /**
         * Highest card in the flush wins. If tied, second-highest card, etc.
         *
         * {@link #apply(Collection)} returns the ranks of the 5 highest cards in a flush in descending order in an {@link Optional},
         * or an empty optional if there is no flush.
         */
        FLUSH(cards -> {
            final List<Card> sorted = new ArrayList<>(cards);
            Collections.sort(sorted, RANK_COMPARATOR);
            final int[] counts = new int[4]; // initialized with 0
            for (final Card card : sorted) {
                counts[card.suit().ordinal()]++;
            }
            List<Rank> ranks = null;
            for (final Suit suit : Suit.values()) {
                if (counts[suit.ordinal()] >= 5) {
                    // if a flush was found (and there can be only one)
                    ranks = new LinkedList<>();
                    for (final Card card : sorted) {
                        if (card.suit() == suit) {
                            // save the 5 highest flush cards
                            ranks.add(card.rank());
                            if (ranks.size() == 5) {
                                break;
                            }
                        }
                    }
                    break;
                }
            }
            return Optional.ofNullable(ranks);
        }),

        /**
         * Highest three of a kind wins. If tied, highest pair wins.
         *
         * {@link #apply(Collection)} returns the rank of the three-of-a-kind and then the rank of the (highest) pair in an {@link Optional},
         * or an empty optional if there is no full house.
         */
        FULL_HOUSE(cards -> {
            Optional<List<Rank>> threeOfAKind = THREE_OF_A_KIND.evaluator.apply(cards);
            if (threeOfAKind.isEmpty()) { // no three of a kind at all
                return Optional.empty();
            }
            List<Rank> ranks = null;
            if (threeOfAKind.get().get(1) == threeOfAKind.get().get(2)) { // the remaining two cards are a pair
                ranks = threeOfAKind.get();
                ranks.remove(1);
            }
            if (ranks == null) { // no match in the current highest cards left
                // that means we need to check the cases like (I) AQ7(22) and (II) AQ7(72)
                // the combined case is A77(72) or A77(77) which we ignore since we can only use 5 cards
                // another special case: (III) J9777(75) gets recognized as three of a kind: 7J9(75)
                final List<Card> sorted = new ArrayList<>(cards);
                Collections.sort(sorted, RANK_COMPARATOR);
                if ((sorted.get(4).rank() == sorted.get(5).rank() && sorted.get(3).rank() != sorted.get(4).rank()) || sorted.get(5).rank() == sorted.get(6).rank()) {
                    // it is a coincidence that cases (I) and (II) have the same logic here
                    ranks = threeOfAKind.get();
                    ranks.remove(2);
                    ranks.add(1, sorted.get(5).rank());
                }
            }
            return Optional.ofNullable(ranks);
        }),

        /**
         * Highest four of a kind wins. If tied, highest remaining card.
         *
         * {@link #apply(Collection)} returns the rank of the four-of-a-kind and then the rank of the highest remaining card in an {@link Optional},
         * or an empty optional if there is no four-of-a-kind.
         */
        FOUR_OF_A_KIND(cards -> {
            final List<Card> sorted = new ArrayList<>(cards);
            Collections.sort(sorted, RANK_COMPARATOR);
            Rank fourKind = sorted.get(3).rank(); // this one must always be in the four of a kind combination
            Rank highest = sorted.get(0).rank() == fourKind ? sorted.get(4).rank() : sorted.get(0).rank();
            int count = 0;
            for (final Card card : sorted) {
                if (card.rank() == fourKind) {
                    count++;
                }
            }
            if (count >= 4) { // count > 4 should be impossible, but hey
                return Optional.of(List.of(fourKind, highest));
            }
            return Optional.empty();
        }),

        /**
         * Highest card in the straight flush wins.
         *
         * {@link #apply(Collection)} returns the rank of the highest card in the straight flush in an {@link Optional},
         * or an empty optional if there is no straight.
         */
        STRAIGHT_FLUSH(cards -> {
            final List<Card> sorted = new ArrayList<>(cards);
            Collections.sort(sorted, RANK_COMPARATOR);
            final boolean[] ranks = new boolean[13]; // initialized with false
            // find all ranks once
            for (final Card card : sorted) {
                ranks[card.rank().pokerValue()-1] = true;
            }
            // find highest straight flush
            int highest = ranks.length-1;
            outerLoop: while (highest > 3) {
                // find a straight
                boolean matchStraight = true;
                for (int i = 0; i < 5; i++) {
                    matchStraight &= ranks[highest-i];
                }
                if (matchStraight) { // check if flush
                    for (final Suit suit : Suit.values()) {
                        boolean matchFlush = true;
                        for (int i = 0; i < 5; i++) {
                            final int rank = highest == 12 && i == 0 ? 0 : highest-i+1;
                            matchFlush &= sorted.contains(new Card(suit, Rank.values()[rank]));
                            if (!matchFlush) {
                                break;
                            }
                        }
                        if (matchFlush) {
                            break outerLoop;
                        }
                    }
                }
                highest--;
            }
            if (highest <= 3) { // no match
                return Optional.empty();
            }
            if (highest == 12) {
                return Optional.of(List.of(Rank.AS));
            }
            return Optional.of(List.of(Rank.values()[highest+1]));
        });

        private final Function<Collection<Card>, Optional<List<Rank>>> evaluator;

        HandEvaluator(@NotNull final Function<Collection<Card>, Optional<List<Rank>>> evaluator) {
            this.evaluator = Objects.requireNonNull(evaluator);
        }

        private static void checkCollection(@NotNull Collection<Card> cards) {
            if (cards.size() != 7) {
                throw new IllegalArgumentException("Can only evaluate a number of 7 cards!");
            }
        }

        /**
         * Maps the given cards to their ranks, without an expensive stream operation.
         * @param sorted a pre-rank-sorted list of cards
         * @param empty an empty rank list
         */
        private static void cards2Ranks(@NotNull final List<Card> sorted, @NotNull final List<Rank> empty) {
            for (Card card : sorted) {
                empty.add(card.rank());
            }
        }

        @Override
        public Optional<List<Rank>> apply(@NotNull Collection<Card> cards) {
            checkCollection(cards);
            return evaluator.apply(cards);
        }
    }

    /**
     * Compares hands from highest to lowest. Comparing a collection with other than 7 elements or containing
     * <code>null</code> will cause exceptions.
     */
    public static final Comparator<Collection<Card>> HAND_COMPARATOR = (hand1, hand2) -> {
        final HandEvaluator[] handEvaluators = HandEvaluator.values();
        Optional<List<Rank>> hand1Val, hand2Val;
        // from highest to lowest
        for (int index = handEvaluators.length - 1; index >= 0; index--) {
            hand1Val = handEvaluators[index].apply(hand1);
            hand2Val = handEvaluators[index].apply(hand2);
            if (hand1Val.isEmpty() && hand2Val.isPresent()) { // player 2 has higher hand
                return 1; // player 1 hand is "greater" in the logic, i.e. gets sorted in later (descending)
            }
            if (hand1Val.isPresent() && hand2Val.isEmpty()) {
                return -1;
            }
            if (hand1Val.isEmpty() && hand2Val.isEmpty()) {
                continue;
            }
            // both hands have something on this level
            final List<Rank> hand1ranks = hand1Val.get();
            final List<Rank> hand2ranks = hand2Val.get();
            // sanity check
            if (hand1ranks.size() != hand2ranks.size()) {
                JCraft.LOGGER.error(String.format("Hands %s and %s lead to differently sized results in evaluator %s!", hand1, hand2, handEvaluators[index].name()));
                return 0; // we don't want to crash because of an unfair poker game
            }
            final Iterator<Rank> hand1it = hand1ranks.iterator();
            final Iterator<Rank> hand2it = hand2ranks.iterator();
            Rank current1, current2;
            while (hand1it.hasNext()) {
                current1 = hand1it.next();
                current2 = hand2it.next();
                if (current1.pokerValue() < current2.pokerValue()) {
                    return 1;
                }
                if (current1.pokerValue() > current2.pokerValue()) {
                    return -1;
                }
            }
            // both players have the same hand
            return 0;
        }
        // sanity check, we should never get here because HIGH_CARD always compares!
        JCraft.LOGGER.error(String.format("Hands %s and %s can't even be compared with HIGH_CARD!", hand1, hand2));
        return 0;
    };

    private final List<LivingEntity> players;

    public TexasHoldEm(@NotNull List<LivingEntity> players) {
        this.players = new ArrayList<>(players);

    }

}
