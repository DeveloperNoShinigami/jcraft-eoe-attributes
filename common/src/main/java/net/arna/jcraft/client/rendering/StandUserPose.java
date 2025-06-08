package net.arna.jcraft.client.rendering;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.api.pose.PoseModifiers;
import net.arna.jcraft.api.pose.modifier.IPoseModifier;
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

public class StandUserPose {
    private static final Map<ResourceLocation, IPoseModifier> poses = new HashMap<>();

    public static IPoseModifier getPose(ResourceLocation id) {
        return poses.getOrDefault(id, IPoseModifier.EMPTY);
    }

    public static CompletableFuture<Void> onReload(PreparableReloadListener.PreparationBarrier preparationBarrier,
                                                    ResourceManager resourceManager, ProfilerFiller profilerFiller,
                                                    ProfilerFiller profilerFiller1, Executor executor, Executor executor1) {
        return CompletableFuture.supplyAsync(() -> loadPoses(resourceManager))
                .thenCompose(preparationBarrier::wait)
                .thenAcceptAsync(StandUserPose::parsePoses);
    }

    private static Map<ResourceLocation, JsonObject> loadPoses(ResourceManager resourceManager) {
        Gson gson = new Gson();
        Map<ResourceLocation, JsonObject> poseJsons = new HashMap<>();
        for (Map.Entry<ResourceLocation, Resource> entry : resourceManager.listResources("poses",
                loc -> loc.getPath().endsWith(".json")).entrySet()) {
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

            DataResult<IPoseModifier> res = PoseModifiers.CODEC.parse(JsonOps.INSTANCE, obj);
            if (res.error().isPresent()) {
                JCraft.LOGGER.error("Failed to parse pose {}: {}", id, res.error().get().message());
                continue;
            }

            res.result().ifPresent(pose -> poses.put(entry.getKey(), pose));
        }
    }
}
