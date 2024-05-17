package net.arna.jcraft.common.entity.projectile;

import net.arna.jcraft.common.component.living.CommonHitPropertyComponent;
import net.arna.jcraft.common.entity.stand.StandEntity;
import net.arna.jcraft.registry.JStatusRegistry;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import mod.azure.azurelib.animatable.GeoEntity;
import mod.azure.azurelib.core.animatable.instance.AnimatableInstanceCache;
import mod.azure.azurelib.core.animation.AnimatableManager;
import mod.azure.azurelib.core.animation.AnimationController;
import mod.azure.azurelib.core.animation.AnimationState;
import mod.azure.azurelib.core.animation.RawAnimation;
import mod.azure.azurelib.core.object.PlayState;
import mod.azure.azurelib.util.AzureLibUtil;

public class RedBindEntity extends JAttackEntity implements GeoEntity {
    private LivingEntity boundEntity;
    private float boundHealth;
    public static final int ticksToLive = 60;
    private int timeLeft = ticksToLive;
    private static final EntityDataAccessor<Boolean> EXPLODED;
    private static final EntityDataAccessor<Float> WIDTH;

    static {
        EXPLODED = SynchedEntityData.defineId(RedBindEntity.class, EntityDataSerializers.BOOLEAN);
        WIDTH = SynchedEntityData.defineId(RedBindEntity.class, EntityDataSerializers.FLOAT);
    }

    public boolean hasExploded() {
        return this.entityData.get(EXPLODED);
    }

    public float getBoundWidth() {
        return entityData.get(WIDTH);
    }

    public void setBoundEntity(@NotNull LivingEntity boundEntity) {
        this.boundEntity = boundEntity;
        this.boundHealth = boundEntity.getHealth();
        this.entityData.set(WIDTH, (float) boundEntity.getBoundingBox().getSize());
        this.startRiding(boundEntity, true);
        boundEntity.addEffect(new MobEffectInstance(JStatusRegistry.STANDLESS.get(), timeLeft, 0, true, false));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(EXPLODED, false);
        entityData.define(WIDTH, 1f);
    }

    public RedBindEntity(EntityType<? extends LivingEntity> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    public double getPassengersRidingOffset() {
        return -1.0;
    }

    @Override
    public void tick() {
        if (!level().isClientSide) {
            if (boundEntity == null) { // If boundEntity data was wiped, attempt to recover
                if (getVehicle() instanceof LivingEntity living) {
                    setBoundEntity(living);
                }
            } else if (!isPassenger() && !hasExploded()) { // If detached
                detonate();
            }

            if (boundEntity == null) {
                discard();
            } else if (!hasExploded() && (--timeLeft <= 0 || boundEntity.getHealth() < boundHealth)) {
                detonate();
            }

            // In practice, redbind lasts slightly longer than the duration, so to account for this,
            // we add two ticks of standless until we're actually done.
            if (boundEntity != null && boundEntity.getEffect(JStatusRegistry.STANDLESS.get()) == null) {
                boundEntity.addEffect(new MobEffectInstance(JStatusRegistry.STANDLESS.get(), 2, 0, true, false));
            }
        }

        super.tick();
    }

    private void detonate() {
        if (master != null) {
            Vec3 vel = boundEntity.position().add(0, 0.5, 0).subtract(master.position());
            Vec3 launch = vel.normalize().scale(1.25);
            StandEntity.damageLogic(boundEntity.level(), boundEntity, launch, 20, 3, true,
                    6, false, 4, level().damageSources().mobAttack(master), master, CommonHitPropertyComponent.HitAnimation.MID, false, true);
        }

        entityData.set(EXPLODED, true);
        kill();
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.LAVA_EXTINGUISH;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.LAVA_EXTINGUISH;
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    // Animations
    private final AnimatableInstanceCache cache = AzureLibUtil.createInstanceCache(this);

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    private PlayState predicate(AnimationState<RedBindEntity> state) {
        return state.setAndContinue(RawAnimation.begin().thenLoop(hasExploded() ? "animation.red_bind.explode" : "animation.red_bind.idle"));
    }
}
