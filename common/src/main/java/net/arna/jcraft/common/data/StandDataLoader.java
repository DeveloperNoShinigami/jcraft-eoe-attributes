package net.arna.jcraft.common.data;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.api.JRegistries;
import net.arna.jcraft.api.StandData;
import net.arna.jcraft.api.StandType2;
import net.arna.jcraft.common.entity.stand.StandEntity;
import net.arna.jcraft.registry.JStandTypeRegistry;
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
     * Gets the StandData for a given stand type id.
     * <b>Important:</b> this is mostly for internal use only. Consider using
     * {@link StandType2#getData()} or {@link StandEntity#getStandData()} instead.
     * The main exception would be if your stand has multiple different data's, and you need a specific one.
     * @param id The ResourceLocation of the stand type, e.g. "jcraft:star_platinum"
     * @return The StandData if it exists, or the {@link StandData#EMPTY empty data} if it doesn't.
     */
    public static StandData getStandData(ResourceLocation id) {
        return registry.getOrDefault(id, StandData.EMPTY);
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
        return CompletableFuture.supplyAsync(() -> loadDataFiles(resourceManager), backgroundExecutor)
                .thenCompose(preparationBarrier::wait) // Wait for preparations to finish
                .thenAcceptAsync(StandDataLoader::loadStandData);
    }

    /**
     * Lists all data files and loads them into JSON objects.
     * @param resourceManager The resource manager used to get data files
     * @return A map of ResourceLocation to JsonObject, where the ResourceLocation is the stand type id
     */
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
            if (!JRegistries.STAND_TYPE_REGISTRY.contains(location)) {
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

    private static void loadStandData(Map<ResourceLocation, JsonObject> data) {
        for (Map.Entry<ResourceLocation, JsonObject> entry : data.entrySet()) {
            ResourceLocation id = entry.getKey();
            JsonObject json = entry.getValue();

            // Parse the JSON object into a StandData instance
            DataResult<StandData> res = StandData.CODEC.parse(JsonOps.INSTANCE, json);
            if (res.result().isEmpty()) {
                JCraft.LOGGER.error("Failed to parse stand data for stand {}: {}", id, res.error().orElseThrow());
                continue;
            }

            registry.put(id, res.result().get());
        }

        // Iterate through the StandType registry to ensure all stand types have data.
        for (ResourceLocation id : JRegistries.STAND_TYPE_REGISTRY.getIds()) {
            if (id.equals(JStandTypeRegistry.NONE.getId())) {
                // Ignore the NONE stand type, as it has no data.
                continue;
            }

            if (!registry.containsKey(id)) {
                // If no data is found for a stand type, log a warning
                JCraft.LOGGER.warn("No stand data found for stand type {}. Using default data.", id);
                registry.put(id, StandData.EMPTY); // Use default empty data
            }
        }
    }
}
