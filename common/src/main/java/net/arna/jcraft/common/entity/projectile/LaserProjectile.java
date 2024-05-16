package net.arna.jcraft.common.entity.projectile;

import lombok.Setter;
import net.arna.jcraft.common.component.living.CommonHitPropertyComponent;
import net.arna.jcraft.common.util.JUtils;
import net.arna.jcraft.registry.JEntityTypeRegistry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;


import java.util.ArrayList;
import java.util.List;
import mod.azure.azurelib.animatable.GeoEntity;
import mod.azure.azurelib.core.animatable.instance.AnimatableInstanceCache;
import mod.azure.azurelib.core.animation.AnimatableManager;
import mod.azure.azurelib.core.animation.AnimationController;
import mod.azure.azurelib.core.animation.AnimationState;
import mod.azure.azurelib.core.animation.RawAnimation;
import mod.azure.azurelib.core.object.PlayState;
import mod.azure.azurelib.util.AzureLibUtil;
import mod.azure.azurelib.core.animatable.GeoAnimatable;

public class LaserProjectile extends AbstractArrow implements GeoEntity {
    private final AnimatableInstanceCache cache = AzureLibUtil.createInstanceCache(this);
    private int lifetime = 60;
    private final List<Entity> hit = new ArrayList<>();
    @Setter
    private boolean unblockable = false;

    public LaserProjectile(EntityType<? extends LaserProjectile> entityType, Level world) {
        super(entityType, world);
        this.pickup = Pickup.DISALLOWED;
    }

    public LaserProjectile(Level world, LivingEntity owner) {
        super(JEntityTypeRegistry.LASER_PROJECTILE.get(), owner, world);
        this.setOwner(owner);
    }

    @Override
    protected void tickDespawn() {
        discard();
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide()) {
            double x = getX(), y = getY(), z = getZ();
            Vec3 vel = getDeltaMovement();

            if (tickCount == 1) {
                for (int i = 0; i < 20; i++) {
                    level().addParticle(
                            ParticleTypes.FIREWORK,
                            x, y, z
                            , (vel.x + random.nextGaussian() * 0.5) * 0.2
                            , (vel.y + random.nextGaussian() * 0.5) * 0.2
                            , (vel.z + random.nextGaussian() * 0.5) * 0.2
                    );
                }
                for (int i = 0; i < 10; i++) {
                    Vec3 frontVel = vel.scale(random.nextDouble());
                    level().addParticle(
                            ParticleTypes.FIREWORK,
                            x, y, z
                            , frontVel.x
                            , frontVel.y
                            , frontVel.z
                    );
                }
            } else {
                level().addParticle(
                        ParticleTypes.WITCH,
                        x, y, z,
                        vel.x / 2, vel.y / 2, vel.z / 2
                );
            }
        } else if (--lifetime < 1) {
            discard();
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        if (level().isClientSide) {
            return;
        }
        Entity owner = getOwner();
        if (owner == null) {
            return;
        }
        Entity entity = entityHitResult.getEntity();
        if (owner.hasPassenger(entity) || entity == owner || hit.contains(entity)) {
            return;
        }

        JUtils.projectileDamageLogic(this, level(), entity, getDeltaMovement(), 20, 1, false,
                5f, 0, CommonHitPropertyComponent.HitAnimation.CRUSH, unblockable, false);
        hit.add(entity);
    }

    @Override
    protected float getWaterInertia() {
        // Not actually drag, just a multiplier
        return 1.0F;
    }

    @Override
    public ItemStack getPickupItem() {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    // Animations

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
