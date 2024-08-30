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
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Set;

public class LargeIcicleProjectile extends AbstractArrow implements GeoEntity {
    private int ticksInAir;
    private final LivingEntity livingOwner;

    public LargeIcicleProjectile(EntityType<? extends LargeIcicleProjectile> entityType, Level world) {
        super(entityType, world);
        livingOwner = null;
    }

    public LargeIcicleProjectile(Level world) {
        super(JEntityTypeRegistry.LARGE_ICICLE.get(), world);
        livingOwner = null;
    }

    public LargeIcicleProjectile(Level world, LivingEntity owner) {
        super(JEntityTypeRegistry.LARGE_ICICLE.get(), owner, world);
        // setNoGravity(true);
        setNoPhysics(true);
        setOwner(owner);
        this.pickup = Pickup.DISALLOWED;
        livingOwner = owner;
        setSoundEvent(SoundEvents.GLASS_BREAK);
    }

    @Override
    public ItemStack getPickupItem() {
        return ItemStack.EMPTY;
    }

    private static final BlockParticleOption ICE_PARTICLE = new BlockParticleOption(ParticleTypes.BLOCK, Blocks.ICE.defaultBlockState());

    @Override
    public void tick() {
        super.tick();

        if (ticksInAir++ == 0 && level().isClientSide) {
            double x = getX();
            double y = getY();
            double z = getZ();
            Vec3 velocity = getDeltaMovement().normalize();

            for (int i = 0; i < 24; i++) {
                level().addParticle(random.nextBoolean() ? ICE_PARTICLE : ParticleTypes.SNOWFLAKE, x, y, z,
                        (velocity.x + random.nextGaussian()) * 0.25,
                        (velocity.y + random.nextGaussian()) * 0.25,
                        (velocity.z + random.nextGaussian()) * 0.25
                );
            }
            return;
        }

        if (ticksInAir < 10) {
            setDeltaMovement(getDeltaMovement().scale(0.9));
        } else if (ticksInAir == 10) {
            if (livingOwner != null) {
                Vec3 pos = position();
                Vec3 direction = getDeltaMovement().normalize();
                Set<LivingEntity> hurt = JUtils.generateHitbox(level(), pos.add(direction.scale(1.25)), 1.75, e -> true);
                hurt.addAll(JUtils.generateHitbox(level(), pos.add(direction.scale(2.25)), 1.25, e -> true));
                boolean hit = false;
                for (LivingEntity living : hurt) {
                    if (!canAttack(living)) {
                        continue;
                    }
                    hit = true;
                    LivingEntity target = JUtils.getUserIfStand(living);
                    Vec3 kbVec = direction.scale(0.75);
                    StandEntity.damageLogic(level(), target, kbVec, 15, 3, false, 7f, true, 13,
                            level().damageSources().mobAttack(livingOwner), livingOwner, CommonHitPropertyComponent.HitAnimation.CRUSH, false);
                }
                if (hit) {
                    JCraft.createParticle((ServerLevel) level(),
                            pos.x + direction.x * 2.5 + random.nextGaussian() * 0.25,
                            pos.y + direction.y * 2.5 + random.nextGaussian() * 0.25,
                            pos.z + direction.z * 2.5 + random.nextGaussian() * 0.25,
                            JParticleType.HIT_SPARK_2);
                }
                playSound(SoundEvents.GLASS_BREAK, 1, 1);
            }
        } else if (ticksInAir > 50) {
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
    protected void onHit(HitResult hitResult) { }

    @Override
    protected float getWaterInertia() { // Not actually drag, just a multiplier
        return 1.0F;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putShort("life", (short) this.ticksInAir);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.ticksInAir = tag.getShort("life");
    }

    // Animations
    private final AnimatableInstanceCache cache = AzureLibUtil.createInstanceCache(this);
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    private static final RawAnimation FIRE = RawAnimation.begin().thenPlayAndHold("animation.large_icicle.spawn");
    private PlayState predicate(AnimationState<LargeIcicleProjectile> state) {
        return state.setAndContinue(FIRE);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
