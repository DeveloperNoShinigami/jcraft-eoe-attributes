package net.arna.jcraft.common.attack.core.data;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import dev.architectury.registry.registries.RegistrySupplier;
import lombok.Getter;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.api.IAttackerType;
import net.arna.jcraft.api.attack.MoveSet;
import net.arna.jcraft.api.attack.StateContainerHolder;
import net.arna.jcraft.common.attack.core.IAttacker;
import net.arna.jcraft.common.attack.core.MoveMap;
import net.minecraft.resources.ResourceLocation;

import java.util.*;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

public class MoveSetImpl<A extends IAttacker<? extends A, S>, S extends Enum<S>> implements MoveSet<A, S> {
    private final RegistrySupplier<? extends IAttackerType> type;
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

    public MoveSetImpl(RegistrySupplier<? extends IAttackerType> type, String name, Consumer<MoveMap<A, S>> register, Class<S> stateClass) {
        this.type = type;
        this.name = name;
        this.register = register;
        this.stateClass = stateClass;
        codec = MoveMap.codecFor(stateClass);
        entryCodec = MoveMap.Entry.codecFor(stateClass);
    }

    @Override
    public IAttackerType getType() {
        return type.get();
    }

    @Override
    public <T> DataResult<Pair<MoveMap<A, S>, T>> load(DynamicOps<T> ops, T t, Executor gameExecutor) {
        DataResult<Pair<MoveMap<A, S>, T>> res = codec.decode(ops, t);
        if (res.result().isEmpty()) return res;

        onLoad(res.result().get().getFirst(), gameExecutor);
        return res;
    }

    @Override
    public <T> MoveMap<A, S> load(DynamicOps<? super T> ops, Collection<Pair<ResourceLocation, T>> ts, Executor gameExecutor) {
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

    @Override
    public MoveMap<A, S> save() {
        MoveMap<A, S> moveMap = new MoveMap<>();
        register.accept(moveMap);
        return moveMap;
    }

    @Override
    public <T> DataResult<T> write(DynamicOps<T> ops) {
        MoveMap<A, S> moveMap = save();
        return codec.encodeStart(ops, moveMap);
    }

    @Override
    public <T> DataResult<T> writeModified(DynamicOps<T> ops) {
        return codec.encodeStart(ops, moveMap);
    }

    @Override
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
