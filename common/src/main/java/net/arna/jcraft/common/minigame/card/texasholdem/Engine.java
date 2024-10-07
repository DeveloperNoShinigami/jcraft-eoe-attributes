package net.arna.jcraft.common.minigame.card.texasholdem;

import lombok.Getter;
import lombok.NonNull;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.minigame.AbstractWager;
import net.arna.jcraft.common.minigame.ImmutableWager;
import net.arna.jcraft.common.minigame.Wager;
import net.arna.jcraft.common.minigame.card.Card;
import net.arna.jcraft.common.minigame.card.Rank;
import net.arna.jcraft.common.minigame.card.Suit;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public final class Engine {

    /**
     * Compares poker ranks from highest to lowest.
     */
    public static final Comparator<Card> RANK_COMPARATOR = Rank.createComparator(true).reversed();

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
            sorted.sort(RANK_COMPARATOR);
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
                sorted.sort(RANK_COMPARATOR);
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
            sorted.sort(RANK_COMPARATOR);
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
            sorted.sort(RANK_COMPARATOR);
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
            sorted.sort(RANK_COMPARATOR);
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
                sorted.sort(RANK_COMPARATOR);
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
            sorted.sort(RANK_COMPARATOR);
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
            sorted.sort(RANK_COMPARATOR);
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

        HandEvaluator(@NonNull final Function<Collection<Card>, Optional<List<Rank>>> evaluator) {
            this.evaluator = Objects.requireNonNull(evaluator);
        }

        private static void checkCollection(@NonNull Collection<Card> cards) {
            if (cards.size() != 7) {
                throw new IllegalArgumentException("Can only evaluate a number of 7 cards!");
            }
        }

        /**
         * Maps the given cards to their ranks, without an expensive stream operation.
         * @param sorted a pre-rank-sorted list of cards
         * @param empty an empty rank list
         */
        private static void cards2Ranks(@NonNull final List<Card> sorted, @NonNull final List<Rank> empty) {
            for (Card card : sorted) {
                empty.add(card.rank());
            }
        }

        @Override
        public Optional<List<Rank>> apply(@NonNull Collection<Card> cards) {
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
            if (hand1Val.isEmpty()) { // && hand2Val.isEmpty()
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

    private final int playerCount;
    private final List<Wager> wagers;
    private final List<ImmutableWager> currentRaises;
    private final List<List<Card>> pockets;

    private Wager pot = new Wager();
    private boolean potChanged; // initialized with false

    @Getter
    private Phase phase = Phase.PRE_POCKET;
    private Integer bigBlindPlayer; // initialized with null
    private ImmutableWager bigBlind;
    private ImmutableWager currentRaise = ImmutableWager.EMPTY;

    private final List<Card> deck = Card.createPokerDeck();
    private final List<Card> burn = new ArrayList<>(3);
    private final List<Card> community = new ArrayList<>(5);

    /**
     * @throws IllegalArgumentException If there are less than 2 or more than 22 players.
     */
    public Engine(final int playerCount) {
        if (playerCount < 2) { // not enough players
            throw new IllegalArgumentException(String.format("At least 2 players are needed, %d is too few!", playerCount));
        }
        if (playerCount > 22) { // we would run out of cards
            throw new IllegalArgumentException(String.format("%d is too many players!", playerCount));
        }
        this.playerCount = playerCount;
        wagers = new ArrayList<>(playerCount);
        resetPlayerWagers();
        pockets = new ArrayList<>(playerCount);
        resetPockets();
        currentRaises = new ArrayList<>(playerCount);
        resetPlayerCurrentRaises();
    }

    public int playerCount() {
        return playerCount;
    }

    public void resetPlayerWagers() {
        wagers.clear();
        for (int player = 0; player < playerCount; player++) {
            wagers.add(new Wager());
        }
    }

    /**
     * Returns a deep, immutable copy of the wager of the specified player.
     * @throws IndexOutOfBoundsException If the specified player is smaller than 0 or greater than or equal to the player count
     * @see #playerCount()
     *
     * @implNote The immutable wagers are not cached, but they shouldn't get so big for it to matter.
     */
    @NonNull
    public ImmutableWager getWager(final int player) {
        return new ImmutableWager(wagers.get(player));
    }

    public void resetPlayerCurrentRaises() {
        currentRaises.clear();
        for (int player = 0; player < playerCount; player++) {
            currentRaises.add(ImmutableWager.EMPTY);
        }
    }

    /**
     * Increases the wager of the specified player, as well as the current raise, if it was increased.
     * @throws IndexOutOfBoundsException If the specified player is smaller than 0 or greater than or equal to the player count
     * @throws IllegalArgumentException If the raise is not an expansion of the current raise.
     * @see #playerCount()
     * @see Wager#expands(AbstractWager)
     */
    public void raise(final int player, @NonNull final ImmutableWager raise) {
        if (!raise.expands(currentRaise)) {
            throw new IllegalArgumentException(String.format("%s is not an expansion of %s!", raise, currentRaise));
        }
        currentRaises.set(player, raise);
        currentRaise = raise;
        potChanged = true;
    }

    /**
     * This method is called after the players have finished raising/calling/folding for the turn.
     * It takes care of finally putting the player raises into the pot.
     */
    public void raiseCallFoldFinished() {
        for (int player = 0; player < playerCount; player++) {
            wagers.set(player, Wager.sum(wagers.get(player), currentRaises.get(player)));
        }
        resetPlayerCurrentRaises();
        // this doesn't change the pot
    }

    public void resetPockets() {
        pockets.clear();
        for (int player = 0; player < playerCount; player++) {
            pockets.add(new ArrayList<>(2));
        }
    }

    public void calculatePot() {
        pot = Wager.sum(Wager.sum(wagers), Wager.sum(currentRaises));
        pot.sort();
    }

    /**
     * Returns a deep, immutable copy of the current pot.
     */
    // this only works well in single-threading
    @NonNull
    public ImmutableWager getPot() {
        if (potChanged) {
            calculatePot();
            potChanged = false;
        }
        return new ImmutableWager(pot);
    }

    /**
     * Returns the big blind if it was set already, otherwise an empty {@link Optional}.
     */
    @NonNull
    public Optional<ImmutableWager> getBigBlind() {
        if (bigBlind == null) {
            return Optional.empty();
        }
        return Optional.of(bigBlind);
    }

    /**
     * Sets the big blind by the specified player. Can only be set once; then returns <code>true</code>, otherwise <code>false</code>.
     * @throws IndexOutOfBoundsException If the specified player is smaller than 0 or greater than or equal to the player count
     * @see #playerCount()
     */
    public boolean setBigBlind(final int player, @NonNull final AbstractWager bigBlind) {
        if (this.bigBlind != null) {
            return false;
        }
        this.bigBlind = new ImmutableWager(bigBlind);
        bigBlindPlayer = player;
        raise(bigBlindPlayer, this.bigBlind);
        return true;
    }

    /**
     * Big blind must be set before calling this method!
     * @throws IllegalStateException If big blind isn't set.
     */
    public void dealPockets() {
        if (bigBlindPlayer == null) {
            throw new IllegalStateException("Big Blind wasn't set!");
        }
        if (phase != Phase.PRE_POCKET) {
            throw new IllegalStateException("Wrong phase " + phase.name() + " to deal pockets!");
        }
        phase = Phase.POCKET;
        Collections.shuffle(deck);
        final int smallBlindPlayer = bigBlindPlayer == 0 ? playerCount-1 : bigBlindPlayer-1;
        // deal each player 2 cards in the correct order (even though it doesn't matter)
        for (int i = 0; i < 2*playerCount; i++) {
            // we remove the last card of the deck instead of the first for slight performance increase (doesn't really matter)
            pockets.get((smallBlindPlayer + i) % playerCount).add(deck.remove(deck.size()-1));
        }
        phase = Phase.PRE_FLOP;
    }

    /**
     * Returns an immutable view of the community cards in the order they were dealt.
     */
    public List<Card> viewCommunity() {
        return Collections.unmodifiableList(community);
    }

    public void dealFlop() {
        if (phase != Phase.PRE_FLOP) {
            throw new IllegalStateException("Wrong phase " + phase.name() + " to deal flop!");
        }
        phase = Phase.FLOP;
        burn.add(deck.remove(deck.size()-1));
        for (int i = 0; i < 3; i++) {
            community.add(deck.remove(deck.size()-1));
        }
        phase = Phase.PRE_TURN;
    }

    public void dealTurn() {
        if (phase != Phase.PRE_TURN) {
            throw new IllegalStateException("Wrong phase " + phase.name() + " to deal turn!");
        }
        phase = Phase.TURN;
        burn.add(deck.remove(deck.size()-1));
        community.add(deck.remove(deck.size()-1));
        phase = Phase.PRE_RIVER;
    }

    public void dealRiver() {
        if (phase != Phase.PRE_RIVER) {
            throw new IllegalStateException("Wrong phase " + phase.name() + " to deal river!");
        }
        phase = Phase.RIVER;
        burn.add(deck.remove(deck.size()-1));
        community.add(deck.remove(deck.size()-1));
        phase = Phase.PRE_END;
    }

    public void readFromNbt(@NonNull CompoundTag tag) {
        final ListIterator<Wager> wagersIt = wagers.listIterator();
        for (final Tag wagerTag : tag.getList("wagers", Tag.TAG_COMPOUND)) {
            wagersIt.next().readFromNbt((CompoundTag)wagerTag);
        }
        final ListIterator<ImmutableWager> currentRaisesIt = currentRaises.listIterator();
        for (final Tag currentRaiseTag : tag.getList("current_raises", Tag.TAG_COMPOUND)) {
            final Wager currentRaise = new Wager();
            currentRaise.readFromNbt((CompoundTag)currentRaiseTag);
            currentRaisesIt.next();
            currentRaisesIt.set(new ImmutableWager(currentRaise));
        }
        potChanged = true;
        final CompoundTag bigBlindTag = tag.getCompound("big_blind");
        final Wager bigBlind = new Wager();
        bigBlind.readFromNbt(bigBlindTag);
        this.bigBlind = new ImmutableWager(bigBlind);
        bigBlindPlayer = tag.getInt("big_blind_player");
        final CompoundTag currentRaiseTag = tag.getCompound("current_raise");
        final Wager currentRaise = new Wager();
        currentRaise.readFromNbt(currentRaiseTag);
        this.currentRaise = new ImmutableWager(currentRaise);
        resetPockets();
        final Iterator<List<Card>> pocketsIt = pockets.iterator();
        for (final Tag pocketTag : tag.getList("pockets", Tag.TAG_LIST)) {
            final List<Card> pocket = pocketsIt.next();
            for (Tag card : (ListTag)pocketTag) {
                pocket.add(Card.decode(((IntTag)card).getAsInt()));
            }
        }
        for (final Tag burnCardTag : tag.getList("burn", Tag.TAG_INT)) {
            burn.add(Card.decode(((IntTag)burnCardTag).getAsInt()));
        }
        for (final Tag communityCardTag : tag.getList("community", Tag.TAG_INT)) {
            community.add(Card.decode(((IntTag)communityCardTag).getAsInt()));
        }
        for (final Tag deckCardTag : tag.getList("deck", Tag.TAG_INT)) {
            deck.add(Card.decode(((IntTag)deckCardTag).getAsInt()));
        }
        phase = Phase.values()[tag.getInt("phase")];
    }

    public void writeToNbt(@NonNull CompoundTag tag) {
        // we assume the player count to be known at this point
        final ListTag wagersTag = new ListTag();
        for (final Wager wager : wagers) {
            final CompoundTag wagerTag = new CompoundTag();
            wager.writeToNbt(wagerTag);
            wagersTag.add(wagerTag);
        }
        tag.put("wagers", wagersTag);
        final ListTag currentRaisesTag = new ListTag();
        for (final ImmutableWager currentRaise : currentRaises) {
            final CompoundTag currentRaiseTag = new CompoundTag();
            currentRaise.writeToNbt(currentRaiseTag);
            currentRaisesTag.add(currentRaiseTag);
        }
        tag.put("current_raises", currentRaisesTag);
        final CompoundTag bigBlindTag = new CompoundTag();
        bigBlind.writeToNbt(bigBlindTag);
        tag.put("big_blind", bigBlindTag);
        tag.put("big_blind_player", IntTag.valueOf(bigBlindPlayer));
        final CompoundTag currentRaiseTag = new CompoundTag();
        currentRaise.writeToNbt(currentRaiseTag);
        tag.put("current_raise", currentRaiseTag);
        final ListTag pocketsTag = new ListTag();
        for (final List<Card> pocket : pockets) {
            final ListTag pocketTag = new ListTag();
            for (final Card card : pocket) {
                pocketTag.add(IntTag.valueOf(card.encode()));
            }
            pocketsTag.add(pocketTag);
        }
        tag.put("pockets", pocketsTag);
        final ListTag burnTag = new ListTag();
        for (final Card card : burn) {
            burnTag.add(IntTag.valueOf(card.encode()));
        }
        tag.put("burn", burnTag);
        final ListTag communityTag = new ListTag();
        for (final Card card : community) {
            communityTag.add(IntTag.valueOf(card.encode()));
        }
        tag.put("community", communityTag);
        final ListTag deckTag = new ListTag();
        for (final Card card : deck) {
            deckTag.add(IntTag.valueOf(card.encode()));
        }
        tag.put("deck", deckTag);
        tag.put("phase", IntTag.valueOf(phase.ordinal()));
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(getClass().getSimpleName());
        sb.append("{playerCount=").append(playerCount);
        sb.append(", wagers=").append(wagers);
        sb.append(", currentRaises=").append(currentRaises);
        sb.append(", pockets=").append(pockets);
        sb.append(", pot=").append(pot);
        sb.append(", potChanged=").append(potChanged);
        sb.append(", phase=").append(phase);
        sb.append(", bigBlindPlayer=").append(bigBlindPlayer);
        sb.append(", bigBlind=").append(bigBlind);
        sb.append(", currentRaise=").append(currentRaise);
        sb.append(", deck=").append(deck);
        sb.append(", burn=").append(burn);
        sb.append(", community=").append(community);
        sb.append('}');
        return sb.toString();
    }
}
