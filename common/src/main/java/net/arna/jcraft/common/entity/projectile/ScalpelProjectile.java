package net.arna.jcraft.common.entity.projectile;

import mod.azure.azurelib.animatable.GeoEntity;
import mod.azure.azurelib.core.animatable.instance.AnimatableInstanceCache;
import mod.azure.azurelib.core.animation.AnimatableManager;
import mod.azure.azurelib.util.AzureLibUtil;
import net.arna.jcraft.common.component.living.CommonHitPropertyComponent;
import net.arna.jcraft.common.entity.stand.MetallicaEntity;
import net.arna.jcraft.common.util.JUtils;
import net.arna.jcraft.platform.JComponentPlatformUtils;
import net.arna.jcraft.registry.JEntityTypeRegistry;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class ScalpelProjectile extends AbstractArrow implements GeoEntity {
    public static final float IRON_COST = 6.0f;

    public ScalpelProjectile(Level world) {
        super(JEntityTypeRegistry.SCALPEL.get(), world);
    }

    public ScalpelProjectile(Level world, LivingEntity owner) {
        super(JEntityTypeRegistry.SCALPEL.get(), owner, world);
    }

    public static ScalpelProjectile fromMetallica(MetallicaEntity metallica) {
        if (metallica.drainIron(IRON_COST)) {
            return new ScalpelProjectile(metallica.level(), metallica.getUserOrThrow());
        }
        return null;
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        if (level().isClientSide) return;
        Entity entity = entityHitResult.getEntity();
        Entity owner = this.getOwner();

        if (owner != null && owner.hasPassenger(entity) || entity == owner) {
            return;
        }

        if (isOnFire()) entity.setSecondsOnFire(5);

        final int blockstun = 4;
        final int stunT = 10;

        JUtils.projectileDamageLogic(this, level(), entity, Vec3.ZERO, stunT, 1, false, 2, blockstun, CommonHitPropertyComponent.HitAnimation.MID);
        playSound(SoundEvents.TRIDENT_HIT, 1, 1);
        // if (entity instanceof LivingEntity living) JComponentPlatformUtils.getMiscData(living).stab();
        // discard();
        setDeltaMovement(getDeltaMovement().scale(0.5));
        hurtMarked = true;
    }

    @Override
    protected boolean tryPickup(Player player) {
        if (JComponentPlatformUtils.getStandData(player).getStand() instanceof MetallicaEntity metallica) {
            if (metallica.getIron() < MetallicaEntity.IRON_MAX) {
                metallica.addIron(IRON_COST);
                return true;
            }
        }
        return super.tryPickup(player);
    }

    @Override
    protected @NotNull ItemStack getPickupItem() {
        return ItemStack.EMPTY;
    }

    // Animations
    private final AnimatableInstanceCache cache = AzureLibUtil.createInstanceCache(this);
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) { }
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }
}
