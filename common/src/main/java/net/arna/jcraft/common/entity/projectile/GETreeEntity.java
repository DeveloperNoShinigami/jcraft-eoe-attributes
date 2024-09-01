package net.arna.jcraft.common.entity.projectile;

import mod.azure.azurelib.animatable.GeoEntity;
import mod.azure.azurelib.core.animatable.GeoAnimatable;
import mod.azure.azurelib.core.animatable.instance.AnimatableInstanceCache;
import mod.azure.azurelib.core.animation.AnimatableManager;
import mod.azure.azurelib.core.animation.AnimationController;
import mod.azure.azurelib.core.animation.AnimationState;
import mod.azure.azurelib.core.animation.RawAnimation;
import mod.azure.azurelib.core.object.PlayState;
import mod.azure.azurelib.util.AzureLibUtil;
import net.arna.jcraft.common.component.living.CommonHitPropertyComponent;
import net.arna.jcraft.common.entity.stand.StandEntity;
import net.arna.jcraft.common.util.JUtils;
import net.arna.jcraft.registry.JEntityTypeRegistry;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Set;

public class GETreeEntity extends AbstractArrow implements GeoEntity {
    private final Vec3 launchVec;
    private LivingEntity livingOwner;

    public GETreeEntity(Level world) {
        this(world, null, Vec3.ZERO);
    }

    public GETreeEntity(Level world, LivingEntity owner, Vec3 launchVec) {
        super(JEntityTypeRegistry.GE_TREE.get(), world);
        this.setOwner(owner);
        this.livingOwner = owner;
        this.pickup = Pickup.DISALLOWED;
        this.setInvulnerable(true);
        this.launchVec = launchVec;
    }

    private boolean lockRotation = false;
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

    @Override
    public void tick() {
        lockRotation = true;
        super.tick();
        lockRotation = false;
        if (tickCount > 120) {
            discard();
        }

        if (level().isClientSide || livingOwner == null) {
            return;
        }

        if (tickCount == 4) {
            DamageSource ds = level().damageSources().mobAttack(livingOwner);
            Set<LivingEntity> hurt = JUtils.generateHitbox(level(), position().add(launchVec.normalize()), 2.5, Set.of(this, livingOwner));

            for (LivingEntity living : hurt) {
                if (!JUtils.canDamage(ds, living)) {
                    continue;
                }

                LivingEntity target = JUtils.getUserIfStand(living);
                if (livingOwner != target) {
                    StandEntity.damageLogic(level(), target, Vec3.ZERO, 25, 3,
                            false, 7f, false, 11, ds, livingOwner, CommonHitPropertyComponent.HitAnimation.MID, false);
                }
                JUtils.addVelocity(target, launchVec.x, launchVec.y, launchVec.z);
            }
        }
    }

    @Override
    protected ItemStack getPickupItem() {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean startRiding(Entity entity, boolean force) {
        return false;
    }

    // Animations
    private final AnimatableInstanceCache cache = AzureLibUtil.createInstanceCache(this);

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    private PlayState predicate(AnimationState<GeoAnimatable> state) {
        if (state.getController().getCurrentAnimation() == null) {
            state.setAnimation(
                    RawAnimation.begin()
                            .thenPlay("animation.getree.spawn")
                            .thenPlay("animation.getree.idle")
                            .thenPlay("animation.getree.return")
            );
        }
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
