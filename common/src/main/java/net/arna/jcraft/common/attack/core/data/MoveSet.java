package net.arna.jcraft.common.attack.core.data;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import lombok.Getter;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.attack.core.IAttacker;
import net.arna.jcraft.common.attack.core.MoveMap;
import net.arna.jcraft.common.entity.stand.StandEntity;
import net.arna.jcraft.common.entity.stand.StandType;
import net.arna.jcraft.common.spec.JSpec;
import net.arna.jcraft.common.spec.SpecType;
import net.arna.jcraft.common.util.SpecAnimationState;
import net.arna.jcraft.common.util.StandAnimationState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

import static net.arna.jcraft.common.attack.core.data.MoveSetLoader.moveSets;

public class MoveSet<A extends IAttacker<? extends A, S>, S extends Enum<S>> {
    @Getter
    private final Enum<?> type;
    @Getter
    private final String name;
    private final Consumer<MoveMap<A, S>> register;
    @Getter
    private final Class<S> stateClass;
    @Getter
    private final Codec<MoveMap<A, S>> codec;
    @Getter
    private final Codec<MoveMap.Entry<A, S>> entryCodec;
    private final Set<ReloadListener<A, S>> listeners = Collections.synchronizedSet(Collections.newSetFromMap(new WeakHashMap<>()));
    @Getter
    private final MoveMap<A, S> moveMap = new MoveMap<>();
    @Getter
    private boolean initialized = false;

    private MoveSet(Enum<?> type, String name, Consumer<MoveMap<A, S>> register, Class<S> stateClass) {
        this.type = type;
        this.name = name;
        this.register = register;
        this.stateClass = stateClass;
        codec = MoveMap.codecFor(stateClass);
        entryCodec = MoveMap.Entry.codecFor(stateClass);
    }

    /**
     * Checks if the given stand type has any move sets.
     * @param standType The stand type to check.
     * @return True if the stand type has any move sets, false otherwise.
     */
    public static boolean hasMoveSets(StandType standType) {
        return !moveSets.getOrDefault(standType, Collections.emptyMap()).isEmpty();
    }

    /**
     * Checks if the given spec type has any move sets.
     * @param specType The spec type to check.
     * @return True if the spec type has any move sets, false otherwise.
     */
    public static boolean hasMoveSets(SpecType specType) {
        return !moveSets.getOrDefault(specType, Collections.emptyMap()).isEmpty();
    }

    /**
     * Gets all move sets for the given stand type.
     * @param standType The stand type to get the move sets for.
     * @return A map of move sets for the given stand type.
     * @param <A> The type of the attacker (stand).
     * @param <S> The type of the state enum.
     */
    public static <A extends StandEntity<? extends A, S>, S extends Enum<S> & StandAnimationState<? extends A>>
    Map<String, MoveSet<A, S>> get(StandType standType) {
        return get((Enum<?>) standType);
    }

