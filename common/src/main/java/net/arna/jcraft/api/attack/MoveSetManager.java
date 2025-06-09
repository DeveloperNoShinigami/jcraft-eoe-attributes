package net.arna.jcraft.api.attack;

import com.google.common.collect.ImmutableMap;
import dev.architectury.registry.registries.RegistrySupplier;
import net.arna.jcraft.api.IAttackerType;
import net.arna.jcraft.common.attack.core.IAttacker;
import net.arna.jcraft.common.attack.core.MoveMap;
import net.arna.jcraft.common.attack.core.MoveSetImpl;
import net.arna.jcraft.common.data.MoveSetLoader;
import net.minecraft.resources.ResourceLocation;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class MoveSetManager {
    private static final Map<ResourceLocation, Map<String, MoveSet<?, ?>>> moveSets = new HashMap<>();

    // Entries are added using MoveSet.create().
    public static Map<ResourceLocation, Map<String, MoveSet<?, ?>>> getMoveSets() {
        return moveSets.entrySet().stream()
                .map(e -> Map.entry(e.getKey(), Collections.unmodifiableMap(e.getValue())))
                .collect(
                        ImmutableMap::<ResourceLocation, Map<String, MoveSet<?, ?>>>builder,
                        ImmutableMap.Builder::put,
                        (b1, b2) -> b1.putAll(b2.build()))
                .build();
    }

    /**
     * Checks if the given stand type has any move sets.
     * @param type The stand type to check.
     * @return True if the stand type has any move sets, false otherwise.
     */
    public static boolean hasMoveSets(IAttackerType type) {
        return !moveSets.getOrDefault(type.getId(), Collections.emptyMap()).isEmpty();
    }

    /**
     * Gets all move sets for the given attacker type.
     * @param type The attacker type to get the move sets for.
     * @return A map of move sets for the given attacker type.
     * @param <A> The type of the attacker.
     * @param <S> The type of the state enum.
     */
    public static <A extends IAttacker<? extends A, S>, S extends Enum<S>> Map<String, MoveSet<A, S>> get(IAttackerType type) {
        return get(type.getId());
    }

    /**
     * Gets all move sets for the given attacker type.
     * @param typeLoc The resource location of the attacker type to get the move sets for.
     * @return A map of move sets for the given attacker type.
     * @param <A> The type of the attacker.
     * @param <S> The type of the state enum.
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <A extends IAttacker<? extends A, S>, S extends Enum<S>> Map<String, MoveSet<A, S>> get(ResourceLocation typeLoc) {
        return (Map<String, MoveSet<A, S>>) (Map) ImmutableMap.copyOf(moveSets.getOrDefault(typeLoc, Collections.emptyMap()));
    }

    /**
     * Gets the move set for the given attacker type with the given name.
     * @param type The attacker type to get the move set for.
     * @param name The name of the move set to get.
     * @return The move set with the given name for the given attacker type.
     * @param <A> The type of the attacker.
     * @param <S> The type of the state enum.
     */
    public static <A extends IAttacker<? extends A, S>, S extends Enum<S>> MoveSet<A, S> get(IAttackerType type, String name) {
        return get(type.getId(), name);
    }

    /**
     * Gets the move set for the given attacker type with the given name.
     * @param typeLoc The resource location of the attacker type to get the move set for.
     * @param name The name of the move set to get.
     * @return The move set with the given name for the given attacker type.
     * @param <A> The type of the attacker.
     * @param <S> The type of the state enum.
     */
    @SuppressWarnings("unchecked")
    public static <A extends IAttacker<? extends A, S>, S extends Enum<S>> MoveSet<A, S> get(ResourceLocation typeLoc, String name) {
        return (MoveSet<A, S>) moveSets.getOrDefault(typeLoc, Collections.emptyMap()).get(name);
    }

    /**
     * Create a new move set for the given attacker type with the name "default".
     * @param type The attacker type to create the move set for.
     * @param register The consumer to register moves with.
     * @param stateClass The class of the state enum for the attacker type.
     * @return The created move set.
     * @param <A> The type of the attacker.
     * @param <S> The type of the state enum.
     */
    public static <A extends IAttacker<? extends A, S>, S extends Enum<S>> MoveSet<A, S> create(
            RegistrySupplier<? extends IAttackerType> type, Consumer<MoveMap<A, S>> register, Class<S> stateClass) {
        return create(type, "default", register, stateClass);
    }

    /**
     * Create a new move set for the given attacker type with the given name.
     * @param type The attacker type to create the move set for.
     * @param register The consumer to register moves with.
     * @param stateClass The class of the state enum for the attacker type.
     * @return The created move set.
     * @param <A> The type of the attacker.
     * @param <S> The type of the state enum.
     */
    public static <A extends IAttacker<? extends A, S>, S extends Enum<S>> MoveSet<A, S> create(
            RegistrySupplier<? extends IAttackerType> type, String name, Consumer<MoveMap<A, S>> register, Class<S> stateClass) {
        if (moveSets.getOrDefault(type.getId(), Collections.emptyMap()).containsKey(name)) {
            throw new IllegalArgumentException("Move set " + name + " for type " + type.getId() + "already exists");
        }

        MoveSet<A, S> moveSet = new MoveSetImpl<>(type, name, register, stateClass);
        moveSets.computeIfAbsent(type.getId(), k -> new HashMap<>()).put(name, moveSet);

        // Attempt to load the move set data if there's any pending data for this type and name.
        MoveSetLoader.attemptLoad(moveSet);
        return moveSet;
    }
}
