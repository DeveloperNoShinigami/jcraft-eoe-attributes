package net.arna.jcraft.common.data;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import dev.architectury.registry.registries.Registrar;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.api.JRegistries;
import net.arna.jcraft.api.spec.SpecData;
import net.arna.jcraft.api.stand.StandData;
import net.arna.jcraft.api.stand.StandType;
import net.arna.jcraft.common.entity.stand.StandEntity;
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

public class AttackerDataLoader {
    private static final Map<ResourceLocation, StandData> standData = new HashMap<>();
    private static final Map<ResourceLocation, SpecData> specData = new HashMap<>();

    /**
     * Gets the StandData for a given stand type id.
     * <b>Important:</b> this is mostly for internal use only. Consider using
     * {@link StandType#getData()} or {@link StandEntity#getStandData()} instead.
     * The main exception would be if your stand has multiple different data's, and you need a specific one.
     * @param id The ResourceLocation of the stand type, e.g. "jcraft:star_platinum"
     * @return The StandData if it exists, or the {@link StandData#EMPTY empty data} if it doesn't.
     */
    public static StandData getStandData(ResourceLocation id) {
        return standData.getOrDefault(id, StandData.EMPTY);
    }

    /**
     * Gets the StandData for a given stand type id.
     * <b>Important:</b> this is mostly for internal use only. Consider using
     * {@link net.arna.jcraft.api.spec.SpecType2#()} or {@link StandEntity#getStandData()} instead.
     * The main exception would be if your stand has multiple different data's, and you need a specific one.
     * @param id The ResourceLocation of the stand type, e.g. "jcraft:star_platinum"
     * @return The StandData if it exists, or the {@link StandData#EMPTY empty data} if it doesn't.
     */
    public static SpecData getSpecData(ResourceLocation id) {
        return specData.getOrDefault(id, SpecData.EMPTY);
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
                .thenAcceptAsync(AttackerDataLoader::loadAllData);
    }

    /**
     * Lists all data files and loads them into JSON objects.
     * @param resourceManager The resource manager used to get data files
     * @return A map of ResourceLocation to JsonObject, where the ResourceLocation is the stand type id
     */
    private static Map<String, Map<ResourceLocation, JsonObject>> loadDataFiles(ResourceManager resourceManager) {
        Map<ResourceLocation, Resource> standResources = resourceManager.listResources("stands",
                rl -> rl.getPath().endsWith(".json"));
        Map<ResourceLocation, Resource> specResources = resourceManager.listResources("specs",
                rl -> rl.getPath().endsWith(".json"));
        Map<ResourceLocation, Resource> resources = new HashMap<>(standResources);
        resources.putAll(specResources);

        Gson gson = new Gson();
        Map<String, Map<ResourceLocation, JsonObject>> data = new HashMap<>();
        for (Map.Entry<ResourceLocation, Resource> entry : resources.entrySet()) {
            // Remove the "stands/" prefix and ".json" suffix to get the stand type name
            // Transforms "stands/star_platinum.json" to "star_platinum"
            // MUST correspond to an entry in the StandType registry.
            ResourceLocation location = entry.getKey().withPath(path ->
                    path.substring(path.indexOf('/') + 1, path.length() - ".json".length()));
            String kind = entry.getKey().getPath().split("/")[0]; // "stands" or "specs"

            // Load the JSON file
            try (BufferedReader reader = entry.getValue().openAsReader()) {
                JsonObject obj = gson.fromJson(reader, JsonObject.class);
                data.computeIfAbsent(kind, s -> new HashMap<>()).put(location, obj);
            } catch (IOException e) {
                JCraft.LOGGER.error("Failed to load data for {} {}",
                        "stands".equals(kind) ? "stand" : "spec", location, e);
            }
        }

        return data;
    }

    private static void loadAllData(Map<String, Map<ResourceLocation, JsonObject>> data) {
        loadData(data.get("stands"), StandData.CODEC, JRegistries.STAND_TYPE_REGISTRY, standData, "stand", StandData.EMPTY);
        loadData(data.get("specs"), SpecData.CODEC, JRegistries.SPEC_TYPE_REGISTRY, specData, "spec", SpecData.EMPTY);
    }

    private static <T> void loadData(Map<ResourceLocation, JsonObject> data, Codec<T> codec, Registrar<?> registry,
                                     Map<ResourceLocation, T> map, String kind, T fallback) {
        if (data == null || data.isEmpty()) return;

        for (Map.Entry<ResourceLocation, JsonObject> entry : data.entrySet()) {
            ResourceLocation id = entry.getKey();
            JsonObject json = entry.getValue();

            // Parse the JSON object into a StandData instance
            DataResult<T> res = codec.parse(JsonOps.INSTANCE, json);
            if (res.result().isEmpty()) {
                JCraft.LOGGER.error("Failed to parse {} data for {} {}: {}", kind, kind, id, res.error()
                        .map(DataResult.PartialResult::message).orElse(null));
                continue;
            }

            map.put(id, res.result().get());
        }

        // Iterate through the StandType registry to ensure all stand types have data.
        for (ResourceLocation id : registry.getIds()) {
            if (id.equals(JCraft.id("none"))) {
                // Ignore the NONE type, as it has no data.
                continue;
            }

            if (!map.containsKey(id)) {
                // If no data is found for a stand type, log a warning
                JCraft.LOGGER.warn("No {} data found for {} type {}. Using default data.", kind, kind, id);
                map.put(id, fallback); // Use default empty data
            }
        }
    }
}
