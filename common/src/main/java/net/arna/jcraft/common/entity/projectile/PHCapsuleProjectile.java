package net.arna.jcraft.common.entity.projectile;

import net.arna.jcraft.common.component.living.CommonHitPropertyComponent;
import net.arna.jcraft.common.entity.PurpleHazeCloudEntity;
import net.arna.jcraft.common.entity.stand.AbstractPurpleHazeEntity;
import net.arna.jcraft.common.util.JUtils;
import net.arna.jcraft.registry.JEntityTypeRegistry;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import mod.azure.azurelib.core.animatable.instance.AnimatableInstanceCache;
import mod.azure.azurelib.core.animation.AnimatableManager;
import mod.azure.azurelib.util.AzureLibUtil;
import mod.azure.azurelib.core.animatable.GeoAnimatable;

public class PHCapsuleProjectile extends AbstractArrow implements GeoAnimatable {
    private AbstractPurpleHazeEntity.PoisonType poisonType;

    public PHCapsuleProjectile(Level world) {
        super(JEntityTypeRegistry.PH_CAPSULE.get(), world);
    }

    public PHCapsuleProjectile(LivingEntity owner, Level world, AbstractPurpleHazeEntity.PoisonType poisonType) {
        super(JEntityTypeRegistry.PH_CAPSULE.get(), owner, world);
        this.poisonType = poisonType;
    }

    @Override
    protected ItemStack getPickupItem() {
        return ItemStack.EMPTY;
    }

    @Override
    protected void onHit(HitResult hitResult) {
        if (level().isClientSide()) {
            return;
        }
        if (hitResult.getType() == HitResult.Type.MISS) {
            return;
        }
        if (hitResult instanceof EntityHitResult entityHitResult) {
            if (getOwner() != null && entityHitResult.getEntity().isPassengerOfSameVehicle(getOwner())) {
                return;
            }

            JUtils.projectileDamageLogic(this, level(), entityHitResult.getEntity(), getDeltaMovement().scale(0.1),
                    2, 1, false, 2f, 2, CommonHitPropertyComponent.HitAnimation.MID);
        }

        discard();
        PurpleHazeCloudEntity cloud = new PurpleHazeCloudEntity(level(), 2.0f, poisonType);
        cloud.copyPosition(this);
        level().addFreshEntity(cloud);
    }

    // Animations
    private final AnimatableInstanceCache cache = AzureLibUtil.createInstanceCache(this);

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public double getTick(Object object) {
        return 0;
    }
}
