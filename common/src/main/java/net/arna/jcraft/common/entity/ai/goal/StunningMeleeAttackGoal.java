package net.arna.jcraft.common.entity.ai.goal;

import net.arna.jcraft.registry.JStatusRegistry;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.pathfinder.Path;
import java.util.EnumSet;

public class StunningMeleeAttackGoal extends Goal {
    protected final PathfinderMob mob;
    private final double speed;
    private final boolean pauseWhenMobIdle;
    private final int stunT;
    private Path path;
    private double targetX;
    private double targetY;
    private double targetZ;
    private int updateCountdownTicks;
    private int cooldown;
    private long lastUpdateTime;

    public StunningMeleeAttackGoal(final PathfinderMob mob, final double speed, final boolean pauseWhenMobIdle, final int stunT) {
        this.mob = mob;
        this.speed = speed;
        this.pauseWhenMobIdle = pauseWhenMobIdle;
        this.stunT = stunT;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    public boolean canUse() {
        long l = this.mob.level().getGameTime();
        if (l - this.lastUpdateTime < 20L) {
            return false;
        }

        lastUpdateTime = l;
        LivingEntity livingEntity = this.mob.getTarget();
        if (livingEntity == null) {
            return false;
        }
        if (!livingEntity.isAlive()) {
            return false;
        }

        path = mob.getNavigation().createPath(livingEntity, 0);
        if (path != null) {
            return true;
        }

        return this.getSquaredMaxAttackDistance(livingEntity) >=
                mob.distanceToSqr(livingEntity.getX(), livingEntity.getY(), livingEntity.getZ());
    }

    public boolean canContinueToUse() {
        LivingEntity livingEntity = mob.getTarget();
        if (livingEntity == null) {
            return false;
        }
        if (!livingEntity.isAlive()) {
            return false;
        }
        if (!pauseWhenMobIdle) {
            return !mob.getNavigation().isDone();
        }
        if (!mob.isWithinRestriction(livingEntity.blockPosition())) {
            return false;
        }

        return !(livingEntity instanceof Player) || !livingEntity.isSpectator() && !((Player) livingEntity).isCreative();
    }

    public void start() {
        mob.getNavigation().moveTo(path, speed);
        mob.setAggressive(true);
        updateCountdownTicks = 0;
        cooldown = 0;
    }

    public void stop() {
        LivingEntity livingEntity = mob.getTarget();
        if (!EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(livingEntity)) {
            mob.setTarget(null);
        }

        mob.setAggressive(false);
        mob.getNavigation().stop();
    }

    public boolean requiresUpdateEveryTick() {
        return true;
    }

    public void tick() {
        LivingEntity target = mob.getTarget();
        if (target == null) {
            return;
        }

        mob.getLookControl().setLookAt(target, 30.0F, 30.0F);
        double d = mob.distanceToSqr(target.getX(), target.getY(), target.getZ());
        updateCountdownTicks = Math.max(updateCountdownTicks - 1, 0);
        if ((pauseWhenMobIdle || mob.getSensing().hasLineOfSight(target)) && updateCountdownTicks <= 0 &&
                (targetX == 0.0 && targetY == 0.0 && targetZ == 0.0 ||
                        target.distanceToSqr(targetX, targetY, targetZ) >= 1.0 ||
                        mob.getRandom().nextFloat() < 0.05F)) {
            targetX = target.getX();
            targetY = target.getY();
            targetZ = target.getZ();
            updateCountdownTicks = 4 + mob.getRandom().nextInt(7) + (d > 256 ? d > 1024 ? 10 : 5 : 0);

            if (!mob.getNavigation().moveTo(target, speed)) {
                updateCountdownTicks += 15;
            }

            updateCountdownTicks = adjustedTickDelay(updateCountdownTicks);
        }

        cooldown = Math.max(cooldown - 1, 0);
        attack(target, d);
    }

    protected void attack(final LivingEntity target, final double squaredDistance) {
        double d = getSquaredMaxAttackDistance(target);
        if (!(squaredDistance <= d) || cooldown > 0) {
            return;
        }

        resetCooldown();
        mob.swing(InteractionHand.MAIN_HAND);
        if (mob.doHurtTarget(target)) {
            target.addEffect(new MobEffectInstance(JStatusRegistry.DAZED.get(), this.stunT, 1, true, false));
        }
    }

    protected void resetCooldown() {
        this.cooldown = this.adjustedTickDelay(20);
    }

    protected boolean isCooledDown() {
        return this.cooldown <= 0;
    }

    protected int getCooldown() {
        return this.cooldown;
    }

    protected int getMaxCooldown() {
        return this.adjustedTickDelay(20);
    }

    protected double getSquaredMaxAttackDistance(final LivingEntity entity) {
        return (this.mob.getBbWidth() * 2.0F * this.mob.getBbWidth() * 2.0F + entity.getBbWidth());
    }
}
