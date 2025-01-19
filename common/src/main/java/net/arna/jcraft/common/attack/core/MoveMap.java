package net.arna.jcraft.common.attack.core;

import com.google.common.collect.*;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.arna.jcraft.common.attack.core.data.MoveSetLoader;
import net.arna.jcraft.common.attack.moves.base.AbstractMove;
import net.arna.jcraft.common.util.CooldownType;
import net.arna.jcraft.common.util.JCodecUtils;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Stream;

@NoArgsConstructor
public class MoveMap<A extends IAttacker<? extends A, S>, S extends Enum<?>> implements Iterable<MoveMap.Entry<A, S>> {
    private final ListMultimap<MoveClass, Entry<A, S>> entries = MultimapBuilder.enumKeys(MoveClass.class).arrayListValues().build();
    private List<AbstractMove<?, ? super A>> allMoves;
    @Getter
    private boolean frozen = false;

    public MoveMap(Collection<Entry<A, S>> entries) {
        entries.forEach(entry -> this.entries.put(entry.getMoveClass(), entry));
        freeze();
    }

    public static <A extends IAttacker<? extends A, S>, S extends Enum<? extends S>> Codec<MoveMap<A, S>> codecFor(Class<S> stateEnum) {
        return RecordCodecBuilder.create(instance -> instance.group(
                Entry.<A, S>codecFor(stateEnum).listOf().fieldOf("moves").forGetter(m -> List.copyOf(m.entries.values()))
        ).apply(instance, MoveMap::new));
    }

    public Entry<A, S> register(final @NonNull MoveClass type, final @NonNull AbstractMove<?, ? super A> move) {
        return register(type, move, null);
    }

    public Entry<A, S> register(final @NonNull MoveClass type, final @NonNull AbstractMove<?, ? super A> move, final @Nullable S animState) {
        return register(type, move, type.getDefaultCooldownType(), animState);
    }

    /**
     * Registers a move and its immediate followup and variants.
     * Sub-moves must have an assigned animation state.
     */
    public void registerImmediate(final @NonNull MoveClass type, final @NonNull AbstractMove<?, ? super A> move, final @Nullable S animState) {
        final Entry<A, S> entry = register(type, move, animState);
        copyAnims(entry);
    }

    @SuppressWarnings("unchecked")
    private void copyAnims(Entry<A, S> entry) {
        final AbstractMove<?, ? super A> move = entry.getMove();
        if (move.getCrouchingVariant() != null) {
            Entry<A, S> cr = entry.withCrouchingVariant((S) move.getCrouchingVariant().getAnimation());
            copyAnims(cr);
        }
        if (move.getAerialVariant() != null) {
            Entry<A, S> ae = entry.withAerialVariant((S) move.getAerialVariant().getAnimation());
            copyAnims(ae);
        }
        if (move.getFollowup() != null) {
            Entry<A, S> fw = entry.withFollowup((S) move.getFollowup().getAnimation());
            copyAnims(fw);
        }
    }

    public Entry<A, S> register(final @NonNull MoveClass type, final @NonNull AbstractMove<?, ? super A> move, final @Nullable CooldownType cooldownType, final @Nullable S animState) {
        checkFrozen();

        final AbstractMove<?, ? super A> copy = move.copy();
        //noinspection ConstantValue // That's the idea
        if (copy == null) {
            throw new IllegalStateException(move.getClass().getSimpleName() + "#copy() returned null.");
        }

        copy.onRegister(type);

        final Entry<A, S> entry = new Entry<A, S>(type, copy, cooldownType, animState);
        entries.put(type, entry);
        return entry;
    }

    /**
     * Used by {@link #copyFrom(MoveMap, boolean)} to copy entries.
     * @param entry The entry to register. Will be copied.
     */
    private void register(final Entry<A, S> entry) {
        checkFrozen();
        entries.put(entry.getMoveClass(), entry.copy());
    }

    public void freeze() {
        checkFrozen();

        frozen = true;
        allMoves = toList();
    }

    public void copyFrom(final MoveMap<A, S> other) {
        copyFrom(other, false);
    }

    public void copyFrom(final MoveMap<A, S> other, final boolean force) {
        if (!force) checkFrozen();
        entries.clear();
        other.entries.values().forEach(this::register);
    }

