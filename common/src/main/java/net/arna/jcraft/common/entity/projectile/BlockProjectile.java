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
import net.arna.jcraft.registry.JSoundRegistry;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Set;

/**
 * Used in C-Moon's {@link net.arna.jcraft.common.attack.moves.cmoon.LaunchAttack}
 */
public class BlockProjectile extends JAttackEntity implements GeoEntity {
    private final int maxTimeToLaunch = 15;
    private int timeToLaunch = maxTimeToLaunch;
    private int timeLaunched = 0;
    private boolean toRefresh = false;
    private boolean launched = false;
    private boolean hit = false;

    private static final EntityDataAccessor<Byte> EFFECT;
    private static final EntityDataAccessor<ItemStack> BLOCKSTACK;

    static {
        EFFECT = SynchedEntityData.defineId(BlockProjectile.class, EntityDataSerializers.BYTE);
        BLOCKSTACK = SynchedEntityData.defineId(BlockProjectile.class, EntityDataSerializers.ITEM_STACK);
    }

    public BlockProjectile(Level world) {
        super(JEntityTypeRegistry.BLOCK_PROJECTILE.get(), world);
        setNoGravity(true);
        supportsItems = true;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(EFFECT, (byte)0);
        entityData.define(BLOCKSTACK, Items.STONE.getDefaultInstance());
    }

    public void setBlockStack(ItemStack stack) {
        entityData.set(BLOCKSTACK, stack);
    }

    /**
     * 0 - NONE
     * 1 - BREAK
     * 2 - HALT
     */
    public void setEffect(byte effect) {
        entityData.set(EFFECT, effect);
    }

    public void markRefresh() {
        toRefresh = true;
    }

    private void breakBlock() {
        setPos(position().add(getDeltaMovement()));
        setDeltaMovement(0, 0, 0);
        setEffect((byte)1);
        setDiscardFriction(false);
        kill();
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) {
            Vec3 vel = getDeltaMovement();
            final int effect = entityData.get(EFFECT);
            if (effect != 0) {
                for (int i = 0; i < 32; i++) {
                    ParticleOptions particle = (effect == 1) ?
                            new BlockParticleOption(ParticleTypes.BLOCK, Block.byItem(entityData.get(BLOCKSTACK).getItem()).defaultBlockState()) :
                            ParticleTypes.REVERSE_PORTAL;
                    level().addParticle(
                            particle,
                            getX() + vel.x + random.nextDouble() - 0.5,
                            getY() + vel.y + random.nextDouble() - 0.5,
                            getZ() + vel.z + random.nextDouble() - 0.5,
                            vel.x + random.nextDouble() * 2 - 1,
                            vel.y + random.nextDouble() * 2 - 1,
                            vel.z + random.nextDouble() * 2 - 1
                    );
                }
            }
            level().addParticle(ParticleTypes.REVERSE_PORTAL,
                    getX() + random.nextDouble() - 0.5,
                    getY() + random.nextDouble() - 0.5,
                    getZ() + random.nextDouble() - 0.5,
                    vel.x / 2,
                    vel.y / 2,
                    vel.z / 2
            );
        } else {
            if (master == null || deathTime > 1) {
                discard();
                return;
            }

            if (entityData.get(EFFECT) != 0) {
                setEffect((byte)0);
            }

            if (hit || onGround() || tickCount > 200) { // Placing this here delays it by 1 tick, allowing the client to see the proper end position
                breakBlock();
            }

            timeToLaunch--;
            if (timeToLaunch == 0) {
                if (toRefresh) {
                    timeToLaunch = maxTimeToLaunch;
                    toRefresh = false;
                    setDeltaMovement(0, 0, 0);
                    setEffect((byte)2);
                    playSound(JSoundRegistry.CMOON_BLOCKHALT.get(), 1, 1);
                } else if (!launched) {
                    final Vec3 eP = master.getEyePosition();
                    final Vec3 rangeMod = master.getLookAngle().scale(32);
                    final EntityHitResult eHit = ProjectileUtil.getEntityHitResult(master, eP, eP.add(rangeMod),
                            master.getBoundingBox().inflate(32),
                            EntitySelector.NO_CREATIVE_OR_SPECTATOR.and(entity -> entity != this),
                            1024 // Squared
                    );

                    final Vec3 targetPos = Objects.requireNonNullElseGet(eHit, () -> level().clip(
                            new ClipContext(eP, eP.add(rangeMod), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, master)
                    )).getLocation();

                    setDeltaMovement(targetPos.subtract(position()).normalize()); //.multiply(1)

                    playSound(JSoundRegistry.CMOON_BLOCKLAUNCH.get(), 1, 1);
                    launched = true;
                    setDiscardFriction(true);
                }
            }

            if (launched && timeLaunched < 20 && !hit) {
                timeLaunched++;
                Set<LivingEntity> toHurt = JUtils.generateHitbox(level(), position(), 1, Set.of(master));
                DamageSource damageSource = level().damageSources().mobAttack(master);
                for (LivingEntity living : toHurt) {
                    LivingEntity target = JUtils.getUserIfStand(living);
                    if (target == master || target == this || !JUtils.canDamage(damageSource, target)) {
                        continue;
                    }
                    hit = true;
                    StandEntity.damageLogic(level(), target, getDeltaMovement(), 15, 1, true,
                            6, false, 11, damageSource, master, CommonHitPropertyComponent.HitAnimation.MID, false);
                }
            }

            if (timeLaunched == 20) {
                setNoGravity(false);
            }
        }
    }

    public static AttributeSupplier.Builder createBlockAttributes() {
        return createLivingAttributes() // This must be used instead of DefaultAttributeContainer.builder() due to compatibility with step-height-entity-attribute
                .add(Attributes.MAX_HEALTH, 10)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1)
                .add(Attributes.MOVEMENT_SPEED)
                .add(Attributes.ARMOR, 10)
                .add(Attributes.ARMOR_TOUGHNESS);
    }

    @Override
    public void push(@NotNull Entity entity) { }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.getDirectEntity() != null) {
            return false;
        }
        return super.hurt(source, amount);
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(@NotNull DamageSource source) {
        return SoundEvents.STONE_STEP;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.STONE_BREAK;
    }

    @Override
    protected @NotNull AABB makeBoundingBox() { // Centered around 0,0,0 instead of 0,0.5,0
        double x = getX();
        double y = getY();
        double z = getZ();
        double s = 0.5;
        return new AABB(x + s, y + s, z + s, x - s, y - s, z - s);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        writeMasterNbt(tag);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        readMasterNbt(tag);
    }

    @Override
    public void setItemSlot(@NotNull EquipmentSlot slot, @NotNull ItemStack stack) {
        if (slot == EquipmentSlot.MAINHAND) super.setItemSlot(slot, stack);
    }

    @Override
    public @NotNull ItemStack getItemBySlot(@NotNull EquipmentSlot slot) {
        if (slot == EquipmentSlot.MAINHAND) return entityData.get(BLOCKSTACK);
        return ItemStack.EMPTY;
    }

    // Animations
    private final AnimatableInstanceCache cache = AzureLibUtil.createInstanceCache(this);
    
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("animation.block.idle");
    private PlayState predicate(AnimationState<GeoAnimatable> state) {
        return state.setAndContinue(IDLE);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