    /**
     * Gets all move sets for the given stand type.
     * @param specType The stand type to get the move sets for.
     * @return A map of move sets for the given stand type.
     * @param <A> The type of the attacker (stand).
     * @param <S> The type of the state enum.
     */
    public static <A extends JSpec<? extends A, S>, S extends Enum<S> & SpecAnimationState<? extends A>>
    Map<String, MoveSet<A, S>> get(SpecType specType) {
        return get((Enum<?>) specType);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static <A extends IAttacker<? extends A, S>, S extends Enum<S>> Map<String, MoveSet<A, S>> get(Enum<?> type) {
        return (Map<String, MoveSet<A, S>>) (Map) ImmutableMap.copyOf(moveSets.getOrDefault(type, Collections.emptyMap()));
    }

    /**
     * Gets the move set for the given stand type with the given name.
     * @param type The stand type to get the move set for.
     * @param name The name of the move set to get.
     * @return The move set with the given name for the given stand type.
     * @param <A> The type of the attacker (stand).
     * @param <S> The type of the state enum.
     */
    public static <A extends StandEntity<? extends A, S>, S extends Enum<S> & StandAnimationState<? extends A>> MoveSet<A, S> get(
            StandType type, String name) {
        return get((Enum<?>) type, name);
    }

    /**
     * Gets the move set for the given spec type with the given name.
     * @param type The spec type to get the move set for.
     * @param name The name of the move set to get.
     * @return The move set with the given name for the given spec type.
     * @param <A> The type of the attacker (spec).
     * @param <S> The type of the state enum.
     */
    public static <A extends JSpec<? extends A, S>, S extends Enum<S> & SpecAnimationState<? extends A>> MoveSet<A, S> get(
            SpecType type, String name) {
        return get((Enum<?>) type, name);
    }

    @SuppressWarnings("unchecked")
    private static <A extends IAttacker<? extends A, S>, S extends Enum<S>> MoveSet<A, S> get(Enum<?> type, String name) {
        return (MoveSet<A, S>) moveSets.getOrDefault(type, Collections.emptyMap()).get(name);
    }

    /**
     * Create a new move set for the given stand type with the name "default".
     * @param type The stand type to create the move set for.
     * @param register The consumer to register moves with.
     * @param stateClass The class of the state enum for the stand type.
     * @return The created move set.
     * @param <A> The type of the attacker (stand).
     * @param <S> The type of the state enum.
     * @see #create(StandType, String, Consumer, Class)
     */
    public static <A extends StandEntity<? extends A, S>, S extends Enum<S> & StandAnimationState<? extends A>> MoveSet<A, S> create(
            StandType type, Consumer<MoveMap<A, S>> register, Class<S> stateClass) {
        return create((Enum<?>) type, "default", register, stateClass);
    }

    /**
     * Create a new move set for the given stand type.
     * @param type The stand type to create the move set for.
     * @param name The name of the move set.
     * @param register The consumer to register moves with.
     * @param stateClass The class of the state enum for the stand type.
     * @return The created move set.
     * @param <A> The type of the attacker (stand).
     * @param <S> The type of the state enum.
     */
    public static <A extends StandEntity<? extends A, S>, S extends Enum<S> & StandAnimationState<? extends A>> MoveSet<A, S> create(
            StandType type, String name, Consumer<MoveMap<A, S>> register, Class<S> stateClass) {
        return create((Enum<?>) type, name, register, stateClass);
    }

    /**
     * Create a new move set for the given spec type with the name "default".
     * @param type The spec type to create the move set for.
     * @param register The consumer to register moves with.
     * @param stateClass The class of the state enum for the spec type.
     * @return The created move set.
     * @param <A> The type of the attacker (spec).
     * @param <S> The type of the state enum.
     * @see #create(SpecType, String, Consumer, Class)
     */
    public static <A extends JSpec<? extends A, S>, S extends Enum<S> & SpecAnimationState<? extends A>> MoveSet<A, S> create(
            SpecType type, Consumer<MoveMap<A, S>> register, Class<S> stateClass) {
        return create((Enum<?>) type, "default", register, stateClass);
    }

    /**
     * Create a new move set for the given spec type.
     * @param type The spec type to create the move set for.
     * @param name The name of the move set.
     * @param register The consumer to register moves with.
     * @param stateClass The class of the state enum for the spec type.
     * @return The created move set.
     * @param <A> The type of the attacker (spec).
     * @param <S> The type of the state enum.
     */
    public static <A extends JSpec<? extends A, S>, S extends Enum<S> & SpecAnimationState<? extends A>> MoveSet<A, S> create(
            SpecType type, String name, Consumer<MoveMap<A, S>> register, Class<S> stateClass) {
        return create((Enum<?>) type, name, register, stateClass);
    }

    private static <A extends IAttacker<? extends A, S>, S extends Enum<S>> MoveSet<A, S> create(
            Enum<?> type, String name, Consumer<MoveMap<A, S>> register, Class<S> stateClass) {
        return new MoveSet<>(type, name, register, stateClass);
    }

    /**
     * Loads/reloads all move sets using the given resource manager.
     * @param resourceManager The resource manager to load from.
     */
    public static void loadAll(ResourceManager resourceManager, Executor gameExecutor) {
        Map<ResourceLocation, Resource> resources = resourceManager.listResources("movesets",
                rl -> JCraft.MOD_ID.equals(rl.getNamespace()) && rl.getPath().endsWith(".json"));

        // Group resources by move set.
        Map<String, Map<String, Multimap<String, Pair<ResourceLocation, Resource>>>> moveSets = Map.of(
                "stand", new HashMap<>(),
                "spec", new HashMap<>()
        );
        for (Map.Entry<ResourceLocation, Resource> entry : resources.entrySet()) {
            String[] parts = entry.getKey().getPath().split("/");
            moveSets.get(parts[1]) // "stand" or "spec"
                    .computeIfAbsent(parts[2], // type name (e.g. "star_platinum")
                            s -> MultimapBuilder.hashKeys(2).arrayListValues().build())
                    .put(parts[3], Pair.of(entry.getKey(), entry.getValue())); // move set name (e.g. "default")
        }

        Gson gson = new Gson();
        MoveSetLoader.moveSets.forEach((type, sets) -> sets.forEach((name, set) -> {
            Multimap<String, Pair<ResourceLocation, Resource>> typeMoveSets = moveSets.get(type instanceof StandType ? "stand" : "spec")
                    .get(type.name().toLowerCase(Locale.ROOT));
            if (typeMoveSets == null) {
                // Suppress the error message if the default move set is also empty.
                if (!set.save().asMovesList().isEmpty()) {
                    JCraft.LOGGER.error("Missing move set resources for type {}", type.name());
                }

                // Insert empty multimap to avoid printing the error message multiple times.
                moveSets.get(type instanceof StandType ? "stand" : "spec")
                        .put(type.name().toLowerCase(Locale.ROOT), MultimapBuilder.hashKeys().arrayListValues().build());

                return;
            }

            // If it's empty, we already printed an error message.
            if (typeMoveSets.isEmpty()) return;

            Collection<Pair<ResourceLocation, Resource>> entries = typeMoveSets.get(set.getName());
            if (entries.isEmpty()) {
                JCraft.LOGGER.error("Missing move set resources for move set {} for type {}", set.getName(), type.name());
                return;
            }

            List<Pair<ResourceLocation, JsonElement>> entriesJson = entries.stream()
                    .map(e -> {
                        try (BufferedReader reader = e.getSecond().openAsReader()) {
                            return e.mapSecond(r -> gson.fromJson(reader, JsonElement.class));
                        } catch (IOException ioException) {
                            JCraft.LOGGER.error("Failed to read file {} for move set {} for type {}",
                                    e.getFirst(), set.getName(), type.name(), ioException);
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .toList();

            set.load(JsonOps.INSTANCE, entriesJson, gameExecutor);
        }));
    }

    /**
     * Load the move set from the given data.
     * Stores the result in the moveMap field and notifies all listeners.
     * @param ops The dynamic ops to use. Such as {@link com.mojang.serialization.JsonOps#INSTANCE JsonOps} or
     * {@link net.minecraft.nbt.NbtOps#INSTANCE NbtOps}.
     * @param t The data to load from. ({@link com.google.gson.JsonElement JsonElement} in case of JsonOps,
     * {@link net.minecraft.nbt.Tag Tag} in case of NbtOps)
     * @return The result of the load operation.
     * @param <T> The type of the element to load from. Such as JsonElement or Tag.
     */
    public <T> DataResult<Pair<MoveMap<A, S>, T>> load(DynamicOps<T> ops, T t, Executor gameExecutor) {
        DataResult<Pair<MoveMap<A, S>, T>> res = codec.decode(ops, t);
        if (res.result().isEmpty()) return res;

        onLoad(res.result().get().getFirst(), gameExecutor);
        return res;
    }

    /**
     * Loads the move set from a collection of encoded entries paired with their resource location (for error logging).
     * Stores the result in the moveMap field and notifies all listeners.
     * @param ops The dynamic ops to use. Such as {@link com.mojang.serialization.JsonOps#INSTANCE JsonOps} or
     *            {@link net.minecraft.nbt.NbtOps#INSTANCE NbtOps}.
     * @param ts The collection of encoded entries paired with their resource location.
     * @return The loaded move map.
     * @param <T> The type of the element to load from. Such as JsonElement or Tag.
     */
    public <T> MoveMap<A, S> load(DynamicOps<T> ops, Collection<Pair<ResourceLocation, T>> ts, Executor gameExecutor) {
        List<MoveMap.Entry<A, S>> entries = ts.stream()
                // Decode each entry
                .map(p -> p.mapSecond(t -> entryCodec.decode(ops, t).map(Pair::getFirst)))
                // Extract results and log errors
                .map(p -> {
                    Optional<MoveMap.Entry<A, S>> result = p.getSecond().result();
                    if (result.isEmpty()) {
                        JCraft.LOGGER.error("Failed to decode move set entry {}: {}", p.getFirst(),
                                p.getSecond().error().map(DataResult.PartialResult::message).orElse("Unknown error"));
                        return null;
                    }
                    return result.get();
                })
                // Filter out nulls (failed decodes)
                .filter(Objects::nonNull)
                .toList();

        MoveMap<A, S> moveMap = new MoveMap<>(entries);
        onLoad(moveMap, gameExecutor);
        return moveMap;
    }

    @SuppressWarnings("unchecked")
    private void onLoad(MoveMap<A, S> moveMap, Executor gameExecutor) {
        moveMap.asMovesList().stream()
                .filter(m -> m instanceof StateContainerHolder<?>)
                .map(m -> (StateContainerHolder<S>) m)
                .forEach(holder -> holder.configureStateContainers(stateClass));
        this.moveMap.copyFrom(moveMap);

        initialized = true;
        // Make a new hashset to avoid concurrent modification exceptions.
        // Will not be necessary anymore once MoveContext has been yeeted at which point,
        // we can replace the onMoveSetReload implementation in StandEntity and JSpec with
        // one that just calls moveMap.copyFrom(moveMap).
        gameExecutor.execute(() -> new HashSet<>(listeners).forEach(listener ->
                listener.onMoveSetReload(this)));
    }

    /**
     * Saves the default move map (made with the register function) to a new move map.
     * @return The default move map.
     */
    public MoveMap<A, S> save() {
        MoveMap<A, S> moveMap = new MoveMap<>();
        register.accept(moveMap);
        return moveMap;
    }

    /**
     * Writes the default move set (made with the register function) using the given dynamic ops.
     * @param ops The dynamic ops to use. Such as {@link com.mojang.serialization.JsonOps#INSTANCE JsonOps}
     * @return The result of the save operation.
     * @param <T> The type of the element to save to. Such as JsonElement.
     */
    public <T> DataResult<T> write(DynamicOps<T> ops) {
        MoveMap<A, S> moveMap = save();
        return codec.encodeStart(ops, moveMap);
    }

    /**
     * Writes the modified move set (including datapack changes) using the given dynamic ops.
     * @param ops The dynamic ops to use. Such as {@link com.mojang.serialization.JsonOps#INSTANCE JsonOps}
     * @return The result of the save operation.
     * @param <T> The type of the element to save to. Such as JsonElement.
     */
    public <T> DataResult<T> writeModified(DynamicOps<T> ops) {
        return codec.encodeStart(ops, moveMap);
    }

    /**
     * Register a listener for changes made to the move set.
     * Held with weak references, so safe to be an instance of StandEntity or JSpec.
     * Immediately notifies the listener of the current move set if it is already initialized.
     * @param listener The listener to register.
     */
    public void registerListener(ReloadListener<A, S> listener) {
        if (!listeners.add(listener)) return;

        if (initialized) {
            listener.onMoveSetReload(this);
        }
    }

    /**
     * A listener for changes made to the move set.
     * Held with weak references, meant to be implemented by StandEntity and JSpec.
     * @param <A>
     * @param <S>
     */
    public interface ReloadListener<A extends IAttacker<? extends A, S>, S extends Enum<S>> {
        void onMoveSetReload(MoveSet<A, S> moveSet);
    }
}
