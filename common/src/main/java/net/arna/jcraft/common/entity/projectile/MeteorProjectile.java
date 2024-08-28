package net.arna.jcraft.common.entity.projectile;

import mod.azure.azurelib.core.animatable.GeoAnimatable;
import mod.azure.azurelib.core.animatable.instance.AnimatableInstanceCache;
import mod.azure.azurelib.core.animation.AnimatableManager;
import mod.azure.azurelib.core.animation.AnimationController;
import mod.azure.azurelib.core.animation.AnimationState;
import mod.azure.azurelib.core.animation.RawAnimation;
import mod.azure.azurelib.core.object.PlayState;
import mod.azure.azurelib.util.AzureLibUtil;
import net.arna.jcraft.common.component.living.CommonHitPropertyComponent;
import net.arna.jcraft.common.entity.damage.JDamageSources;
import net.arna.jcraft.common.entity.stand.MagiciansRedEntity;
import net.arna.jcraft.common.entity.stand.TheSunEntity;
import net.arna.jcraft.common.util.JUtils;
import net.arna.jcraft.registry.JEntityTypeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static net.arna.jcraft.common.entity.stand.StandEntity.damageLogic;
import static net.arna.jcraft.common.util.JUtils.canDamage;

public class MeteorProjectile extends AbstractArrow implements GeoAnimatable {
    public static final EntityDataAccessor<Integer> SKIN;
    private int ticksInAir = 0;
    private int ticksInGround = 0;
    private TheSunEntity sun;
    boolean explosive = false;

    static {
        SKIN = SynchedEntityData.defineId(MeteorProjectile.class, EntityDataSerializers.INT);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(SKIN, 0);
    }

    public void assignSun(TheSunEntity sunEntity) {
        this.sun = sunEntity;
    }

    public int getSkin() {
        return entityData.get(SKIN);
    }

    public void setSkin(int skin) {
        entityData.set(SKIN, skin);
    }

    public MeteorProjectile(EntityType<? extends MeteorProjectile> entityType, Level world) {
        super(entityType, world);
    }

    public MeteorProjectile(Level world, LivingEntity owner) {
        super(JEntityTypeRegistry.METEOR.get(), owner, world);
        this.setOwner(owner);
        this.pickup = Pickup.DISALLOWED;
    }

    @Override
    public ItemStack getPickupItem() {
        return new ItemStack(Items.AIR);
    }

    @Override
    protected boolean updateInWaterStateAndDoFluidPushing() {
        return false;
    }

    @Override
    protected SoundEvent getDefaultHitGroundSoundEvent() {
        return SoundEvents.FIRECHARGE_USE;
    }

    public void setExplosive(boolean explosive) {
        this.explosive = explosive;
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        Entity owner = getOwner();
        if (owner == null) {
            return;
        }
        Entity entity = entityHitResult.getEntity();
        if (owner.hasPassenger(entity) || entity == owner || entity == sun) {
            return;
        }

        if (level().isClientSide) {
            // Hack that displays explosion without needing sync
            inGround = true;
            return;
        }

        entity.setSecondsOnFire(3);
        JUtils.projectileDamageLogic(this, level(), entity, getDeltaMovement(), 20, 1, false,
                6f, 10, CommonHitPropertyComponent.HitAnimation.HIGH);
        if (explosive && ticksInGround < 1) {
            explode();
            playSound(getHitGroundSoundEvent(), 1.0F, 1.2F / (random.nextFloat() * 0.2F + 0.9F));
            // Hack that prevents another explosion
            ticksInGround = 1;
        } else {
            discard();
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult blockHitResult) {
        if (!level().isClientSide()) {
            Direction movementDirection = getMotionDirection();
            BlockPos blockPos2 = blockPosition(); //.offset(movementDirection);
            if (BaseFireBlock.canBePlacedAt(level(), blockPos2, movementDirection)) {
                //world.playSound(null, blockPos2, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, world.getRandom().nextFloat() * 0.4F + 0.8F);
                BlockState blockState2 = BaseFireBlock.getState(level(), blockPos2);
                level().setBlock(blockPos2, blockState2, 11);
            }
            MagiciansRedEntity.ignite(level(), blockHitResult.getBlockPos());
        }
        inGround = true;
        super.onHitBlock(blockHitResult);
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

    @Override
    public void tick() {
        super.tick();

        if (level().isClientSide()) {
            Vec3 vel = getDeltaMovement();
            level().addParticle(
                    getSkin() == 2 ? ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.FLAME,
                    getX() + random.nextFloat() * 0.5f - 0.25f,
                    getY() + random.nextFloat() * 0.5f - 0.25f,
                    getZ() + random.nextFloat() * 0.5f - 0.25f,
                    vel.x / 2, vel.y / 2, vel.z / 2
            );
            if (inGround) ticksInGround++;
        } else {
            if (this.inGround) {
                if (explosive && ticksInGround == 0) {
                    explode();
                }
                this.ticksInGround++;
                if (!explosive || ticksInGround > 10) {
                    discard();
                    return;
                }
            } else {
                this.ticksInAir++;
                if (ticksInAir >= 600) {
                    discard();
                    return;
                }
            }

            if (!(getOwner() instanceof LivingEntity)) {
                discard();
                return;
            }

            TheSunEntity.dryOut((ServerLevel) level(), blockPosition());
        }
    }

    private void explode() {
        Entity owner = getOwner();
        Set<Entity> filter = new HashSet<>();
        filter.add(owner);
        filter.add(this);

        List<LivingEntity> hurtAll = new ArrayList<>(JUtils.generateHitbox(level(), position(), 2, filter));
        hurtAll.removeIf(e -> !canDamage(JDamageSources.create(level(), DamageTypes.ON_FIRE), e));

        if (!hurtAll.isEmpty()) {
            for (LivingEntity l : hurtAll) {
                LivingEntity target = JUtils.getUserIfStand(l);
                damageLogic(level(), target, l.position().subtract(position()).normalize(), 20, 3, false, 5f,
                        false, 10, JDamageSources.create(level(), DamageTypes.ON_FIRE), owner, CommonHitPropertyComponent.HitAnimation.LAUNCH);
            }
        }
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    // Animations
    private final AnimatableInstanceCache cache = AzureLibUtil.createInstanceCache(this);

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 1, this::predicate));
    }

    private static final RawAnimation explode = RawAnimation.begin().thenPlayAndHold("animation.meteor.explode");
    private static final RawAnimation idle = RawAnimation.begin()
            .thenPlay("animation.meteor.spawn")
            .thenLoop("animation.meteor.idle");
    private PlayState predicate(AnimationState<GeoAnimatable> state) {
        if (inGround) {
            if (ticksInGround == 1) {
                state.getController().setAnimation(explode);
                state.getController().forceAnimationReset();
            }
        } else if (ticksInAir == 0) {
            state.getController().setAnimation(idle);
        }
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public double getTick(Object object) {
        return this.tickCount;
    }
}
