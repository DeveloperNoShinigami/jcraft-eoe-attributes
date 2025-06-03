package net.arna.jcraft.api.attack;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import net.arna.jcraft.api.IAttackerType;
import net.arna.jcraft.common.attack.core.IAttacker;
import net.arna.jcraft.common.attack.core.MoveMap;
import net.arna.jcraft.common.attack.core.data.MoveSetImpl;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.concurrent.Executor;

public interface MoveSet<A extends IAttacker<? extends A, S>, S extends Enum<S>> {
    /**
     * Gets the type of the move set.
     *
     * @return The type of the move set.
     */
    IAttackerType getType();

    /**
     * Load the move set from the given data.
     * Stores the result in the moveMap field and notifies all listeners.
     *
     * @param ops The dynamic ops to use. Such as {@link com.mojang.serialization.JsonOps#INSTANCE JsonOps} or
     *            {@link net.minecraft.nbt.NbtOps#INSTANCE NbtOps}.
     * @param t   The data to load from. ({@link com.google.gson.JsonElement JsonElement} in case of JsonOps,
     *            {@link net.minecraft.nbt.Tag Tag} in case of NbtOps)
     * @param <T> The type of the element to load from. Such as JsonElement or Tag.
     * @return The result of the load operation.
     */
    <T> DataResult<Pair<MoveMap<A, S>, T>> load(DynamicOps<T> ops, T t, Executor gameExecutor);

    /**
     * Loads the move set from a collection of encoded entries paired with their resource location (for error logging).
     * Stores the result in the moveMap field and notifies all listeners.
     *
     * @param ops The dynamic ops to use. Such as {@link com.mojang.serialization.JsonOps#INSTANCE JsonOps} or
     *            {@link net.minecraft.nbt.NbtOps#INSTANCE NbtOps}.
     * @param ts  The collection of encoded entries paired with their resource location.
     * @param <T> The type of the element to load from. Such as JsonElement or Tag.
     * @return The loaded move map.
     */
    <T> MoveMap<A, S> load(DynamicOps<? super T> ops, Collection<Pair<ResourceLocation, T>> ts, Executor gameExecutor);

    /**
     * Saves the default move map (made with the register function) to a new move map.
     *
     * @return The default move map.
     */
    MoveMap<A, S> save();

    /**
     * Writes the default move set (made with the register function) using the given dynamic ops.
     *
     * @param ops The dynamic ops to use. Such as {@link com.mojang.serialization.JsonOps#INSTANCE JsonOps}
     * @param <T> The type of the element to save to. Such as JsonElement.
     * @return The result of the save operation.
     */
    <T> DataResult<T> write(DynamicOps<T> ops);

    /**
     * Writes the modified move set (including datapack changes) using the given dynamic ops.
     *
     * @param ops The dynamic ops to use. Such as {@link com.mojang.serialization.JsonOps#INSTANCE JsonOps}
     * @param <T> The type of the element to save to. Such as JsonElement.
     * @return The result of the save operation.
     */
    <T> DataResult<T> writeModified(DynamicOps<T> ops);

    /**
     * Register a listener for changes made to the move set.
     * Held with weak references, so safe to be an instance of StandEntity or JSpec.
     * Immediately notifies the listener of the current move set if it is already initialized.
     *
     * @param listener The listener to register.
     */
    void registerListener(MoveSetImpl.ReloadListener<A, S> listener);

    String getName();

    Class<S> getStateClass();

    Codec<MoveMap<A, S>> getCodec();

    Codec<MoveMap.Entry<A, S>> getEntryCodec();

    MoveMap<A, S> getMoveMap();
}
