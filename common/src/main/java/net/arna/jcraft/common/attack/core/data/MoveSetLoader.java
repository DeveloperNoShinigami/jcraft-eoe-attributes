package net.arna.jcraft.common.attack.core.data;

import com.google.common.collect.ImmutableMap;
import net.arna.jcraft.api.IAttackerType;
import net.arna.jcraft.common.entity.stand.*;
import net.arna.jcraft.common.spec.AnubisSpec;
import net.arna.jcraft.common.spec.BrawlerSpec;
import net.arna.jcraft.common.spec.VampireSpec;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class MoveSetLoader {
    static final Map<IAttackerType, Map<String, MoveSet<?, ?>>> moveSets = new HashMap<>();

    public static void init() {
        // Stands
        registerMS(AtumEntity.MOVE_SET);
        registerMS(ChariotRequiemEntity.MOVE_SET);
        registerMS(CinderellaEntity.MOVE_SET);
        registerMS(CMoonEntity.MOVE_SET);
        registerMS(CreamEntity.DEFAULT_MOVE_SET);
        registerMS(CreamEntity.HALF_BALL_MOVE_SET);
        registerMS(D4CEntity.MOVE_SET);
        registerMS(DiverDownEntity.MOVE_SET);
        registerMS(DragonsDreamEntity.MOVE_SET);
        registerMS(FooFightersEntity.MOVE_SET);
        registerMS(GEREntity.MOVE_SET);
        registerMS(GoldExperienceEntity.MOVE_SET);
        registerMS(GooGooDollsEntity.MOVE_SET);
        registerMS(HGEntity.MOVE_SET);
        registerMS(HorusEntity.MOVE_SET);
        registerMS(KillerQueenEntity.MOVE_SET);
        registerMS(KingCrimsonEntity.MOVE_SET);
        registerMS(KQBTDEntity.MOVE_SET);
        registerMS(MadeInHeavenEntity.MOVE_SET);
        registerMS(MagiciansRedEntity.MOVE_SET);
        registerMS(MetallicaEntity.MOVE_SET);
        registerMS(OsirisEntity.MOVE_SET);
        registerMS(PurpleHazeDistortionEntity.MOVE_SET);
        registerMS(PurpleHazeEntity.MOVE_SET);
        registerMS(ShadowTheWorldEntity.MOVE_SET);
        registerMS(SilverChariotEntity.DEFAULT_MOVE_SET);
        registerMS(SilverChariotEntity.POSSESSED_MOVE_SET);
        registerMS(SPTWEntity.MOVE_SET);
        registerMS(StarPlatinumEntity.MOVE_SET);
        registerMS(TheFoolEntity.MOVE_SET);
        registerMS(TheHandEntity.MOVE_SET);
        registerMS(TheSunEntity.MOVE_SET);
        registerMS(TheWorldEntity.MOVE_SET);
        registerMS(TheWorldOverHeavenEntity.MOVE_SET);
        registerMS(WhiteSnakeEntity.DEFAULT_MOVE_SET);
        registerMS(WhiteSnakeEntity.REMOTE_MOVE_SET);

        // Specs
        registerMS(AnubisSpec.MOVE_SET);
        registerMS(BrawlerSpec.MOVE_SET);
        registerMS(VampireSpec.MOVE_SET);
    }

    private static void registerMS(MoveSet<?, ?> ms) {
        Map<String, MoveSet<?, ?>> moveSets = MoveSetLoader.moveSets.computeIfAbsent(ms.getType(), k -> new HashMap<>());
        if (moveSets.containsKey(ms.getName())) {
            throw new IllegalArgumentException("Duplicate moveset " + ms.getName() + " for " + ms.getType());
        }
        moveSets.put(ms.getName(), ms);
    }

    public static Map<IAttackerType, Map<String, MoveSet<?, ?>>> getMoveSets() {
        return moveSets.entrySet().stream()
                .map(e -> Map.entry(e.getKey(), Collections.unmodifiableMap(e.getValue())))
                .collect(
                        ImmutableMap::<IAttackerType, Map<String, MoveSet<?, ?>>>builder,
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
