package net.arna.jcraft.common.tickable;

import lombok.Getter;
import lombok.NonNull;
import net.arna.jcraft.JCraft;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Revivables {
    public static class ReviveData {
        @NonNull
        @Getter
        final EntityType<?> type;
        @NonNull
        @Getter
        final Vec3 pos;
        @NonNull
        final ResourceKey<Level> worldKey;
        public int timer = 20 * 60; // 1min

        private ReviveData(@NonNull EntityType<?> type, @NonNull Vec3 pos, @NonNull ResourceKey<Level> worldKey) {
            this.type = type;
            this.pos = pos;
            this.worldKey = worldKey;
        }
    }

    protected static final List<ReviveData> revivables = new ArrayList<>();
    private static final Queue<ReviveData> queuedAdditions = new LinkedList<>();
    private static final Queue<ReviveData> queuedRemovals = new LinkedList<>();
    private static boolean ticking = false;

    public static void addRevivable(EntityType<?> type, Vec3 pos, ResourceKey<Level> worldKey) {
        ReviveData newReviveData = new ReviveData(type, pos, worldKey);
        if (ticking) {
            queuedAdditions.add(newReviveData);
        } else {
            revivables.add(newReviveData);
        }
    }

    public static void removeRevivable(ReviveData reviveData) {
        if (ticking) {
            queuedRemovals.add(reviveData);
        } else {
            revivables.remove(reviveData);
        }
    }

    public static void revive(@NonNull MinecraftServer server, @NonNull ReviveData revivable) {
        ServerLevel serverWorld = server.getLevel(revivable.worldKey);
        if (serverWorld == null) {
            JCraft.LOGGER.fatal("Tried to revive entity from invalid ServerWorld!");
            return;
        }
        Entity entity = revivable.type.create(serverWorld);
        if (entity == null) {
            JCraft.LOGGER.warn("Failed to create entity from EntityType: " + revivable.type);
            return;
        }
        entity.setPos(revivable.pos);
        serverWorld.addFreshEntity(entity);
        removeRevivable(revivable);
    }

    public static boolean removeRevivableAt(Vec3 pos) {
        for (ReviveData revivable : revivables) {
            if (revivable.pos == pos) {
                return revivables.remove(revivable);
            }
        }
        return false;
    }


    public static List<ReviveData> getAround(Vec3 pos, double distance) {
        List<ReviveData> out = new ArrayList<>();
        for (ReviveData revivable : revivables) {
            if (revivable.pos.distanceToSqr(pos) <= distance * distance) {
                out.add(revivable);
            }
        }
        return out;
    }

    public static void tick(MinecraftServer server) {
        if (ticking) {
            JCraft.LOGGER.error("Tried to tick Revivables while already ticking!");
            return;
        }
        ticking = true;

        revivables.addAll(queuedAdditions);
        revivables.removeAll(queuedRemovals);
        queuedAdditions.clear();
        queuedRemovals.clear();

        List<ReviveData> newRevivables = new ArrayList<>();
        for (ReviveData revivable : revivables) {
            if (--revivable.timer > 1) {
                newRevivables.add(revivable);
            }
        }

        revivables.clear();
        revivables.addAll(newRevivables);

        ticking = false;
    }
}
