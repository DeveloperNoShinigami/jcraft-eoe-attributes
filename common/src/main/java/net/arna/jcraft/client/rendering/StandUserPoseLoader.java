package net.arna.jcraft.client.rendering;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.api.pose.ModelType;
import net.arna.jcraft.api.pose.PoseModifiers;
import net.arna.jcraft.api.pose.modifier.IPoseModifier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class StandUserPoseLoader {
    private static final Map<ResourceLocation, Map<ModelType<?>, IPoseModifier>> poses = new HashMap<>();

    public static IPoseModifier getPose(ModelType<?> type, ResourceLocation id) {
        return poses.getOrDefault(id, Collections.emptyMap()).getOrDefault(type, IPoseModifier.EMPTY);
    }

    public static CompletableFuture<Void> onReload(PreparableReloadListener.PreparationBarrier preparationBarrier,
                                                    ResourceManager resourceManager, ProfilerFiller profilerFiller,
                                                    ProfilerFiller profilerFiller1, Executor executor, Executor executor1) {
        return CompletableFuture.supplyAsync(() -> loadPoses(resourceManager))
                .thenCompose(preparationBarrier::wait)
                .thenAcceptAsync(StandUserPoseLoader::parsePoses);
    }

    private static Map<ResourceLocation, JsonObject> loadPoses(ResourceManager resourceManager) {
        Gson gson = new Gson();
        Map<ResourceLocation, JsonObject> poseJsons = new HashMap<>();
        Map<ResourceLocation, Resource> resources = resourceManager.listResources("poses",
                loc -> loc.getPath().endsWith(".json") && loc.getPath().split("/").length == 3);
        for (Map.Entry<ResourceLocation, Resource> entry : resources.entrySet()) {
            try (BufferedReader reader = entry.getValue().openAsReader()) {
                JsonObject obj = gson.fromJson(reader, JsonObject.class);
                poseJsons.put(entry.getKey(), obj);
            } catch (IOException e) {
                JCraft.LOGGER.error("Failed to load pose {}", entry.getKey(), e);
            }
        }

        return poseJsons;
    }

    private static void parsePoses(Map<ResourceLocation, JsonObject> poseJsons) {
        poses.clear();

        for (Map.Entry<ResourceLocation, JsonObject> entry : poseJsons.entrySet()) {
            ResourceLocation id = entry.getKey();
            JsonObject obj = entry.getValue();

            ResourceLocation standType = id.withPath(path -> path.split("/")[1]);

            // Remove ".json" suffix
            String typeName = id.getPath().substring(id.getPath().lastIndexOf('/') + 1, id.getPath().length() - 5);
            ModelType<?> modelType = ModelType.fromName(typeName);
            if (modelType == null) {
                JCraft.LOGGER.error("Unknown model type {} for pose {}", typeName, id);
                continue;
            }

            DataResult<IPoseModifier> res = PoseModifiers.CODEC.parse(JsonOps.INSTANCE, obj);
            if (res.error().isPresent()) {
                JCraft.LOGGER.error("Failed to parse pose {}: {}", id, res.error().get().message());
                continue;
            }

            //noinspection OptionalGetWithoutIsPresent // checked above
            IPoseModifier pose = res.result().get();
            poses.computeIfAbsent(standType, t -> new HashMap<>())
                    .put(modelType, pose);
        }
    }
}
