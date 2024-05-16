package net.arna.jcraft.common.entity.projectile;

import net.arna.jcraft.common.component.living.CommonHitPropertyComponent;
import net.arna.jcraft.common.entity.stand.StandEntity;
import net.arna.jcraft.common.util.IOwnable;
import net.arna.jcraft.common.util.JUtils;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.Set;

public class LifeDetectorEntity extends JAttackEntity implements GeoEntity {
    private static final EntityDataAccessor<Boolean> EXPLODED;
    public LivingEntity target;

    static {
        EXPLODED = SynchedEntityData.defineId(LifeDetectorEntity.class, EntityDataSerializers.BOOLEAN);
    }

    public boolean hasExploded() {
        return this.entityData.get(EXPLODED);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(EXPLODED, false);
    }

    public LifeDetectorEntity(EntityType<? extends LivingEntity> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    public boolean canAttack(LivingEntity target) {
        if (target == null || master == null) {
            return false;
        }
        if (target == this || target == master) {
            return false;
        }
        if (target.isPassengerOfSameVehicle(master)) {
            return false;
        }
        if (target instanceof IOwnable ownable && ownable.getMaster() == master) {
            return false;
        }
        return target.canBeSeenAsEnemy() && target.isAlive() && JUtils.canDamage(level().damageSources().mobAttack(master), target);
    }

    private void Explode() {
        setDeltaMovement(0, 0, 0);
        hurtMarked = true;

        Vec3 pos = position();
        Set<LivingEntity> hurt = JUtils.generateHitbox(level(), pos, 2.25, e -> true);
        for (LivingEntity living : hurt) {
            if (!canAttack(living)) {
                continue;
            }
            LivingEntity target = JUtils.getUserIfStand(living);
            Vec3 kbVec = target.position().subtract(pos).normalize();
            StandEntity.damageLogic(level(), target, kbVec, 10, 1, false, 5f, true, 9,
                    level().damageSources().mobAttack(master), master, CommonHitPropertyComponent.HitAnimation.MID, false);
        }

        entityData.set(EXPLODED, true);

        playSound(SoundEvents.FIRECHARGE_USE, 1f, 1f);

        kill();
    }

    @Override
    public void tick() {
        super.tick();
        if (master == null) {
            kill();
        }
        if (hasExploded()) {
            return;
        }

        if (level().isClientSide) {
            level().addParticle(
                    ParticleTypes.FLAME,
                    this.getX() + random.nextFloat() - 0.5f,
                    this.getY() + random.nextFloat() - 0.5f,
                    this.getZ() + random.nextFloat() - 0.5f,
                    0.0, 0.0, 0.0);
        } else {
            if (target == null) {
                if (this.tickCount % 2 == 0) {
                    LivingEntity finalTarget = null;
                    List<LivingEntity> targets = level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(32f), EntitySelector.ENTITY_STILL_ALIVE);

                    for (LivingEntity t :
                            targets) {
                        if (!canAttack(t)) {
                            continue;
                        }
                        if (finalTarget == null) {
                            finalTarget = t;
                            continue;
                        }
                        // Prioritise nearest
                        if (t.position().distanceToSqr(position()) < finalTarget.position().distanceToSqr(position())) {
                            finalTarget = t;
                        }
                    }

                    target = finalTarget;
                }
            } else if (target.isAlive()) {
                Vec3 eyePos = target.getEyePosition();
                lookAt(EntityAnchorArgument.Anchor.EYES, eyePos);
                if (this.distanceToSqr(eyePos) < 2.5) {
                    Explode(); //If closer than 1.58m
                }
            } else {
                target = null;
            }

            if (!hasExploded() && (this.tickCount >= 300 || getHealth() <= 0f)) {
                Explode();
            }

            // Lerp velocity to simulate inertia
            this.setDeltaMovement(
                    getDeltaMovement().add(getLookAngle().scale(0.5)).scale(0.25)
            );
            this.hurtMarked = true;
        }
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

    @Override
    public boolean startRiding(Entity entity, boolean force) {
        return false;
    }

    public static AttributeSupplier.Builder createDetectorAttributes() {
        return createLivingAttributes() // This must be used instead of DefaultAttributeContainer.builder() due to compatibility with step-height-entity-attribute
                .add(Attributes.MAX_HEALTH, 10)
                .add(Attributes.KNOCKBACK_RESISTANCE)
                .add(Attributes.MOVEMENT_SPEED)
                .add(Attributes.ARMOR)
                .add(Attributes.ARMOR_TOUGHNESS);
    }

    @Override
    protected AABB makeBoundingBox() { // Centered around 0,0,0 instead of 0,0.5,0
        double x = getX();
        double y = getY();
        double z = getZ();
        double s = hasExploded() ? 0.1 : 0.5;
        return new AABB(x + s, y + s, z + s, x - s, y - s, z - s);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (master == null) {
            return;
        }
        boolean ownerIsPlayer = master instanceof Player;
        tag.putBoolean("playerOwner", ownerIsPlayer);
        if (ownerIsPlayer) {
            tag.putUUID("ownerUUID", master.getUUID());
        } else {
            tag.putInt("ownerID", master.getId());
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        boolean ownerIsPlayer = tag.getBoolean("playerOwner");
        if (ownerIsPlayer) {
            master = level().getPlayerByUUID(tag.getUUID("ownerUUID"));
        } else {
            master = (LivingEntity) level().getEntity(tag.getInt("ownerID")); // Always is living
        }
    }

    // Animations
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<GeoAnimatable>(this, "controller", 0, this::predicate));
    }

    private PlayState predicate(AnimationState<GeoAnimatable> state) {
        return state.setAndContinue(RawAnimation.begin().thenLoop(hasExploded() ? "animation.detector.explode" : "animation.detector.idle"));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
