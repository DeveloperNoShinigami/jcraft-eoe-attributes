package net.arna.jcraft.mixin;

import net.arna.jcraft.common.entity.stand.StandEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RandomStrollGoal.class)
public abstract class RandomStrollGoalMixin {
    @Shadow @Final protected PathfinderMob mob;

    @Shadow protected boolean forceTrigger;

    @Shadow @Final private boolean checkNoActionTime;

    @Shadow protected int interval;

    @Shadow @Nullable protected abstract Vec3 getPosition();

    @Shadow protected double wantedX;

    @Shadow protected double wantedZ;

    @Shadow protected double wantedY;

    @Inject(
            method = "canUse",
            at = @At(value = "RETURN", ordinal = 0),
            cancellable = true
    )
    private void jcraft$walkWithStand(CallbackInfoReturnable<Boolean> cir) {
        if (mob.getFirstPassenger() instanceof StandEntity<?,?>) {
            if (!this.forceTrigger) {
                if (this.checkNoActionTime && this.mob.getNoActionTime() >= 100) {
                    cir.setReturnValue(false);
                }

                if (this.mob.getRandom().nextInt(reducedTickDelay(this.interval)) != 0) {
                    cir.setReturnValue(false);
                }
            }

            Vec3 vec3 = this.getPosition();
            if (vec3 == null) {
                cir.setReturnValue(false);
            } else {
                this.wantedX = vec3.x;
                this.wantedY = vec3.y;
                this.wantedZ = vec3.z;
                this.forceTrigger = false;
                cir.setReturnValue(true);
            }
        }
    }

    @Inject(
            method = "canContinueToUse",
            at = @At("RETURN"),
            cancellable = true
    )
    private void jcraft$allowWalkingWithStand(CallbackInfoReturnable<Boolean> cir) {
        if (mob.getFirstPassenger() instanceof StandEntity<?,?> && !this.mob.getNavigation().isDone()) cir.setReturnValue(true);
    }

    @Unique
    private static int reducedTickDelay(int reduction) {
        return Mth.positiveCeilDiv(reduction, 2);
    }
}
