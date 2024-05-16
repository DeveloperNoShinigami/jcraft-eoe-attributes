package net.arna.jcraft.common.tickable;

import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.util.DimensionData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PastDimensions {
    protected static final List<DimensionData> dimensions = new ArrayList<>();

    public static void enqueue(DimensionData dimensionData) {
        dimensions.add(dimensionData);
    }

    public static void remove(DimensionData dimensionData) {
        dimensions.remove(dimensionData);
    }

    public static void tick(MinecraftServer server) {
        List<DimensionData> newDimensions = new ArrayList<>();

        for (DimensionData dimensionData : dimensions) {
            Entity user = dimensionData.user;
            if (user == null || !user.isAlive()) {
                continue;
            }

            ServerLevel original = server.getLevel(dimensionData.worldKey);
            if (user.level() == original) {
                continue;
            }

            if (--dimensionData.timer > 1) {
                newDimensions.add(dimensionData);
                continue;
            }

            Vec3 dimPos = user.position(); //dimValues.pos;
            if (user instanceof ServerPlayer player) {
                player.teleportTo(original, dimPos.x, dimPos.y, dimPos.z, player.getYRot(), player.getXRot());
            } else {
                JCraft.teleportToWorld(user, original, dimPos.x, dimPos.y, dimPos.z);
            }
        }

        if (JCraft.preloadLockTicks <= 0 && newDimensions.isEmpty()) // Nobody left in AU
        {
            JCraft.clearPreloadedChunks();
        }

        dimensions.clear();
        dimensions.addAll(newDimensions);
    }

    public static boolean tryExit(LivingEntity user, Set<? extends Entity> targets) {
        boolean isStored = false;

        for (DimensionData dimV : dimensions) {
            // Bring others out of the AU
            if (targets.contains(dimV.user)) {
                dimV.timer = 1;
                continue;
            }

            if (dimV.user != user) {
                continue;
            }
            isStored = true;
            dimV.timer = 1;
        }

        return isStored;
    }
}
