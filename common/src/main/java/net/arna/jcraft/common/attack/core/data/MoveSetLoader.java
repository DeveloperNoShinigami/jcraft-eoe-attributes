package net.arna.jcraft.common.attack.core.data;

import com.google.common.collect.ImmutableMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class MoveSetLoader {
    // Entries are added using MoveSet.create().
    static final Map<ResourceLocation, Map<String, MoveSet<?, ?>>> moveSets = new HashMap<>();

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
     * Called upon datapack (re)load.
     * Loads stand movesets from datapacks.
     * @param preparationBarrier Preparation stuff that must be finished before reading anything
     * @param resourceManager The resource manager used to get data
     * @param preparationsProfiler Profiler for preparations
     * @param reloadProfiler Profiler for reload
     * @param backgroundExecutor Executor for background tasks
     * @param gameExecutor Executor for game tasks
     * @return A completable future that completes when the reload is done
     * @see net.minecraft.server.ReloadableServerResources
     */
    public static CompletableFuture<Void> onReload(PreparableReloadListener.PreparationBarrier preparationBarrier,
                                                   ResourceManager resourceManager, ProfilerFiller preparationsProfiler,
                                                   ProfilerFiller reloadProfiler, Executor backgroundExecutor, Executor gameExecutor) {
        return CompletableFuture.<Void>supplyAsync(() -> null, backgroundExecutor)
                .thenCompose(preparationBarrier::wait) // Wait for preparations to finish
                .thenAcceptAsync(v -> MoveSet.loadAll(resourceManager, gameExecutor));
    }
}
