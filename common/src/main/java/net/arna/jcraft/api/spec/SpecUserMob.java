package net.arna.jcraft.api.spec;

import mod.azure.azurelib.animatable.GeoEntity;
import mod.azure.azurelib.constant.DefaultAnimations;
import mod.azure.azurelib.core.animatable.GeoAnimatable;
import mod.azure.azurelib.core.animatable.instance.AnimatableInstanceCache;
import mod.azure.azurelib.core.animation.AnimatableManager;
import mod.azure.azurelib.core.animation.AnimationController;
import mod.azure.azurelib.core.animation.AnimationState;
import mod.azure.azurelib.core.animation.RawAnimation;
import mod.azure.azurelib.core.object.PlayState;
import mod.azure.azurelib.util.AzureLibUtil;
import net.arna.jcraft.api.component.player.CommonSpecComponent;
import net.arna.jcraft.common.tickable.JEnemies;
import net.arna.jcraft.platform.JComponentPlatformUtils;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;

public class SpecUserMob extends PathfinderMob implements JSpecHolder, GeoEntity {
    protected final CommonSpecComponent component;
    private final AnimatableInstanceCache cache = AzureLibUtil.createInstanceCache(this);

    private static final EntityDataAccessor<Boolean> ANIMATION_RESET = SynchedEntityData.defineId(SpecUserMob.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(SpecUserMob.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Float> ANIMATION_SPEED = SynchedEntityData.defineId(SpecUserMob.class, EntityDataSerializers.FLOAT);

    public SpecUserMob(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);

        component = JComponentPlatformUtils.getSpecData(this);

        if (level.isClientSide()) return;
        JEnemies.add(this);
    }

    @Override
    public void tick() {
        setSpeed((float) getAttribute(Attributes.MOVEMENT_SPEED).getValue());

        super.tick();

        if (level().isClientSide()) return;

        entityData.set(ANIMATION_RESET, false);

        JSpec<?, ?> spec = getSpec();
        if (spec != null) {
            final LivingEntity target = getTarget();

            if (target != null) {
                lookAt(EntityAnchorArgument.Anchor.EYES, target.getEyePosition());

                if (target.distanceToSqr(this) > 1.0) {
                    Vec3 posTowards = DefaultRandomPos.getPosTowards(this, 2, 7, target.position(), 1.5707963705062866);
                    if (posTowards != null) getNavigation().moveTo(posTowards.x, posTowards.y, posTowards.z, 1.0);
                }
            }

            spec.tickSpec();

            if (spec.getMoveStun() <= 0) setAnimation("", 1.0f);
        } else {
            setAnimation("", 1.0f);
        }
    }

    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(3, new RandomStrollGoal(this, 0.6));
        goalSelector.addGoal(5, new RandomLookAroundGoal(this));
        goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 8.0F));
        goalSelector.addGoal(10, new OpenDoorGoal(this, true));

        targetSelector.addGoal(1, new HurtByTargetGoal(this));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal(this, Player.class, true));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(ANIMATION, "");
        entityData.define(ANIMATION_SPEED, 1.0f);
        entityData.define(ANIMATION_RESET, false);
    }

    @Override
    public void setSpecType(SpecType type) {
        component.setType(type);
    }

    @Override
    public JSpec<?, ?> getSpec() {
        return component.getSpec();
    }

    @Override
    public void setAnimation(String animationID, float animationSpeed) {
        entityData.set(ANIMATION_RESET, entityData.get(ANIMATION).equals(animationID));

        entityData.set(ANIMATION, animationID);
        entityData.set(ANIMATION_SPEED, animationSpeed);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(DefaultAnimations.genericWalkIdleController(this));
        controllers.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    private static final Map<String, RawAnimation> cachedAnimations = new HashMap<>();
    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> state) {
        if (entityData.get(ANIMATION_RESET)) {
            state.resetCurrentAnimation();
        }

        final String animation = entityData.get(ANIMATION);

        if (animation.isEmpty()) return PlayState.STOP;
        if (!cachedAnimations.containsKey(animation)) cachedAnimations.put(animation, RawAnimation.begin().thenLoop(animation));

        final float speed = entityData.get(ANIMATION_SPEED);
        state.setControllerSpeed(speed);

        return state.setAndContinue(cachedAnimations.get(animation));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
