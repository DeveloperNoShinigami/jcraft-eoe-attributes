package net.arna.jcraft.common.entity.projectile;

import net.arna.jcraft.common.component.living.CommonHitPropertyComponent;
import net.arna.jcraft.common.entity.stand.StandEntity;
import net.arna.jcraft.common.util.JUtils;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import mod.azure.azurelib.animatable.GeoEntity;
import mod.azure.azurelib.core.animatable.instance.AnimatableInstanceCache;
import mod.azure.azurelib.core.animation.AnimatableManager;
import mod.azure.azurelib.core.animation.AnimationController;
import mod.azure.azurelib.core.animation.AnimationState;
import mod.azure.azurelib.core.animation.RawAnimation;
import mod.azure.azurelib.core.object.PlayState;
import mod.azure.azurelib.util.AzureLibUtil;
import mod.azure.azurelib.core.animatable.GeoAnimatable;
import java.util.Set;

public class GETreeEntity extends JAttackEntity implements GeoEntity {
    private final Vec3 launchVec;

    public GETreeEntity(EntityType<? extends LivingEntity> type, Level world) {
        this(type, world, Vec3.ZERO);
    }

    public GETreeEntity(EntityType<? extends LivingEntity> type, Level world, Vec3 launchVec) {
        super(type, world);
        this.setInvulnerable(true);
        this.launchVec = launchVec;
    }

    @Override
    public void tick() {
        super.tick();
        if (tickCount > 120) {
            discard();
        }

        if (level().isClientSide || master == null) {
            return;
        }

        if (tickCount == 4) {
            DamageSource ds = level().damageSources().mobAttack(master);
            Set<LivingEntity> hurt = JUtils.generateHitbox(level(), position().add(launchVec.normalize()), 2.5, Set.of(this, master));

            for (LivingEntity living : hurt) {
                if (!JUtils.canDamage(ds, living)) {
                    continue;
                }

                LivingEntity target = JUtils.getUserIfStand(living);
                if (master != target) {
                    StandEntity.damageLogic(level(), target, Vec3.ZERO, 25, 3,
                            false, 7f, false, 11, ds, master, CommonHitPropertyComponent.HitAnimation.MID, false);
                }
                JUtils.addVelocity(target, launchVec.x, launchVec.y, launchVec.z);
            }
        }
    }

    @Override
    public boolean startRiding(Entity entity, boolean force) {
        return false;
    }

    // Animations
    private final AnimatableInstanceCache cache = AzureLibUtil.createInstanceCache(this);

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<GeoAnimatable>(this, "controller", 0, this::predicate));
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
