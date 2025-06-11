package net.arna.jcraft.common.entity.projectile;

import lombok.NonNull;
import mod.azure.azurelib.animatable.GeoEntity;
import mod.azure.azurelib.core.animatable.GeoAnimatable;
import mod.azure.azurelib.core.animatable.instance.AnimatableInstanceCache;
import mod.azure.azurelib.core.animation.AnimatableManager;
import mod.azure.azurelib.core.animation.AnimationController;
import mod.azure.azurelib.core.animation.AnimationState;
import mod.azure.azurelib.core.animation.RawAnimation;
import mod.azure.azurelib.core.object.PlayState;
import mod.azure.azurelib.util.AzureLibUtil;
import net.arna.jcraft.api.component.living.CommonHitPropertyComponent;
import net.arna.jcraft.api.stand.StandEntity;
import net.arna.jcraft.common.util.IOwnable;
import net.arna.jcraft.common.util.JUtils;
import net.arna.jcraft.api.registry.JEntityTypeRegistry;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class SandTornadoEntity extends JAttackEntity implements GeoEntity, IOwnable {
    private static final EntityDataAccessor<Boolean> DISAPPEARED;
    private int hitsLeft = 5;

    static {
        DISAPPEARED = SynchedEntityData.defineId(SandTornadoEntity.class, EntityDataSerializers.BOOLEAN);
    }

    public SandTornadoEntity(Level world) {
        super(JEntityTypeRegistry.SAND_TORNADO.get(), world);
    }

    public boolean hasDisappeared() {
        return this.entityData.get(DISAPPEARED);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(DISAPPEARED, false);
    }

    private void disappear() {
        entityData.set(DISAPPEARED, true);
        kill();
    }

    @Override
    public void tick() {
        super.tick();
        if (hasDisappeared()) {
            if (deathTime > 26 || tickCount > 526) discard();
            return;
        }

        final Vec3 circulation = new Vec3(Mth.sin(tickCount * 0.25f) * 0.3f, 0.0, Mth.cos(tickCount * 0.25f) * 0.3f);

        if (level().isClientSide) {
            for (int i = 0; i < 3; i++) {
                level().addParticle(
                        new BlockParticleOption(ParticleTypes.BLOCK, Blocks.SAND.defaultBlockState()),
                        getX() + random.nextFloat() - 0.5f,
                        getY() + random.nextFloat() * 2f,
                        getZ() + random.nextFloat() - 0.5f,
                        circulation.x, 0, circulation.z
                );
            }
        } else if (tickCount % 5 == 0) {
            if (master == null) {
                if (isAlive()) {
                    discard();
                }
                return;
            }

            final Set<LivingEntity> toHurt = JUtils.generateHitbox(level(), getEyePosition(), 1.8, Set.of(this, master));

            if (toHurt.isEmpty()) {
                setDeltaMovement(getDeltaMovement().add(getLookAngle().scale(0.5)).scale(0.4));
            } else {
                setDeltaMovement(getDeltaMovement().scale(0.25));
                for (LivingEntity living : toHurt) {
                    final LivingEntity target = JUtils.getUserIfStand(living);
                    if (target.isPassengerOfSameVehicle(master)) {
                        return;
                    }
                    StandEntity.damageLogic(level(), target, circulation, 10, 1, false, 2f, true, 6,
                            level().damageSources().mobAttack(master), master, CommonHitPropertyComponent.HitAnimation.MID, false);
                }
                hitsLeft--;
            }

            hurtMarked = true;

            if (hitsLeft < 1 || getHealth() <= 0 || tickCount >= 500) {
                disappear();
            }
        }
    }

    // Physical properties
    @Override
    public boolean isInvulnerableTo(DamageSource damageSource) {
        return !damageSource.is(DamageTypes.FELL_OUT_OF_WORLD) && !damageSource.is(DamageTypes.GENERIC_KILL);
    }

    @Override
    protected void doPush(@NonNull Entity entity) {
    }

    @Override
    public void push(@NonNull Entity entity) {
    }

    @Override
    public boolean canCollideWith(@NonNull Entity other) {
        return false;
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(@NonNull DamageSource source) {
        return SoundEvents.SAND_STEP;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.SAND_BREAK;
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    @Override
    public boolean startRiding(@NonNull Entity entity, boolean force) {
        return false;
    }

    public static AttributeSupplier.Builder createTornadoAttributes() {
        return createLivingAttributes() // This must be used instead of DefaultAttributeContainer.builder() due to compatibility with step-height-entity-attribute
                .add(Attributes.MAX_HEALTH, 20)
                .add(Attributes.KNOCKBACK_RESISTANCE)
                .add(Attributes.MOVEMENT_SPEED)
                .add(Attributes.ARMOR)
                .add(Attributes.ARMOR_TOUGHNESS);
    }

    @Override
    public void addAdditionalSaveData(@NonNull CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        writeMasterNbt(tag);
    }

    @Override
    public void readAdditionalSaveData(@NonNull CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        readMasterNbt(tag);
    }

    // Animations
    private final AnimatableInstanceCache cache = AzureLibUtil.createInstanceCache(this);

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("animation.sandtornado.idle");
    private static final RawAnimation DISAPPEAR = RawAnimation.begin().thenLoop("animation.sandtornado.disappear");
    private PlayState predicate(AnimationState<GeoAnimatable> state) {
        return state.setAndContinue(hasDisappeared() ? DISAPPEAR : IDLE);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
