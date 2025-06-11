package net.arna.jcraft.common.data;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import dev.architectury.registry.registries.Registrar;
import lombok.Getter;
import lombok.Setter;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.api.JRegistries;
import net.arna.jcraft.api.spec.SpecData;
import net.arna.jcraft.api.spec.SpecType;
import net.arna.jcraft.api.stand.StandData;
import net.arna.jcraft.api.stand.StandEntity;
import net.arna.jcraft.api.stand.StandType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class AttackerDataLoader {
    private static final Map<ResourceLocation, StandData> STAND_DATA = new HashMap<>();
    private static final Map<ResourceLocation, SpecData> SPEC_DATA = new HashMap<>();
    @Getter @Setter // used in server tick to inform clients of changes
    private static boolean dirty = false;

    /**
     * Gets the StandData for a given stand type id.
     * <b>Important:</b> this is mostly for internal use only. Consider using
     * {@link StandType#getData()} or {@link StandEntity#getStandData()} instead.
     * The main exception would be if your stand has multiple different data's, and you need a specific one.
     * @param id The ResourceLocation of the stand type, e.g. "jcraft:star_platinum"
     * @return The StandData if it exists, or the {@link StandData#EMPTY empty data} if it doesn't.
     */
    public static StandData getStandData(final ResourceLocation id) {
        return STAND_DATA.getOrDefault(id, StandData.EMPTY);
    }

    /**
     * Gets the StandData for a given stand type id.
     * <b>Important:</b> this is mostly for internal use only. Consider using
     * {@link SpecType#()} or {@link StandEntity#getStandData()} instead.
     * The main exception would be if your stand has multiple different data's, and you need a specific one.
     * @param id The ResourceLocation of the stand type, e.g. "jcraft:star_platinum"
     * @return The StandData if it exists, or the {@link StandData#EMPTY empty data} if it doesn't.
     */
    public static SpecData getSpecData(final ResourceLocation id) {
        return SPEC_DATA.getOrDefault(id, SpecData.EMPTY);
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
    public static CompletableFuture<Void> onReload(final PreparableReloadListener.PreparationBarrier preparationBarrier,
                                                   final ResourceManager resourceManager, final ProfilerFiller preparationsProfiler,
                                                   final ProfilerFiller reloadProfiler, final Executor backgroundExecutor, final Executor gameExecutor) {
        return CompletableFuture.supplyAsync(() -> loadDataFiles(resourceManager), backgroundExecutor)
                .thenCompose(preparationBarrier::wait) // Wait for preparations to finish
                .thenAcceptAsync(AttackerDataLoader::loadAllData);
    }

    /**
     * Lists all data files and loads them into JSON objects.
     * @param resourceManager The resource manager used to get data files
     * @return A map of ResourceLocation to JsonObject, where the ResourceLocation is the stand type id
     */
    private static Map<String, Map<ResourceLocation, JsonObject>> loadDataFiles(final ResourceManager resourceManager) {
        final Map<ResourceLocation, Resource> standResources = resourceManager.listResources("stands",
                rl -> rl.getPath().endsWith(".json"));
        final Map<ResourceLocation, Resource> specResources = resourceManager.listResources("specs",
                rl -> rl.getPath().endsWith(".json"));
        final Map<ResourceLocation, Resource> resources = new HashMap<>(standResources);
        resources.putAll(specResources);

        final Gson gson = new Gson();
        final Map<String, Map<ResourceLocation, JsonObject>> data = new HashMap<>();
        for (Map.Entry<ResourceLocation, Resource> entry : resources.entrySet()) {
            // Remove the "stands/" prefix and ".json" suffix to get the stand type name
            // Transforms "stands/star_platinum.json" to "star_platinum"
            // MUST correspond to an entry in the StandType registry.
            final ResourceLocation location = entry.getKey().withPath(path ->
                    path.substring(path.indexOf('/') + 1, path.length() - ".json".length()));
            final String kind = entry.getKey().getPath().split("/")[0]; // "stands" or "specs"

            // Load the JSON file
            try (BufferedReader reader = entry.getValue().openAsReader()) {
                final JsonObject obj = gson.fromJson(reader, JsonObject.class);
                data.computeIfAbsent(kind, s -> new HashMap<>()).put(location, obj);
            } catch (IOException e) {
                JCraft.LOGGER.error("Failed to load data for {} {}",
                        "stands".equals(kind) ? "stand" : "spec", location, e);
            }
        }

        return data;
    }

    private static void loadAllData(final Map<String, Map<ResourceLocation, JsonObject>> data) {
        loadData(data.get("stands"), StandData.CODEC, JRegistries.STAND_TYPE_REGISTRY, STAND_DATA, "stand", StandData.EMPTY);
        loadData(data.get("specs"), SpecData.CODEC, JRegistries.SPEC_TYPE_REGISTRY, SPEC_DATA, "spec", SpecData.EMPTY);

        dirty = true; // Mark data as dirty to inform clients of changes
    }

    private static <T> void loadData(final Map<ResourceLocation, JsonObject> data, final Codec<T> codec, final Registrar<?> registry,
                                     final Map<ResourceLocation, T> map, final String kind, final T fallback) {
        if (data == null || data.isEmpty()) {
            return;
        }

        for (final Map.Entry<ResourceLocation, JsonObject> entry : data.entrySet()) {
            final ResourceLocation id = entry.getKey();
            final JsonObject json = entry.getValue();

            // Parse the JSON object into a StandData instance
            final DataResult<T> res = codec.parse(JsonOps.INSTANCE, json);
            if (res.result().isEmpty()) {
                JCraft.LOGGER.error("Failed to parse {} data for {} {}: {}", kind, kind, id, res.error()
                        .map(DataResult.PartialResult::message).orElse(null));
                continue;
            }

            map.put(id, res.result().get());
        }

        // Iterate through the StandType registry to ensure all stand types have data.
        for (final ResourceLocation id : registry.getIds()) {
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

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public static void writeToBuffer(FriendlyByteBuf buf) {
        List<Map.Entry<ResourceLocation, CompoundTag>> standData = STAND_DATA.entrySet().stream()
                .map(entry -> Map.entry(entry.getKey(),
                        StandData.CODEC.encodeStart(NbtOps.INSTANCE, entry.getValue())))
                .peek(e -> {
                    if (e.getValue().error().isPresent()) {
                        JCraft.LOGGER.error("Failed to encode StandData for {}: {}",
                                e.getKey(), e.getValue().error().get().message());
                    }
                })
                .filter(e -> e.getValue().result().isPresent())
                .map(e -> Map.entry(e.getKey(), (CompoundTag) e.getValue().result().get()))
                .toList();

        List<Map.Entry<ResourceLocation, CompoundTag>> specData = SPEC_DATA.entrySet().stream()
                .map(entry -> Map.entry(entry.getKey(),
                        SpecData.CODEC.encodeStart(NbtOps.INSTANCE, entry.getValue())))
                .peek(e -> {
                    if (e.getValue().error().isPresent()) {
                        JCraft.LOGGER.error("Failed to encode SpecData for {}: {}",
                                e.getKey(), e.getValue().error().get().message());
                    }
                })
                .filter(e -> e.getValue().result().isPresent())
                .map(e -> Map.entry(e.getKey(), (CompoundTag) e.getValue().result().get()))
                .toList();

        buf.writeVarInt(standData.size());
        for (Map.Entry<ResourceLocation, CompoundTag> entry : standData) {
            buf.writeResourceLocation(entry.getKey());
            buf.writeNbt(entry.getValue());
        }

        buf.writeVarInt(specData.size());
        for (Map.Entry<ResourceLocation, CompoundTag> entry : specData) {
            buf.writeResourceLocation(entry.getKey());
            buf.writeNbt(entry.getValue());
        }
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public static void readFromBuffer(FriendlyByteBuf buf) {
        int standDataSize = buf.readVarInt();
        for (int i = 0; i < standDataSize; i++) {
            ResourceLocation id = buf.readResourceLocation();
            CompoundTag data = buf.readNbt();
            DataResult<StandData> result = StandData.CODEC.parse(NbtOps.INSTANCE, data);
            if (result.error().isPresent()) {
                JCraft.LOGGER.error("Failed to decode StandData for {}: {}",
                        id, result.error().get().message());
                continue;
            }
            STAND_DATA.put(id, result.result().get());
        }

        int specDataSize = buf.readVarInt();
        for (int i = 0; i < specDataSize; i++) {
            ResourceLocation id = buf.readResourceLocation();
            CompoundTag data = buf.readNbt();
            DataResult<SpecData> result = SpecData.CODEC.parse(NbtOps.INSTANCE, data);
            if (result.error().isPresent()) {
                JCraft.LOGGER.error("Failed to decode SpecData for {}: {}",
                        id, result.error().get().message());
                continue;
            }
            SPEC_DATA.put(id, result.result().get());
        }
    }
}
