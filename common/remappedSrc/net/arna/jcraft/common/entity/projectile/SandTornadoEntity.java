package net.arna.jcraft.common.entity.projectile;

import net.arna.jcraft.common.component.living.CommonHitPropertyComponent;
import net.arna.jcraft.common.entity.stand.StandEntity;
import net.arna.jcraft.common.util.IOwnable;
import net.arna.jcraft.common.util.JUtils;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.Set;

public class SandTornadoEntity extends LivingEntity implements GeoEntity, IOwnable {
    private static final EntityDataAccessor<Boolean> DISAPPEARED;
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private LivingEntity master;
    private int hitsLeft = 5;

    static {
        DISAPPEARED = SynchedEntityData.defineId(SandTornadoEntity.class, EntityDataSerializers.BOOLEAN);
    }

    public SandTornadoEntity(EntityType<? extends LivingEntity> entityType, Level world) {
        super(entityType, world);
    }

    public boolean hasDisappeared() {
        return this.entityData.get(DISAPPEARED);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(DISAPPEARED, false);
    }

    @Override
    public LivingEntity getMaster() {
        return master;
    }

    public void setMaster(LivingEntity l) {
        this.master = l;
    }

    private void disappear() {
        entityData.set(DISAPPEARED, true);
        kill();
    }

    @Override
    public void tick() {
        super.tick();
        if (hasDisappeared()) {
            return;
        }

        Vec3 circulation = new Vec3(Mth.sin(tickCount * 0.25f) * 0.3f, 0.0, Mth.cos(tickCount * 0.25f) * 0.3f);

        if (level().isClientSide) {
            for (int i = 0; i < 3; i++) {
                level().addParticle(
                        new BlockParticleOption(ParticleTypes.BLOCK, Blocks.SAND.defaultBlockState()),
                        getX() + random.nextFloat() - 0.5f,
                        getY() + random.nextFloat() * 2f,
                        getZ() + random.nextFloat() - 0.5f,
                        circulation.x, 0, circulation.z
                );
            }
        } else if (tickCount % 5 == 0) {
            if (master == null) {
                if (isAlive()) {
                    kill();
                }
                return;
            }

            Set<LivingEntity> toHurt = JUtils.generateHitbox(level(), getEyePosition(), 1.8, Set.of(this, master));

            if (toHurt.isEmpty()) {
                setDeltaMovement(getDeltaMovement().add(getLookAngle().scale(0.5)).scale(0.4));
            } else {
                setDeltaMovement(getDeltaMovement().scale(0.25));
                for (LivingEntity living : toHurt) {
                    LivingEntity target = JUtils.getUserIfStand(living);
                    if (target.isPassengerOfSameVehicle(master)) {
                        return;
                    }
                    StandEntity.damageLogic(level(), target, circulation, 10, 1, false, 2f, true, 6,
                            level().damageSources().mobAttack(master), master, CommonHitPropertyComponent.HitAnimation.MID, false);
                }
                hitsLeft--;
            }

            hurtMarked = true;

            if (hitsLeft < 1 || getHealth() <= 0 || tickCount >= 500) {
                disappear();
            }
        }
    }

    // Physical properties
    @Override
    public boolean isInvulnerableTo(DamageSource damageSource) {
        return !damageSource.is(DamageTypes.FELL_OUT_OF_WORLD);
    }

    @Override
    protected void doPush(Entity entity) {
    }

    @Override
    public void push(Entity entity) {
    }

    @Override
    public boolean canCollideWith(Entity other) {
        return false;
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.SAND_STEP;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.SAND_BREAK;
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    @Override
    public boolean startRiding(Entity entity, boolean force) {
        return false;
    }

    public static AttributeSupplier.Builder createTornadoAttributes() {
        return createLivingAttributes() // This must be used instead of DefaultAttributeContainer.builder() due to compatibility with step-height-entity-attribute
                .add(Attributes.MAX_HEALTH, 20)
                .add(Attributes.KNOCKBACK_RESISTANCE)
                .add(Attributes.MOVEMENT_SPEED)
                .add(Attributes.ARMOR)
                .add(Attributes.ARMOR_TOUGHNESS);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (master == null) {
            return;
        }
        boolean ownerIsPlayer = master instanceof Player;
        tag.putBoolean("playerOwner", ownerIsPlayer);
        if (ownerIsPlayer) {
            tag.putUUID("ownerUUID", master.getUUID());
        } else {
            tag.putInt("ownerID", master.getId());
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        boolean ownerIsPlayer = tag.getBoolean("playerOwner");
        if (ownerIsPlayer) {
            master = level().getPlayerByUUID(tag.getUUID("ownerUUID"));
        } else {
            master = (LivingEntity) level().getEntity(tag.getInt("ownerID")); // Always is living
        }
    }

    @Override
    public Iterable<ItemStack> getArmorSlots() {
        return List.of();
    }

    @Override
    public ItemStack getItemBySlot(EquipmentSlot slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setItemSlot(EquipmentSlot slot, ItemStack stack) {
    }

    @Override
    public HumanoidArm getMainArm() {
        return null;
    }

    // Animations

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<GeoAnimatable>(this, "controller", 0, this::predicate));
    }

    private PlayState predicate(AnimationState<GeoAnimatable> state) {
        return state.setAndContinue(RawAnimation.begin().thenLoop(hasDisappeared() ? "animation.sandtornado.disappear" : "animation.sandtornado.idle"));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
