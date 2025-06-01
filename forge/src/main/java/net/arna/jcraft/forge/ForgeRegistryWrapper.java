package net.arna.jcraft.forge;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Lifecycle;
import net.minecraft.core.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraftforge.registries.IForgeRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Forge uses its own registry system that is incompatible with the vanilla registry system.
 * Regular built-in registries are backed by a vanilla registry which can be retrieved using
 * {@link IForgeRegistry#getSlaveMap(ResourceLocation, Class)}, but custom registries are not.
 * So instead, we have this wrapper class that implements the vanilla {@link Registry} interface
 * and wraps around a Forge {@link IForgeRegistry}.
 * @param <T> The type of the registry entries.
 */
public class ForgeRegistryWrapper<T> implements Registry<T> {
    private final IForgeRegistry<T> registry;
    private final Lookup lookup = new Lookup();
    private final Map<T, Holder.Reference<T>> unregisteredIntrusiveHolders = new HashMap<>();
    private volatile Map<TagKey<T>, HolderSet.Named<T>> tags = new IdentityHashMap<>();

    public ForgeRegistryWrapper(IForgeRegistry<T> registry) {
        this.registry = registry;
    }

    @Override
    public @NotNull ResourceKey<? extends Registry<T>> key() {
        return registry.getRegistryKey();
    }

    @Override
    public @Nullable ResourceLocation getKey(final @NotNull T value) {
        return registry.getKey(value);
    }

    @Override
    public @NotNull Optional<ResourceKey<T>> getResourceKey(final @NotNull T value) {
        return registry.getResourceKey(value);
    }

    @Override
    public int getId(@Nullable final T value) {
        throw new UnsupportedOperationException("Forge registries do not support numeric IDs");
    }

    @Override
    public @Nullable T byId(final int id) {
        throw new UnsupportedOperationException("Forge registries do not support numeric IDs");
    }

    @Override
    public int size() {
        return registry.getEntries().size();
    }

    @Override
    public @Nullable T get(@Nullable final ResourceKey<T> key) {
        return registry.getHolder(key).map(Holder::value).orElse(null);
    }

    @Override
    public @Nullable T get(@Nullable final ResourceLocation name) {
        return registry.getValue(name);
    }

    @Override
    public @NotNull Lifecycle lifecycle(final @NotNull T value) {
        return Lifecycle.stable();
    }

    @Override
    public @NotNull Lifecycle registryLifecycle() {
        return Lifecycle.stable();
    }

    @Override
    public @NotNull Set<ResourceLocation> keySet() {
        return registry.getKeys();
    }

    @Override
    public @NotNull Set<Map.Entry<ResourceKey<T>, T>> entrySet() {
        return registry.getEntries();
    }

    @Override
    public @NotNull Set<ResourceKey<T>> registryKeySet() {
        return registry.getEntries().stream()
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    @Override
    public @NotNull Optional<Holder.Reference<T>> getRandom(final @NotNull RandomSource random) {
        List<ResourceKey<T>> keys = new ArrayList<>(registryKeySet());
        if (keys.isEmpty()) {
            return Optional.empty();
        }

        ResourceKey<T> randomKey = keys.get(random.nextInt(keys.size()));
        return registry.getDelegate(randomKey);
    }

    @Override
    public boolean containsKey(final @NotNull ResourceLocation name) {
        return registry.containsKey(name);
    }

    @Override
    public boolean containsKey(final @NotNull ResourceKey<T> key) {
        return registry.containsKey(key.location());
    }

    @Override
    public @NotNull Registry<T> freeze() {
        return this;
    }

    @SuppressWarnings("deprecation") // This is how Minecraft does it
    @Override
    public Holder.@NotNull Reference<T> createIntrusiveHolder(final @NotNull T value) {
        return this.unregisteredIntrusiveHolders.computeIfAbsent(value, (object) -> Holder.Reference.createIntrusive(this.asLookup(), object));
    }

    @Override
    public @NotNull Optional<Holder.Reference<T>> getHolder(final int id) {
        throw new UnsupportedOperationException("Forge registries do not support numeric IDs");
    }

    @Override
    public @NotNull Optional<Holder.Reference<T>> getHolder(final @NotNull ResourceKey<T> key) {
        return registry.getDelegate(key);
    }

    @Override
    public @NotNull Holder<T> wrapAsHolder(final @NotNull T value) {
        return registry.getHolder(value).orElseGet(() -> Holder.direct(value));
    }

    @Override
    public @NotNull Stream<Holder.Reference<T>> holders() {
        //noinspection OptionalGetWithoutIsPresent // these are guaranteed to be present
        return registry.getEntries().stream()
                .map(entry -> registry.getDelegate(entry.getKey()))
                .map(Optional::get);
    }

    @Override
    public @NotNull Optional<HolderSet.Named<T>> getTag(final @NotNull TagKey<T> key) {
        return Optional.ofNullable(this.tags.get(key));
    }

    @Override
    public HolderSet.@NotNull Named<T> getOrCreateTag(final @NotNull TagKey<T> key) {
        // This is exactly how Minecraft does it in MappedRegistry, so we follow suit.
        HolderSet.Named<T> named = tags.get(key);
        if (named == null) {
            named = new HolderSet.Named<>(holderOwner(), key);
            Map<TagKey<T>, HolderSet.Named<T>> map = new IdentityHashMap<>(this.tags);
            map.put(key, named);
            tags = map;
        }

        return named;
    }

    @Override
    public @NotNull Stream<Pair<TagKey<T>, HolderSet.Named<T>>> getTags() {
        return tags.entrySet().stream()
                .map(entry -> Pair.of(entry.getKey(), entry.getValue()));
    }

    @Override
    public @NotNull Stream<TagKey<T>> getTagNames() {
        return tags.keySet().stream();
    }

    @Override
    public void resetTags() {
        this.tags.values().forEach(arg -> arg.bind(List.of()));
        holders().forEach(arg -> arg.bindTags(Set.of()));
    }

    @Override
    public void bindTags(final @NotNull Map<TagKey<T>, List<Holder<T>>> tagMap) {
        throw new UnsupportedOperationException("I'm not implementing this, idek what it does.");
    }

    @Override
    public @NotNull HolderOwner<T> holderOwner() {
        return lookup;
    }

    @Override
    public HolderLookup.@NotNull RegistryLookup<T> asLookup() {
        return lookup;
    }

    @Override
    public @NotNull Iterator<T> iterator() {
        return registry.iterator();
    }

    private class Lookup implements HolderLookup.RegistryLookup<T> {
        @Override
        public @NotNull ResourceKey<? extends Registry<? extends T>> key() {
            return ForgeRegistryWrapper.this.key();
        }

        @Override
        public @NotNull Lifecycle registryLifecycle() {
            return ForgeRegistryWrapper.this.registryLifecycle();
        }

        @Override
        public @NotNull Stream<Holder.Reference<T>> listElements() {
            return ForgeRegistryWrapper.this.holders();
        }

        @Override
        public @NotNull Stream<HolderSet.Named<T>> listTags() {
            return ForgeRegistryWrapper.this.getTags().map(Pair::getSecond);
        }

        @Override
        public @NotNull Optional<Holder.Reference<T>> get(final @NotNull ResourceKey<T> resourceKey) {
            return ForgeRegistryWrapper.this.getHolder(resourceKey);
        }

        @Override
        public @NotNull Optional<HolderSet.Named<T>> get(final @NotNull TagKey<T> tagKey) {
            return ForgeRegistryWrapper.this.getTag(tagKey);
        }
    }
}
