package net.arna.jcraft.common.entity.ai.goal;

import net.arna.jcraft.common.entity.PlayerCloneEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.pathfinder.Path;
import java.util.EnumSet;

public class CloneAttackGoal extends Goal {
    private final PlayerCloneEntity clone;
    private final LookControl cloneLookControl;
    private final PathNavigation cloneNavigation;
    private LivingEntity target;
    private final double speed;
    private long lastUpdateTime;

    public CloneAttackGoal(final PlayerCloneEntity mob, final double speed) {
        clone = mob;
        cloneLookControl = mob.getLookControl();
        cloneNavigation = mob.getNavigation();
        this.speed = speed;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    public boolean canUse() {
        long l = clone.level().getGameTime();
        if (l - this.lastUpdateTime < 10L) {
            return false;
        } else {
            this.lastUpdateTime = l;
            target = clone.getTarget();
            if (target == null) {
                return false;
            } else if (!target.isAlive()) {
                return false;
            } else {
                Path path = cloneNavigation.createPath(target, 0);
                if (path != null) {
                    return true;
                } else {
                    return this.getSquaredMaxAttackDistance(target) >= clone.distanceToSqr(target);
                }
            }
        }
    }

    public boolean canContinueToUse() {
        if (target == null) {
            return false;
        } else if (!target.isAlive() || target.isRemoved()) {
            return false;
        } else if (clone.distanceToSqr(target) > 1024.0D) {
            return false;
        } else if (target == clone.getMaster()) {
            return false;
        } else {
            return !cloneNavigation.isDone() || this.canUse();
        }
    }

    public void start() {
        clone.setAggressive(true);
    }

    public void stop() {
        target = null;
        clone.setTarget(null);
        clone.setAggressive(false);
        cloneNavigation.stop();
    }

    public boolean requiresUpdateEveryTick() {
        return true;
    }

    public void tick() {
        if (target == null) {
            return;
        }

        cloneNavigation.moveTo(target, speed);
        cloneLookControl.setLookAt(target, 30.0F, 30.0F);
        double d = this.getSquaredMaxAttackDistance(target);
        if (target.distanceToSqr(clone) <= d && clone.getCooldown() <= 0) {
            clone.startCooldown();
            clone.swing(InteractionHand.MAIN_HAND);
            clone.doHurtTarget(target);
        }
    }

    private double getSquaredMaxAttackDistance(final LivingEntity entity) {
        return 1.44F + entity.getBbWidth(); // 0.6^2 * 4 i.e. cloneWidth^2 * 2^2
    }
}
