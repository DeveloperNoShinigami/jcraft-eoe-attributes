package net.arna.jcraft.common.entity.projectile;

import mod.azure.azurelib.animatable.GeoEntity;
import mod.azure.azurelib.core.animatable.instance.AnimatableInstanceCache;
import mod.azure.azurelib.core.animation.AnimatableManager;
import mod.azure.azurelib.core.animation.AnimationController;
import mod.azure.azurelib.core.animation.AnimationState;
import mod.azure.azurelib.core.animation.RawAnimation;
import mod.azure.azurelib.core.object.PlayState;
import mod.azure.azurelib.util.AzureLibUtil;
import net.arna.jcraft.registry.JEntityTypeRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;


public class BubbleProjectile extends AbstractArrow implements GeoEntity {
    public BubbleProjectile(final Level world) {
        super(JEntityTypeRegistry.BUBBLE.get(), world);
        this.pickup = Pickup.DISALLOWED;
    }

    public BubbleProjectile(final Level world, final LivingEntity owner) {
        super(JEntityTypeRegistry.BUBBLE.get(), owner, world);
        this.setOwner(owner);
    }

    @Override
    public @NotNull ItemStack getPickupItem() {
        return new ItemStack(Items.AIR);
    }

    @Override
    protected void onHit(final HitResult hitResult) {
        final HitResult.Type type = hitResult.getType();
        if (type == HitResult.Type.BLOCK) {
            this.onHitBlock((BlockHitResult) hitResult);
            this.gameEvent(GameEvent.PROJECTILE_LAND, getOwner());
            this.discard();
        }
    }

    public boolean isInGround() {
        return inGround;
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide()) {
            if (getOwner() == null || tickCount > 1600) {
                discard();
            }
        }
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean shouldRenderAtSqrDistance(final double distance) {
        return true;
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    @Override
    protected @NotNull SoundEvent getDefaultHitGroundSoundEvent() {
        return SoundEvents.BUBBLE_COLUMN_BUBBLE_POP;
    }

    // Animations
    private final AnimatableInstanceCache cache = AzureLibUtil.createInstanceCache(this);

    @Override
    public void registerControllers(final AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("animation.bubble.idle");
    private PlayState predicate(final AnimationState<BubbleProjectile> state) {
        return state.setAndContinue(IDLE);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