    @NonNull
    public List<Entry<A, S>> getEntries(final MoveClass type) {
        return Collections.unmodifiableList(entries.get(type));
    }

    /**
     * Finds first move of the given type that matches all conditions for the given attacker.
     *
     * @param type     The type of move to find
     * @param attacker The attacker to test the move against
     * @return The first valid entry for the given type, or null if none are found.
     */
    @Nullable
    public Entry<A, S> getFirstValidEntry(final MoveClass type, final A attacker, final boolean crouching, final boolean aerial) {
        return getEntries(type).stream()
                .map(entry -> {
                    if (crouching && entry.getCrouchingVariant() != null) {
                        entry = entry.getCrouchingVariant();
                        if (aerial && entry.getAerialVariant() != null) {
                            return entry.getAerialVariant();
                        }

                        return entry;
                    } else if (aerial && entry.getAerialVariant() != null) {
                        entry = entry.getAerialVariant();
                        if (crouching && entry.getCrouchingVariant() != null) {
                            return entry.getCrouchingVariant();
                        }

                        return entry;
                    } else {
                        return entry;
                    }
                })
                .filter(entry -> entry.getMove().conditionsMet(attacker))
                .findFirst()
                .orElse(null);
    }

    /**
     * Throws an {@link IllegalStateException} if this MoveMap is frozen.
     */
    private void checkFrozen() {
        if (frozen) {
            throw new IllegalStateException("MoveMap is already frozen.");
        }
    }

    @Nullable
    public Entry<A, S> getEntry(final AbstractMove<?, ? super A> move) {
        return Streams.stream(this)
                .filter(entry -> entry.getMove().getOriginalMove() == move)
                .findFirst()
                .orElse(null);
    }

    public void initiateFollowup(final A attacker, AbstractMove<?, ? super A> move, boolean setMoveStun, int chargeTime) {
        Entry<A, S> entry = getEntry(move.getOriginalMove());
        if (entry == null || entry.getFollowup() == null) {
            return;
        }
        entry = entry.getFollowup();

        // Check conditions
        if (!entry.getMove().conditionsMet(attacker)) {
            return;
        }

        move = entry.getMove();
        move.setChargeTime(attacker, chargeTime);
        attacker.setMove(move.isCopyOnUse() ? move.copy() : move, entry.getAnimState());
        if (setMoveStun) {
            attacker.setMoveStun(move.getDuration());
        }
    }

    public void tickMoves(final A attacker) {
        asMovesList().forEach(move -> move.tick(attacker));
    }

    @NonNull
    @Override
    public Iterator<Entry<A, S>> iterator() {
        // Ensure we add all variants here too.
        return entries.values().stream()
                .flatMap(this::streamEntryAndChildren)
                .iterator();
    }

    /**
     * Builds a list of all moves in this map.
     *
     * @return A list of all moves in this map.
     */
    private List<AbstractMove<?, ? super A>> toList() {
        return ImmutableList.copyOf(this).stream()
                .map(Entry::getMove)
                .collect(ImmutableList.toImmutableList());
    }

    /**
     * Returns a list of all moves in this map.
     * Includes variants and follow-ups, recursively.
     *
     * @return A list of all moves in this map.
     */
    public List<AbstractMove<?, ? super A>> asMovesList() {
        // If the map is frozen, we can return the cached list.
        return frozen && allMoves != null ? allMoves : toList();
    }

    private Stream<Entry<A, S>> streamEntryAndChildren(final Entry<A, S> entry) {
        Stream.Builder<Entry<A, S>> builder = Stream.builder();
        builder.add(entry);
        if (entry.getCrouchingVariant() != null) {
            streamEntryAndChildren(entry.getCrouchingVariant()).forEach(builder::add);
        }
        if (entry.getAerialVariant() != null) {
            streamEntryAndChildren(entry.getAerialVariant()).forEach(builder::add);
        }
        if (entry.getFollowup() != null) {
            streamEntryAndChildren(entry.getFollowup()).forEach(builder::add);
        }

        return builder.build();
    }

    public Multimap<MoveClass, Entry<A, S>> getEntries() {
        return ImmutableListMultimap.copyOf(entries);
    }

