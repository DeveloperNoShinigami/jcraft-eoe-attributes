package net.arna.jcraft.common.entity.projectile;

import mod.azure.azurelib.animatable.GeoEntity;
import mod.azure.azurelib.core.animatable.instance.AnimatableInstanceCache;
import mod.azure.azurelib.core.animation.AnimatableManager;
import mod.azure.azurelib.core.animation.AnimationController;
import mod.azure.azurelib.core.animation.AnimationState;
import mod.azure.azurelib.core.animation.RawAnimation;
import mod.azure.azurelib.core.object.PlayState;
import mod.azure.azurelib.util.AzureLibUtil;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.component.living.CommonHitPropertyComponent;
import net.arna.jcraft.common.entity.stand.StandEntity;
import net.arna.jcraft.common.util.JParticleType;
import net.arna.jcraft.common.util.JUtils;
import net.arna.jcraft.platform.JComponentPlatformUtils;
import net.arna.jcraft.registry.JEntityTypeRegistry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;
import java.util.Optional;
import java.util.Set;

public class IceBranchProjectile extends AbstractArrow implements GeoEntity {
    private static final int MAX_CHAIN_LENGTH = 16;
    private final int chainIndex;

    private IceBranchProjectile next;
    private LivingEntity livingOwner;

    private boolean grown = false;
    private boolean lockRotation = false;

    public IceBranchProjectile(Level level) {
        super(JEntityTypeRegistry.ICE_BRANCH.get(), level);
        chainIndex = 0;
    }
    public IceBranchProjectile(Level level, LivingEntity owner, int chainIndex) {
        super(JEntityTypeRegistry.ICE_BRANCH.get(), level);
        setOwner(owner);
        livingOwner = owner;
        setNoGravity(true);
        setNoPhysics(true);
        this.chainIndex = chainIndex;
        this.pickup = Pickup.DISALLOWED;
    }

    @Override
    public void setXRot(float xRot) {
        if (lockRotation) return;
        super.setXRot(xRot);
    }

    @Override
    public void setYRot(float yRot) {
        if (lockRotation) return;
        super.setYRot(yRot);
    }

    private final Comparator<Entity> distanceComparator = (entity1, entity2) -> {
        double distance1 = this.distanceToSqr(entity1);
        double distance2 = this.distanceToSqr(entity2);
        return Double.compare(distance1, distance2);
    };

    //todo: variants + less noclip
    public static final double LENGTH = 1;
    @Override
    public void tick() {
        lockRotation = true;
        super.tick();
        lockRotation = false;
        if (level().isClientSide()) {
            if (tickCount == 1) {
                for (int i = 0; i < 6; i++) {
                    level().addParticle(random.nextBoolean() ? LargeIcicleProjectile.ICE_PARTICLE : ParticleTypes.SNOWFLAKE,
                            getX(), getY(), getZ(),
                            random.nextGaussian() * 0.25,
                            random.nextGaussian() * 0.25,
                            random.nextGaussian() * 0.25
                    );
                }
            } else if (random.nextFloat() < 0.1f) {
                level().addParticle(random.nextBoolean() ? ParticleTypes.SPIT : ParticleTypes.SNOWFLAKE,
                        getX() + random.nextGaussian() * 0.25,
                        getY() + random.nextGaussian() * 0.25,
                        getZ() + random.nextGaussian() * 0.25,
                        0, 0, 0
                );
            }
            return;
        }
        if (livingOwner == null) {
            discard();
            return;
        }
        if (tickCount == 1) {
            Vec3 rotVec = calculateViewVector(getXRot(), -getYRot()); // Noclipping projectiles have inverted yaw
            Vec3 pos = position();
            Set<LivingEntity> hurt = JUtils.generateHitbox(level(), pos.add(rotVec.scale(-0.25)), 1.25, e -> true);
            boolean hit = false;
            for (LivingEntity living : hurt) {
                if (!canAttack(living)) continue;
                hit = !JUtils.isBlocking(living);

                LivingEntity target = JUtils.getUserIfStand(living);

                StandEntity.damageLogic(level(), target, Vec3.ZERO,
                        30, 0, false, 3f, true,
                        10, level().damageSources().mobAttack(livingOwner), livingOwner,
                        CommonHitPropertyComponent.HitAnimation.MID);
            }
            if (hit) {
                Vec3 frontPos = pos.add(rotVec.scale(-0.5));
                JCraft.createParticle((ServerLevel) level(),
                        frontPos.x + random.nextGaussian() * 0.25,
                        frontPos.y + random.nextGaussian() * 0.25,
                        frontPos.z + random.nextGaussian() * 0.25,
                        JParticleType.HIT_SPARK_1);
                playSound(SoundEvents.PLAYER_HURT_FREEZE, 1, 1);

                grown = true; // Stop growth
            }
        } else if (chainIndex < MAX_CHAIN_LENGTH && !grown && tickCount == 15) {
            ServerLevel serverWorld = (ServerLevel) level();
            Vec3 rotVec = calculateViewVector(getXRot(), -getYRot()); // Noclipping projectiles have inverted yaw
            next = new IceBranchProjectile(level(), livingOwner, chainIndex + 1);
            Vec3 initialPos = position().add(rotVec.scale(-LENGTH));

            Optional<LivingEntity> target = serverWorld.getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(32.0), EntitySelector.NO_CREATIVE_OR_SPECTATOR)
                    .stream()
                    .filter(livingEntity -> livingEntity != livingOwner && !livingEntity.isPassengerOfSameVehicle(livingOwner))
                    .min(distanceComparator);

            if (target.isPresent()) {
                LivingEntity nearestTarget = target.get();
                Vec3 toTarget = nearestTarget.position()
                        .subtract(initialPos)
                        .add(
                                random.nextGaussian() * 0.3,
                                random.nextGaussian() * 0.3,
                                random.nextGaussian() * 0.3
                        ).normalize();
                double e = toTarget.x;
                double f = toTarget.y;
                double g = toTarget.z;
                double l = toTarget.horizontalDistance();
                next.setYRot((float) (Mth.atan2(-e, -g) * 57.2957763671875));
                next.setXRot((float) (Mth.atan2(f, l) * 57.2957763671875));
                next.setPos(initialPos.add(toTarget.scale(LENGTH)));
            } else {
                next.setXRot(getXRot() + random.nextFloat() * 30);
                next.setYRot(getXRot() + random.nextFloat() * 30);
                next.setPos(initialPos.add(next.getLookAngle().scale(LENGTH)));
            }

            next.xRotO = next.getXRot();
            next.yRotO = next.getYRot();
            level().addFreshEntity(next);
            grown = true;
        } else if (tickCount == 100) {
            discard();
        }
    }

    private boolean canAttack(LivingEntity living) {
        if (living == livingOwner)
            return false;
        if (livingOwner != null && JComponentPlatformUtils.getStandData(livingOwner).getStand() == living)
            return false;
        return true;
    }

    @Override
    protected void onHit(HitResult result) { }

    @Override
    protected ItemStack getPickupItem() {
        return ItemStack.EMPTY;
    }

    // Animations
    private final AnimatableInstanceCache cache = AzureLibUtil.createInstanceCache(this);
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    private static final RawAnimation FIRE = RawAnimation.begin().thenPlayAndHold("animation.ice_branch.spawn");
    private PlayState predicate(AnimationState<IceBranchProjectile> state) {
        return state.setAndContinue(FIRE);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
