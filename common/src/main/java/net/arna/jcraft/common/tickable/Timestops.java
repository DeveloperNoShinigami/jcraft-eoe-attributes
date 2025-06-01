package net.arna.jcraft.common.tickable;

import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.component.living.CommonStandComponent;
import net.arna.jcraft.common.entity.stand.GEREntity;
import net.arna.jcraft.common.entity.stand.KingCrimsonEntity;
import net.arna.jcraft.common.entity.stand.StandEntity;
import net.arna.jcraft.common.util.DimensionData;
import net.arna.jcraft.common.util.JUtils;
import net.arna.jcraft.platform.JComponentPlatformUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * For clientside timestops, see {@link net.arna.jcraft.client.util.JClientUtils#activeTimestops}
 */
public class Timestops {
    protected static final List<DimensionData> timestops = new ArrayList<>();

    public static void enqueue(DimensionData dimensionData) {
        timestops.add(dimensionData);
    }

    public static void remove(DimensionData dimensionData) {
        timestops.remove(dimensionData);
    }

    /**
     * Common-side. Thus cannot access serverside data.
     */
    public static final Predicate<Entity> timestopPredicate = entity -> {
        if (entity instanceof Player player) {
            if (entity.isSpectator()) return false;
            if (player.isCreative()) return false;
        }
        if (entity instanceof LivingEntity living) {
            StandEntity<?, ?> stand = null;
            if (living instanceof StandEntity<?,?> livingStand) {
                stand = livingStand;
            } else {
                CommonStandComponent standComponent = JComponentPlatformUtils.getStandComponent(living);
                if (standComponent.getStand() != null) stand = standComponent.getStand();
            }

            if (stand != null) {
                if (stand instanceof KingCrimsonEntity kingCrimson) {
                    if (kingCrimson.getTETime() > 0) return false;
                }
                if (stand instanceof GEREntity requiem) {
                    if (requiem.getState() == GEREntity.State.COUNTER) return false;
                }
            }
        }
        return true;
    };

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
                        new AABB(pos.add(96.0, 96.0, 96.0), pos.subtract(96.0, 96.0, 96.0)), timestopPredicate);

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