    @Data
    public static class Entry<A extends IAttacker<? extends A, S>, S extends Enum<?>> {
        private final MoveClass moveClass;
        private final AbstractMove<?, ? super A> move;
        private final CooldownType cooldownType;
        private final @Nullable S animState;
        private @Nullable Entry<A, S> crouchingVariant, aerialVariant, followup;

        private Entry(final MoveClass moveClass, final AbstractMove<?, ? super A> move, final CooldownType cooldownType, final @Nullable S animState) {
            this.moveClass = moveClass;
            this.move = move;
            this.cooldownType = cooldownType;
            this.animState = animState;

            if (move.getCrouchingVariant() != null) {
                crouchingVariant = new Entry<A, S>(moveClass, move.getCrouchingVariant(), cooldownType, animState);
            }

            if (move.getAerialVariant() != null) {
                aerialVariant = new Entry<A, S>(moveClass, move.getAerialVariant(), cooldownType, animState);
            }

            if (move.getFollowup() != null) {
                followup = new Entry<A, S>(moveClass, move.getFollowup(), cooldownType, animState);
            }
        }

        // Constructor for codec
        @SuppressWarnings("unchecked")
        private Entry(@NonNull MoveClass moveClass, @NonNull AbstractMove<?, ?> move, @NonNull CooldownType cooldownType, @Nullable S animState,
                     @Nullable Entry<A, S> crouchingVariant, @Nullable Entry<A, S> aerialVariant, @Nullable Entry<A, S> followup) {
            this.moveClass = moveClass;
            this.move = (AbstractMove<?, ? super A>) move;
            this.move.withAnim(animState);
            this.cooldownType = cooldownType;
            this.animState = animState;
            this.crouchingVariant = crouchingVariant;
            this.aerialVariant = aerialVariant;
            this.followup = followup;

            if (this.crouchingVariant != null) {
                ((AbstractMove<?, A>) this.move).withCrouchingVariant(this.crouchingVariant.move);
            }

            if (this.aerialVariant != null) {
                ((AbstractMove<?, A>) this.move).withAerialVariant(this.aerialVariant.move);
            }

            if (this.followup != null) {
                ((AbstractMove<?, A>) this.move).withFollowup(this.followup.move);
            }

            this.move.onRegister(moveClass);
        }

        /**
         * Creates a codec for an entry with the given state enum.
         * @param stateEnum The class of the animation state enum
         * @return A codec for an entry with the given state enum
         * @param <S> The type of the animation state enum
         */
        public static <A extends IAttacker<? extends A, S>, S extends Enum<? extends S>> Codec<Entry<A, S>> codecFor(Class<S> stateEnum) {
            Codec<S> stateCodec = JCodecUtils.createEnumCodec(stateEnum);
            return JCodecUtils.recursive("MoveMap.Entry", self ->
                    RecordCodecBuilder.create(instance -> instance.group(
                            MoveClass.CODEC.fieldOf("class").forGetter(Entry::getMoveClass),
                            MoveSetLoader.MOVE_CODEC.get().fieldOf("move").forGetter(Entry::getMove),
                            CooldownType.CODEC.fieldOf("cooldown_type").forGetter(Entry::getCooldownType),
                            stateCodec.optionalFieldOf("anim_state").forGetter(e -> Optional.ofNullable(e.getAnimState())),
                            self.optionalFieldOf("crouching_variant").forGetter(e -> Optional.ofNullable(e.getCrouchingVariant())),
                            self.optionalFieldOf("aerial_variant").forGetter(e -> Optional.ofNullable(e.getAerialVariant())),
                            self.optionalFieldOf("followup").forGetter(e -> Optional.ofNullable(e.getFollowup()))
                ).apply(instance, (moveClass, move, cooldownType,
                                   animState, crouchingVariant, aerialVariant, followup) ->
                        new Entry<>(moveClass, move, cooldownType, animState.orElse(null),
                                crouchingVariant.orElse(null), aerialVariant.orElse(null), followup.orElse(null)))));
        }

        /**
         * Overrides the default crouching variant of this entry.
         * If this method is not called, but this entry's move does have a
         * crouching variant, the crouching variant will use the same cooldown type
         * and animation state as this entry.
         * Use this if you wish to use a different state for the crouching variant.
         *
         * @param animState The animation state to use for the crouching variant of this move
         * @return The crouching variant entry
         * @see #withCrouchingVariant(CooldownType, Enum)
         */
        public Entry<A, S> withCrouchingVariant(final S animState) {
            return withCrouchingVariant(cooldownType, animState);
        }

