package net.arna.jcraft.common.data;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.attack.core.data.MoveSet;
import net.arna.jcraft.registry.StandTypeRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class StandDataLoader {
    private static final Map<ResourceLocation, StandData> registry = new HashMap<>();

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

    private static Map<ResourceLocation, JsonObject> loadDataFiles(ResourceManager resourceManager) {
        Map<ResourceLocation, Resource> resources = resourceManager.listResources("stands",
                rl -> rl.getPath().endsWith(".json"));

        Gson gson = new Gson();
        Map<ResourceLocation, JsonObject> data = new HashMap<>();
        for (Map.Entry<ResourceLocation, Resource> entry : resources.entrySet()) {
            // Remove the "stands/" prefix and ".json" suffix to get the stand type name
            // Transforms "stands/star_platinum.json" to "star_platinum"
            // MUST correspond to an entry in the StandType registry.
            ResourceLocation location = entry.getKey().withPath(path ->
                    path.substring("stands/".length(), path.length() - ".json".length()));

            // Check if a stand type with this id exists in the registry
            if () {
                JCraft.LOGGER.warn("Found stand data for non-existent stand {}. Skipping...", location);
                continue;
            }

            // Load the JSON file
            try (BufferedReader reader = entry.getValue().openAsReader()) {
                JsonObject obj = gson.fromJson(reader, JsonObject.class);
                data.put(location, obj);
            } catch (IOException e) {
                JCraft.LOGGER.error("Failed to load stand data for stand {}", location, e);
            }
        }

        return data;
    }
}
