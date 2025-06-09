package net.arna.jcraft.common.data;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.JsonOps;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.api.attack.MoveSet;
import net.arna.jcraft.api.attack.MoveSetManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class MoveSetLoader {
    private static final Map<ResourceLocation, Map<String, Collection<Pair<ResourceLocation, JsonObject>>>> pendingMoveSetData = new HashMap<>();

    public static void attemptLoad(MoveSet<?, ?> moveSet) {
        // This method is called when a MoveSet is created.
        // It will load the move set data if it exists.
        ResourceLocation typeLoc = moveSet.getType().getId();
        String moveSetName = moveSet.getName();

        if (pendingMoveSetData.getOrDefault(typeLoc, Collections.emptyMap()).containsKey(moveSetName)) {
            Map<String, Collection<Pair<ResourceLocation, JsonObject>>> typeMoveSets = pendingMoveSetData.get(typeLoc);
            moveSet.load(JsonOps.INSTANCE, typeMoveSets.get(moveSetName), null);
            typeMoveSets.remove(moveSetName);

            if (typeMoveSets.isEmpty()) {
                pendingMoveSetData.remove(typeLoc);
            }
        }
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
        return CompletableFuture.supplyAsync(() -> loadFiles(resourceManager), backgroundExecutor)
                .thenCompose(preparationBarrier::wait) // Wait for preparations to finish
                .thenAcceptAsync(data -> loadMoveSets(data, gameExecutor));
    }

    /**
     * Loads/reloads all move sets using the given resource manager.
     *
     * @param resourceManager The resource manager to load from.
     * @return A map of move sets with attacker keys and move set values
     */
    private static Map<ResourceLocation, Multimap<String, Pair<ResourceLocation, JsonObject>>> loadFiles(ResourceManager resourceManager) {
        Map<ResourceLocation, Resource> resources = resourceManager.listResources("movesets",
                rl -> rl.getPath().split("/").length >= 4 && rl.getPath().endsWith(".json"));

        Gson gson = new Gson();
        // Load resources into JsonObjects and order them by attacker type and move set name.
        Map<ResourceLocation, Multimap<String, Pair<ResourceLocation, JsonObject>>> moveSets = new HashMap<>();
        for (Map.Entry<ResourceLocation, Resource> entry : resources.entrySet()) {
            String[] parts = entry.getKey().getPath().split("/");
            ResourceLocation typeLoc = entry.getKey().withPath(parts[2]); // e.g. "jcraft:star_platinum"
            String moveSetName = parts[3];

            Multimap<String, Pair<ResourceLocation, JsonObject>> map = moveSets.computeIfAbsent(typeLoc,
                    s -> MultimapBuilder.hashKeys(2).arrayListValues().build());

            // Read resource into a JsonObject.
            try (BufferedReader reader = entry.getValue().openAsReader()) {
                JsonObject obj = gson.fromJson(reader, JsonObject.class);
                map.put(moveSetName, Pair.of(entry.getKey(), obj));
            } catch (IOException e) {
                JCraft.LOGGER.error("Failed to load move {} for move set {} of type {}",
                        entry.getKey(), moveSetName, typeLoc, e);
            }
        }

        return moveSets;
    }

    /**
     * Loads all move sets from the given map.
     * This method is called after the files are loaded and the preparation barrier is released.
     *
     * @param moveSets The map of move sets to load.
     * @param gameExecutor The executor for game tasks.
     */
    private static void loadMoveSets(Map<ResourceLocation, Multimap<String, Pair<ResourceLocation, JsonObject>>> moveSets, Executor gameExecutor) {
        pendingMoveSetData.clear();

        for (final Map.Entry<ResourceLocation, Multimap<String, Pair<ResourceLocation, JsonObject>>> typeEntry : moveSets.entrySet()) {
            ResourceLocation typeLoc = typeEntry.getKey();
            Multimap<String, Pair<ResourceLocation, JsonObject>> sets = typeEntry.getValue();

            for (final Map.Entry<String, Collection<Pair<ResourceLocation, JsonObject>>> moveSetEntry : sets.asMap().entrySet()) {
                String moveSetName = moveSetEntry.getKey();
                MoveSet<?, ?> moveSet = MoveSetManager.get(typeLoc, moveSetName);
                if (moveSet == null) {
                    // Either this moveset does not exist or the class of the stand entity it corresponds to is not loaded yet.
                    // We cannot be sure which one it is, so we just store this moveset for later.
                    pendingMoveSetData.computeIfAbsent(typeLoc, k -> new HashMap<>())
                            .put(moveSetName, moveSetEntry.getValue());
                    continue;
                }

                moveSet.load(JsonOps.INSTANCE, moveSetEntry.getValue(), gameExecutor);
            }
        }
    }
}