        /**
         * Overrides the default crouching variant of this entry.
         * If this method is not called, but this entry's move does have a
         * crouching variant, the crouching variant will use the same cooldown type
         * and animation state as this entry.
         * Use this if you wish to use a different state and cooldown type for the crouching variant.
         *
         * @param cooldownType The cooldown type to use for the crouching variant of this move
         * @param animState    The animation state to use for the crouching variant of this move
         * @return The crouching variant entry
         * @see #withCrouchingVariant(Enum)
         */
        public Entry<A, S> withCrouchingVariant(final CooldownType cooldownType, final S animState) {
            if (move.getCrouchingVariant() == null) {
                throw new IllegalArgumentException("The move of this entry has " +
                        "no crouching variant.");
            }
            crouchingVariant = new Entry<A, S>(moveClass, move.getCrouchingVariant(), cooldownType, animState);
            return crouchingVariant;
        }

        /**
         * Overrides the default aerial variant of this entry.
         * If this method is not called, but this entry's move does have an
         * aerial variant, the aerial variant will use the same cooldown type
         * and animation state as this entry.
         * Use this if you wish to use a different state for the aerial variant.
         *
         * @param animState The animation state to use for the aerial variant of this move
         * @return The aerial variant entry
         * @see #withAerialVariant(CooldownType, Enum)
         */
        public Entry<A, S> withAerialVariant(final S animState) {
            return withAerialVariant(cooldownType, animState);
        }

        /**
         * Overrides the default aerial variant of this entry.
         * If this method is not called, but this entry's move does have an
         * aerial variant, the aerial variant will use the same cooldown type
         * and animation state as this entry.
         * Use this if you wish to use a different state for the aerial variant.
         *
         * @param cooldownType The cooldown type to use for the aerial variant of this move
         * @param animState    The animation state to use for the aerial variant of this move
         * @return The aerial variant entry
         * @see #withAerialVariant(Enum)
         */
        public Entry<A, S> withAerialVariant(final CooldownType cooldownType, final S animState) {
            if (move.getAerialVariant() == null) {
                throw new IllegalArgumentException("The move of this entry has " +
                        "no aerial variant.");
            }
            aerialVariant = new Entry<A, S>(moveClass, move.getAerialVariant(), cooldownType, animState);
            return aerialVariant;
        }

        /**
         * Overrides the default follow-up of this entry.
         * If this method is not called, but this entry's move does have a
         * follow-up, the follow-up will use the same cooldown type and
         * animation state as this entry.
         * Use this if you wish to use a different state for the follow-up.
         *
         * @param animState The animation state to use for the crouching variant of this move
         * @return The followup entry
         * @see #withFollowup(CooldownType, Enum)
         */
        public Entry<A, S> withFollowup(final S animState) {
            return withFollowup(cooldownType, animState);
        }

        /**
         * Overrides the default follow-up of this entry.
         * If this method is not called, but this entry's move does have a
         * follow-up, the follow-up will use the same cooldown type and
         * animation state as this entry.
         * Use this if you wish to use a different cooldown type and state for the follow-up.
         *
         * @param cooldownType The cooldown type to use for the follow-up of this move
         * @param animState    The animation state to use for the follow-up of this move
         * @return The followup entry
         * @see #withFollowup(CooldownType, Enum)
         */
        public Entry<A, S> withFollowup(final CooldownType cooldownType, final S animState) {
            if (move.getFollowup() == null) {
                throw new IllegalArgumentException("The move of this entry has " +
                        "no follow-up.");
            }
            followup = new Entry<A, S>(moveClass, move.getFollowup(), cooldownType, animState);
            return followup;
        }

        private Entry<A, S> copy() {
            return new Entry<>(moveClass, move.copy(), cooldownType, animState,
                    crouchingVariant != null ? crouchingVariant.copy() : null,
                    aerialVariant != null ? aerialVariant.copy() : null,
                    followup != null ? followup.copy() : null);
        }

        @Override
        public String toString() {
            return "Type: " + moveClass + ", Move name: " + move.getName() + ", Move desc: " + move.getDescription();
        }
    }
}
