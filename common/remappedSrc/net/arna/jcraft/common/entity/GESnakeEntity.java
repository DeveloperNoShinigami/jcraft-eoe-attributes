package net.arna.jcraft.common.entity;

import net.arna.jcraft.common.entity.ai.goal.StunningMeleeAttackGoal;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.level.EntityGetter;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Arrays;

public class GESnakeEntity extends TamableAnimal implements GeoEntity {
    public GESnakeEntity(EntityType<? extends TamableAnimal> entityType, Level world) {
        super(entityType, world);
        Arrays.fill(this.handDropChances, 1F);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel world, AgeableMob entity) {
        return null;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(4, new LeapAtTargetGoal(this, 0.4F));
        this.goalSelector.addGoal(5, new StunningMeleeAttackGoal(this, 1.0, true, 10));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, LivingEntity.class, 32.0F));
        this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));

        this.goalSelector.addGoal(6, new FollowOwnerGoal(this, 1.0, 10.0F, 2.0F, false));

        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
    }

    @Override
    public void tick() {
        super.tick();

        if (level().isClientSide()) {
            if (this.swinging) {
                this.swingTime += 1;

                if (this.swingTime > 10) {
                    this.swinging = false;
                    this.swingTime = 0;
                }
            }
        } else if (this.tickCount == 500) {
            spawnAtLocation(getMainHandItem());
            kill();
        } else if (this.isAlive() && this.tickCount > 500) { // Edge case, mostly dealing with unloading
            discard();
        }
    }

    // Animations
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "movement", 10, this::predicate));
        controllers.add(new AnimationController<>(this, "attack", 0, this::attackPredicate));
    }

    private PlayState attackPredicate(AnimationState<GESnakeEntity> state) {
        if (!swinging) {
            return PlayState.STOP;
        }

        state.setAnimation(RawAnimation.begin().thenLoop("animation.gesnake.attack"));
        return PlayState.CONTINUE;
    }

    private PlayState predicate(AnimationState<GESnakeEntity> state) {
        if (state.isMoving()) {
            state.setAnimation(RawAnimation.begin().thenLoop("animation.gesnake.move"));
            state.getController().setAnimationSpeed(1 + this.getDeltaMovement().length());
        } else {
            state.setAnimation(RawAnimation.begin().thenLoop("animation.gesnake.idle"));
        }

        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public EntityGetter level() {
        return null;
    }
}
