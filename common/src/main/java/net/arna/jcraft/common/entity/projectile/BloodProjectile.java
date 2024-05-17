package net.arna.jcraft.common.entity.projectile;

import net.arna.jcraft.common.component.living.CommonHitPropertyComponent;
import net.arna.jcraft.common.entity.stand.StandEntity;
import net.arna.jcraft.registry.JEntityTypeRegistry;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import mod.azure.azurelib.animatable.GeoEntity;
import mod.azure.azurelib.core.animatable.instance.AnimatableInstanceCache;
import mod.azure.azurelib.core.animation.AnimatableManager;
import mod.azure.azurelib.util.AzureLibUtil;

import static net.arna.jcraft.common.entity.stand.StandEntity.damageLogic;

public class BloodProjectile extends AbstractArrow implements GeoEntity {
    private final AnimatableInstanceCache cache = AzureLibUtil.createInstanceCache(this);

    public BloodProjectile(EntityType<? extends BloodProjectile> entityType, Level world) {
        super(entityType, world);
        this.pickup = Pickup.DISALLOWED;
    }

    public BloodProjectile(Level world, LivingEntity owner) {
        super(JEntityTypeRegistry.BLOOD_PROJECTILE.get(), owner, world);
        this.setSoundEvent(SoundEvents.SLIME_BLOCK_FALL);
        this.setOwner(owner);
    }

    @Override
    protected void tickDespawn() {
        discard();
    } // Disappear instantly upon contact with the ground

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
        if (owner.hasPassenger(entity) || entity == owner) {
            return;
        }

        if (entity instanceof LivingEntity living) {
            LivingEntity target = living;
            if (entity instanceof StandEntity<?, ?> stand && stand.hasUser()) {
                target = stand.getUserOrThrow();
            }
            damageLogic(level(), target, Vec3.ZERO, 10, 1, false, 2f,
                    false, 6, level().damageSources().thrown(this, owner), owner, CommonHitPropertyComponent.HitAnimation.MID);
            target.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 60, 0, false, true));
            discard();
        }

        if (entity instanceof EndCrystal endCrystal) {
            endCrystal.hurt(level().damageSources().thrown(this, owner), 2f);
        }

        playSound(SoundEvents.SLIME_BLOCK_FALL, 1, 0.5f);
    }

    @Override
    public ItemStack getPickupItem() {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean isNoGravity() {
        return false;
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
