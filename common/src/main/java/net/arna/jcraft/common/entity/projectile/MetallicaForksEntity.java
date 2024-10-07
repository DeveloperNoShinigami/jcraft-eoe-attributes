package net.arna.jcraft.common.entity.projectile;

import lombok.NonNull;
import mod.azure.azurelib.animatable.GeoEntity;
import mod.azure.azurelib.core.animatable.instance.AnimatableInstanceCache;
import mod.azure.azurelib.core.animation.AnimatableManager;
import mod.azure.azurelib.core.animation.AnimationController;
import mod.azure.azurelib.core.animation.AnimationState;
import mod.azure.azurelib.core.animation.RawAnimation;
import mod.azure.azurelib.core.object.PlayState;
import mod.azure.azurelib.util.AzureLibUtil;
import net.arna.jcraft.common.component.living.CommonHitPropertyComponent;
import net.arna.jcraft.common.entity.stand.MetallicaEntity;
import net.arna.jcraft.common.entity.stand.StandEntity;
import net.arna.jcraft.common.util.JUtils;
import net.arna.jcraft.registry.JEntityTypeRegistry;
import net.arna.jcraft.registry.JStatusRegistry;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class MetallicaForksEntity extends JAttackEntity implements GeoEntity {
    public static final float IRON_COST = 20.0f;

    public MetallicaForksEntity(Level world) {
        super(JEntityTypeRegistry.METALLICA_FORKS.get(), world);
    }

    public MetallicaForksEntity(Level world, LivingEntity owner) {
        super(JEntityTypeRegistry.METALLICA_FORKS.get(), world);
        setMaster(owner);
    }

    public static MetallicaForksEntity fromMetallica(MetallicaEntity metallica) {
        if (metallica.drainIron(IRON_COST)) {
            return new MetallicaForksEntity(metallica.level(), metallica.getUserOrThrow());
        }
        return null;
    }

    private static final ParticleOptions IRON_PARTICLE = new BlockParticleOption(ParticleTypes.BLOCK, Blocks.IRON_BARS.defaultBlockState());
    @Override
    public void tick() {
        super.tick();

        if (level().isClientSide()) {
            if (tickCount == 200) {
                final double x = getX(), y = getY(), z = getZ();
                for (int i = 0; i < 16; i++) {
                    level().addParticle(
                            IRON_PARTICLE, false,
                            x + random.nextGaussian(),
                            y + random.nextGaussian(),
                            z + random.nextGaussian(),
                            random.nextFloat() * 0.5f,
                            random.nextFloat() * 0.5f,
                            random.nextFloat() * 0.5f
                    );
                }
            }
            return;
        }

        if (master == null) discard();

        if (tickCount == 9) {
            final DamageSource ds = level().damageSources().mobAttack(master);
            final Set<LivingEntity> hurt = JUtils.generateHitbox(level(), position(), 2, Set.of(this, master));

            for (LivingEntity living : hurt) {
                if (!JUtils.canDamage(ds, living)) {
                    continue;
                }

                final LivingEntity target = JUtils.getUserIfStand(living);
                if (master != target) {
                    StandEntity.damageLogic(level(), target, Vec3.ZERO, 10, 0,
                            false, 4f, false, 10, ds, master, CommonHitPropertyComponent.HitAnimation.MID, false);
                }
                target.addEffect(
                        new MobEffectInstance(JStatusRegistry.KNOCKDOWN.get(), 45, 0, true, false)
                );
            }
        } else if (tickCount == 200) {
            discard();
        }
    }

    @Override
    protected void doPush(@NonNull Entity entity) {
    }

    @Override
    public void push(@NonNull Entity entity) {
    }

    @Override
    public boolean canCollideWith(@NonNull Entity other) {
        return false;
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    protected @NonNull AABB makeBoundingBox() { // Centered around 0,0,0 instead of 0,0.5,0
        final double x = getX(), y = getY(), z = getZ();
        final double s = 0.5;
        return new AABB(x + s, y + s, z + s, x - s, y - s, z - s);
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(@NonNull DamageSource source) {
        return SoundEvents.METAL_HIT;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.METAL_BREAK;
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    // Animations
    private final AnimatableInstanceCache cache = AzureLibUtil.createInstanceCache(this);

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, this::predicate));
        controllers.add(new AnimationController<>(this, "air_anim", 0, this::airAnim));
    }

    private static final RawAnimation SPAWN = RawAnimation.begin().thenPlayAndHold("animation.forks.spawn");
    private static final RawAnimation AIR_MOD = RawAnimation.begin().thenPlayAndHold("animation.forks.air_mod");
    private PlayState predicate(AnimationState<MetallicaForksEntity> state) {
        return state.setAndContinue(SPAWN);
    }
    private PlayState airAnim(AnimationState<MetallicaForksEntity> state) {
        if (onGround()) return PlayState.STOP;
        return state.setAndContinue(AIR_MOD);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
