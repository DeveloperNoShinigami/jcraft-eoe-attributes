package net.arna.jcraft.common.entity.projectile;


import net.arna.jcraft.common.component.living.CommonHitPropertyComponent;
import net.arna.jcraft.common.entity.stand.StandEntity;
import net.arna.jcraft.common.util.IOwnable;
import net.arna.jcraft.common.util.JUtils;
import net.arna.jcraft.registry.JSoundRegistry;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import mod.azure.azurelib.animatable.GeoEntity;
import mod.azure.azurelib.core.animatable.instance.AnimatableInstanceCache;
import mod.azure.azurelib.core.animation.AnimatableManager;
import mod.azure.azurelib.core.animation.AnimationController;
import mod.azure.azurelib.core.animation.AnimationState;
import mod.azure.azurelib.core.animation.RawAnimation;
import mod.azure.azurelib.core.object.PlayState;
import mod.azure.azurelib.util.AzureLibUtil;
import mod.azure.azurelib.core.animatable.GeoAnimatable;
import java.util.List;
import java.util.Set;

public class BlockProjectile extends LivingEntity implements IOwnable, GeoEntity {
    private final AnimatableInstanceCache cache = AzureLibUtil.createInstanceCache(this);
    private final int maxTimeToLaunch = 15;
    private int timeToLaunch = maxTimeToLaunch;
    private int timeLaunched = 0;
    private boolean toRefresh = false;
    private boolean launched = false;
    private boolean hit = false;

    private static final EntityDataAccessor<Integer> EFFECT;
    private static final EntityDataAccessor<ItemStack> BLOCKSTACK;

    static {
        EFFECT = SynchedEntityData.defineId(BlockProjectile.class, EntityDataSerializers.INT);
        BLOCKSTACK = SynchedEntityData.defineId(BlockProjectile.class, EntityDataSerializers.ITEM_STACK);
    }

    public BlockProjectile(EntityType<? extends LivingEntity> entityType, Level world) {
        super(entityType, world);
        setNoGravity(true);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(EFFECT, 0);
        entityData.define(BLOCKSTACK, Items.STONE.getDefaultInstance());
    }

    public void setBlockStack(ItemStack stack) {
        entityData.set(BLOCKSTACK, stack);
    }

    public void setEffect(int effect) {
        entityData.set(EFFECT, effect);
    }

    public void markRefresh() {
        toRefresh = true;
    }

    private void breakBlock() {
        setPos(position().add(getDeltaMovement()));
        setDeltaMovement(0, 0, 0);
        setEffect(1);
        setDiscardFriction(false);
        kill();
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) {
            Vec3 vel = getDeltaMovement();
            int effect = entityData.get(EFFECT);
            if (effect != 0) {
                for (int i = 0; i < 32; i++) {
                    level().addParticle(
                            effect == 1 ? new BlockParticleOption(ParticleTypes.BLOCK, Blocks.STONE.defaultBlockState()) : ParticleTypes.REVERSE_PORTAL,
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
                setEffect(0);
            }

            if (hit || onGround() || tickCount > 200) // Placing this here delays it by 1 tick, allowing the client to see the proper end position
            {
                breakBlock();
            }

            timeToLaunch--;
            if (timeToLaunch == 0) {
                if (toRefresh) {
                    timeToLaunch = maxTimeToLaunch;
                    toRefresh = false;
                    setDeltaMovement(0, 0, 0);
                    setEffect(2);
                    playSound(JSoundRegistry.CMOON_BLOCKHALT.get(), 1, 1);
                } else if (!launched) {
                    Vec3 targetPos;
                    Vec3 eP = master.getEyePosition();
                    Vec3 rangeMod = master.getLookAngle().scale(32);
                    EntityHitResult eHit = ProjectileUtil.getEntityHitResult(master, eP, eP.add(rangeMod),
                            master.getBoundingBox().inflate(32),
                            EntitySelector.NO_CREATIVE_OR_SPECTATOR.and(entity -> entity != this),
                            1024 // Squared
                    );

                    if (eHit != null) {
                        targetPos = eHit.getLocation();
                    } else {
                        targetPos = level().clip(
                                new ClipContext(eP, eP.add(rangeMod), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, master)
                        ).getLocation();
                    }

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
    public void push(Entity entity) {
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.getDirectEntity() != null) {
            return false;
        }
        return super.hurt(source, amount);
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.STONE_STEP;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.STONE_BREAK;
    }

    @Override
    protected AABB makeBoundingBox() { // Centered around 0,0,0 instead of 0,0.5,0
        double x = getX();
        double y = getY();
        double z = getZ();
        double s = 0.5;
        return new AABB(x + s, y + s, z + s, x - s, y - s, z - s);
    }

    @Override
    public boolean startRiding(Entity entity, boolean force) {
        return false;
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
        return entityData.get(BLOCKSTACK);
    }

    @Override
    public void setItemSlot(EquipmentSlot slot, ItemStack stack) {
    }

    @Override
    public HumanoidArm getMainArm() {
        return null;
    }

    private LivingEntity master;

    @Override
    public LivingEntity getMaster() {
        return master;
    }

    public void setMaster(LivingEntity l) {
        this.master = l;
    }

    // Animations

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<GeoAnimatable>(this, "controller", 0, this::predicate));
    }

    private PlayState predicate(AnimationState<GeoAnimatable> state) {
        return state.setAndContinue(RawAnimation.begin().thenLoop("animation.block.idle"));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
