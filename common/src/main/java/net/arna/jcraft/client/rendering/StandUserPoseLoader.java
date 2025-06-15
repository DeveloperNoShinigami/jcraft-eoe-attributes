package net.arna.jcraft.client.rendering;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.api.pose.ModelType;
import net.arna.jcraft.api.pose.PoseModifiers;
import net.arna.jcraft.api.pose.modifier.IPoseModifier;
import net.arna.jcraft.api.stand.StandEntity;
import net.arna.jcraft.client.command.JPoseCommand;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.LivingEntity;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class StandUserPoseLoader {
    private static final Map<ResourceLocation, Map<ModelType<?>, IPoseModifier>> POSES = new HashMap<>();

    public static IPoseModifier getPose(final ModelType<?> type, final LivingEntity entity) {
        if (entity.getFirstPassenger() instanceof StandEntity<?, ?> stand) {
            return getPose(type, stand.getUserPose());
        }

        if (JPoseCommand.hasPose(type)) {
            return JPoseCommand.getPose();
        }

        return IPoseModifier.EMPTY;
    }

    public static IPoseModifier getPose(final ModelType<?> type, final ResourceLocation id) {
        IPoseModifier pose = POSES.getOrDefault(id, Collections.emptyMap()).get(type);
        if (pose == null) {
            if (JPoseCommand.hasPose(type))
                return JPoseCommand.getPose();
            return IPoseModifier.EMPTY;
        }

        return pose;
    }

    public static CompletableFuture<Void> onReload(final PreparableReloadListener.PreparationBarrier preparationBarrier,
                                                    final ResourceManager resourceManager, final ProfilerFiller profilerFiller,
                                                    final ProfilerFiller profilerFiller1, final Executor executor, final Executor executor1) {
        return CompletableFuture.supplyAsync(() -> loadPoses(resourceManager))
                .thenCompose(preparationBarrier::wait)
                .thenAcceptAsync(StandUserPoseLoader::parsePoses);
    }

    private static Map<ResourceLocation, JsonObject> loadPoses(final ResourceManager resourceManager) {
        final Gson gson = new Gson();
        final Map<ResourceLocation, JsonObject> poseJsons = new HashMap<>();
        final Map<ResourceLocation, Resource> resources = resourceManager.listResources("poses",
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

    private static void parsePoses(final Map<ResourceLocation, JsonObject> poseJsons) {
        POSES.clear();

        for (final Map.Entry<ResourceLocation, JsonObject> entry : poseJsons.entrySet()) {
            final ResourceLocation id = entry.getKey();
            final JsonObject obj = entry.getValue();

            final ResourceLocation standType = id.withPath(path -> path.split("/")[1]);

            // Remove ".json" suffix
            final String typeName = id.getPath().substring(id.getPath().lastIndexOf('/') + 1, id.getPath().length() - 5);
            final ModelType<?> modelType = ModelType.fromName(typeName);
            if (modelType == null) {
                JCraft.LOGGER.error("Unknown model type {} for pose {}", typeName, id);
                continue;
            }

            final DataResult<IPoseModifier> res = PoseModifiers.CODEC.parse(JsonOps.INSTANCE, obj);
            if (res.error().isPresent()) {
                JCraft.LOGGER.error("Failed to parse pose {}: {}", id, res.error().get().message());
                continue;
            }

            //noinspection OptionalGetWithoutIsPresent // checked above
            final IPoseModifier pose = res.result().get();
            POSES.computeIfAbsent(standType, t -> new HashMap<>())
                    .put(modelType, pose);
        }
    }
}
