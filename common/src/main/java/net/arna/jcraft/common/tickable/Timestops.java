package net.arna.jcraft.common.tickable;

import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.util.DimensionData;
import net.arna.jcraft.common.util.JUtils;
import net.arna.jcraft.platform.JComponentPlatformUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Timestops {
    protected static final List<DimensionData> timestops = new ArrayList<>();

    public static void enqueue(DimensionData dimensionData) {
        timestops.add(dimensionData);
    }

    public static void remove(DimensionData dimensionData) {
        timestops.remove(dimensionData);
    }

    public static void tick(MinecraftServer server) {
        List<DimensionData> newActiveTimestops = new ArrayList<>();

        for (DimensionData timestop : timestops) {
            Entity user = timestop.user;
            //JCraft.LOGGER.info("SERVER: Ticking timestop " + timestop + " with user " + user + " and duration " + timestop.timer);

            if (user != null && user.isAlive() && timestop.timer-- > 0) {
                ServerLevel world = server.getLevel(timestop.worldKey);
                if (world == null) {
                    JCraft.LOGGER.fatal("World that timestop belongs to no longer exists! Key: " + timestop.worldKey + " Timestopper: " + user);
                    continue;
                }

                Vec3 pos = timestop.pos;

                List<? extends Entity> toStop = world.getEntitiesOfClass(Entity.class,
                        new AABB(pos.add(96.0, 96.0, 96.0), pos.subtract(96.0, 96.0, 96.0)), EntitySelector.NO_CREATIVE_OR_SPECTATOR);

                for (Entity entity : toStop) {
                    if (!entity.isPassenger() && entity != user && (!(entity instanceof LivingEntity living) || entity != JUtils.getStand(living)) &&
                            entity != user.getVehicle()) {
                        if (JComponentPlatformUtils.getTimeStopData(entity).isPresent()) {
                            JComponentPlatformUtils.getTimeStopData(entity).get().setTicks(2);
                        }
                    }
                }

                newActiveTimestops.add(timestop);
            }
        }

        timestops.clear();
        timestops.addAll(newActiveTimestops);
    }

    public static boolean isInTSRange(Vec3 pos) {
        for (DimensionData timeStop : timestops) {
            if (timeStop != null) {
                if (timeStop.pos.distanceToSqr(pos.x(), pos.y(), pos.z()) <= 65536) {
                    return true;
                }
            }
        }

        return false;
    }

    public static boolean isInTSRange(BlockPos pos) {
        for (DimensionData timeStop : timestops) {
            if (timeStop != null && timeStop.pos.distanceToSqr(pos.getX(), pos.getY(), pos.getZ()) <= 65536) {
                return true;
            }
        }

        return false;
    }

    public static int getTicksIfInTSRange(BlockPos pos) {
        for (DimensionData timeStop : timestops) {
            if (timeStop != null && timeStop.pos.distanceToSqr(pos.getX(), pos.getY(), pos.getZ()) <= 65536) {
                return timeStop.timer;
            }
        }

        return 0;
    }

    public static @Nullable DimensionData getTimestop(Entity entity) {
        for (DimensionData d : timestops) {
            if (d.user == entity) {
                return d;
            }
        }
        return null;
    }
}
