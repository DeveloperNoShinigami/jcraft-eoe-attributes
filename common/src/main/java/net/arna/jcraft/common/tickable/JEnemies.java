package net.arna.jcraft.common.tickable;

import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.component.living.CommonStandComponent;
import net.arna.jcraft.common.entity.stand.StandEntity;
import net.arna.jcraft.platform.JComponentPlatformUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.CombatEntry;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;

import java.util.*;

import static net.arna.jcraft.common.entity.stand.StandEntity.standUserCombatAI;

/**
 * Stores and updates all MobEntities that use Stands.
 */
public class JEnemies {
    private static final TickableHashMap<Mob, ResourceKey<Level>> enemies = new TickableHashMap<>();

    public static void add(Mob entity) {
        if (entity.level().isClientSide()) {
            throw new UnsupportedOperationException("Attempted to add an enemy to JEnemies from the clientside!");
        }
        if (enemies.containsKey(entity)) {
            return;
        }

        add(entity, entity.level().dimension());
    }

    public static void add(Mob entity, ResourceKey<Level> registryKey) {
        enemies.add(entity, registryKey);
    }

    public static void tick(MinecraftServer server) {
        enemies.tick(iter -> {
            final Map.Entry<Mob, ResourceKey<Level>> enemyData = iter.next();
            final Mob enemy = enemyData.getKey();

            if (enemy.isAlive()) {
                if (!enemy.isNoAi()) {
                    final ServerLevel world = server.getLevel(enemyData.getValue());
                    final CommonStandComponent standComponent = JComponentPlatformUtils.getStandData(enemy);
                    if (standComponent.getType() != null) {
                        final StandEntity<?, ?> stand = standComponent.getStand();
                        if (stand == null) {
                            JCraft.summon(world, enemy);
                        } else {
                            final LivingEntity target = enemy.getTarget();

                            // Combat AI
                            if (target != null && target.isAlive()) {
                                standUserCombatAI(enemy, target, stand);
                            } else {
                                // Targeting priority: top to bottom
                                final LinkedList<LivingEntity> targets = new LinkedList<>();
                                targets.add(enemy.getKillCredit());
                                final CombatEntry damageRec = enemy.getCombatTracker().getMostSignificantFall();
                                if (damageRec != null) {
                                    final Entity targetEntity = damageRec.source().getEntity();
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
                                                () -> {
                                                    if (stand.hasUser()) {
                                                        stand.standUserPassiveAI();
                                                    }
                                                }
                                        );
                            }
                        }
                    }
                }
            } else {
                iter.remove();
            }
        });
    }
}
