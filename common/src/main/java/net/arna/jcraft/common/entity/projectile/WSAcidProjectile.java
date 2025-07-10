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
import net.arna.jcraft.common.splatter.SplatterType;
import net.arna.jcraft.common.util.JUtils;
import net.arna.jcraft.api.registry.JEntityTypeRegistry;
import net.arna.jcraft.api.registry.JStatusRegistry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import static net.arna.jcraft.api.Attacks.damageLogic;

public class WSAcidProjectile extends AbstractArrow implements GeoEntity {
    private static final EntityDataAccessor<Boolean> MYH; // Melt your Heart variant

    static {
        MYH = SynchedEntityData.defineId(WSAcidProjectile.class, EntityDataSerializers.BOOLEAN);
    }

    public WSAcidProjectile(Level world) {
        super(JEntityTypeRegistry.WS_ACID_PROJECTILE.get(), world);
    }

    public WSAcidProjectile(Level world, LivingEntity owner) {
        super(JEntityTypeRegistry.WS_ACID_PROJECTILE.get(), owner, world);
        setSoundEvent(SoundEvents.SLIME_BLOCK_FALL);
        pickup = Pickup.DISALLOWED;
        noCulling = true;
    }

    public void markMeltYourHeart() {
        entityData.set(MYH, true);
    }

    private void splat() {
        JUtils.getSplatterManager(level()).addSplatter(position(), SplatterType.ACID, 1, getOwner());
        discard();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(MYH, false);
    }

    @Override
    protected void onHitEntity(@NonNull EntityHitResult entityHitResult) {
        if (level().isClientSide) {
            return;
        }

        final Entity owner = getOwner();
        if (owner == null) {
            return;
        }

        if (entityData.get(MYH)) {
            return; // Melt your Heart variants of this phase through entities
        }

        final Entity entity = entityHitResult.getEntity();
        if (owner.hasPassenger(entity) || entity == owner) {
            return;
        }

        if (entity instanceof LivingEntity living) {
            LivingEntity target = living;
            if (entity instanceof StandEntity<?, ?> stand && stand.hasUser()) {
                target = stand.getUserOrThrow();
            }
            damageLogic(level(), target, Vec3.ZERO, 10, 1, false, 5f, false, 6,
                    level().damageSources().thrown(this, owner), owner, CommonHitPropertyComponent.HitAnimation.MID);
            target.addEffect(new MobEffectInstance(JStatusRegistry.WSPOISON.get(), 60, 0, false, true));
            discard();
        }

        if (entity instanceof EndCrystal endCrystal) {
            endCrystal.hurt(level().damageSources().thrown(this, owner), 2f);
        }

        playSound(SoundEvents.BUCKET_EMPTY, 1, 0.5f);
    }

    private int timeOnSurface = 0;

    @Override
    protected void tickDespawn() {
        super.tickDespawn();
        if (level().isClientSide) {
            return;
        }
        if (timeOnSurface++ >= 100) {
            discard();
        }
        splat();
    }

    @Override
    public void tick() {
        final Entity owner = getOwner();
        if (owner == null) {
            if (!level().isClientSide()) {
                discard();
            }
            return;
        }

        // Display spit effects
        if (firstTick) {
            double x = getX();
            double y = getY();
            double z = getZ();
            for (int h = 0; h < 128; ++h) {
                double pX = x + random.nextDouble() * 2 - 1;
                double pY = y + random.nextDouble() * 2 - 1;
                double pZ = z + random.nextDouble() * 2 - 1;
                final Vec3 awayVector = getForward().scale(0.3);

                level().addParticle(
                        ParticleTypes.SPIT,
                        pX, pY, pZ,
                        -awayVector.x, -awayVector.y, awayVector.z);
            }
        }

        super.tick();

        if (!inGround) {
            final Vec3 vel = getDeltaMovement();
            level().addParticle(
                    ParticleTypes.SPIT,
                    getX(), getY(), getZ(),
                    vel.x, vel.y, vel.z);
        }
    }

    @Override
    public @NonNull ItemStack getPickupItem() {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean isNoGravity() {
        return false;
    }

    // Animations
    private final AnimatableInstanceCache cache = AzureLibUtil.createInstanceCache(this);

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    private static final RawAnimation MELT_IDLE = RawAnimation.begin().thenLoop("animation.wsacid.meltidle");
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("animation.wsacid.idle");
    private PlayState predicate(AnimationState<GeoAnimatable> state) {
        return state.setAndContinue(entityData.get(MYH) ? MELT_IDLE : IDLE);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
