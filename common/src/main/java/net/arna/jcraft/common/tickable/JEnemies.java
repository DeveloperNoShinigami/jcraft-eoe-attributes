package net.arna.jcraft.common.tickable;

import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.component.living.CommonStandComponent;
import net.arna.jcraft.common.entity.stand.StandEntity;
import net.arna.jcraft.platform.JComponentPlatformUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;

import java.util.*;

import static net.arna.jcraft.common.entity.stand.StandEntity.standUserCombatAI;

/**
 * Stores and updates all MobEntities that use Stands.
 */
// todo: standardize all tickables with a concurrent mod-safe tick method or data structure
public class JEnemies {
    private static final HashMap<Mob, ResourceKey<Level>> enemies = new HashMap<>();
    /**
     * A queue designed to prevent any ConcurrentModificationException.
     * Stores to-be JEnemies temporarily if they were attempted to be registered while {@link JEnemies#ticking} is true.
     */
    private static final Queue<Mob> queuedEnemies = new LinkedList<>();
    private static boolean ticking = false;

    public static void add(Mob entity) {
        if (entity.level().isClientSide()) {
            throw new UnsupportedOperationException("Attempted to add an enemy to JEnemies from the clientside!");
        }
        if (enemies.containsKey(entity)) {
            return;
        }

        if (ticking) {
            queuedEnemies.add(entity);
        } else {
            add(entity, entity.level().dimension());
        }
    }

    public static void add(Mob entity, ResourceKey<Level> registryKey) {
        enemies.put(entity, registryKey);
    }

    public static void tick(MinecraftServer server) {
        if (ticking) {
            JCraft.LOGGER.error("Tried to tick JEnemies while already ticking!");
            return;
        }
        ticking = true;

        while (!queuedEnemies.isEmpty()) {
            Mob queued = queuedEnemies.peek();
            add(queued, queued.level().dimension());
            queuedEnemies.remove();
        }

        Iterator<Map.Entry<Mob, ResourceKey<Level>>> iter = enemies.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<Mob, ResourceKey<Level>> enemyData = iter.next();
            Mob enemy = enemyData.getKey();

            if (!enemy.isAlive()) {
                iter.remove();
                continue;
            }

            ServerLevel world = server.getLevel(enemyData.getValue());

            if (enemy.isNoAi()) {
                continue;
            }
            CommonStandComponent standComponent = JComponentPlatformUtils.getStandData(enemy);
            if (standComponent.getType() != null) {
                StandEntity<?, ?> stand = standComponent.getStand();
                if (stand == null) {
                    JCraft.summon(world, enemy);
                } else {
                    LivingEntity target = enemy.getTarget();

                    // Combat AI
                    if (target != null && target.isAlive()) {
                        standUserCombatAI(enemy, target, stand);
                    } else {
                        // Targeting priority: top to bottom
                        LinkedList<LivingEntity> targets = new LinkedList<>();
                        targets.add(enemy.getKillCredit());
                        var damageRec = enemy.getCombatTracker().getMostSignificantFall();
                        if (damageRec != null) {
                            var targetEntity = damageRec.source().getEntity();
                            if (targetEntity instanceof LivingEntity living) {
                                targets.add(living);
                            }
                        }

                        targets.add(enemy.getLastHurtByMob());
                        // Shouldn't use canTarget because that applies a PlayerEntity only filter.
                        targets.stream()
                                .filter(potentialTarget -> potentialTarget != null &&
                                        potentialTarget.isAlive() &&
                                        // enemy.hasLineOfSight(potentialTarget) &&
                                        potentialTarget.canBeSeenAsEnemy())
                                .findFirst()
                                .ifPresentOrElse(
                                        selectedTarget -> {
                                            enemy.setTarget(selectedTarget);
                                            standUserCombatAI(enemy, selectedTarget, stand);
                                        },
                                        stand::standUserPassiveAI
                                );
                    }
                }
            }
        }

        ticking = false;
    }
}
