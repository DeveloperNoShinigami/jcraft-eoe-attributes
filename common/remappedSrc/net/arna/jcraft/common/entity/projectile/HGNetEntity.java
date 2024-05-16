package net.arna.jcraft.common.entity.projectile;

import net.arna.jcraft.common.component.living.CommonHitPropertyComponent;
import net.arna.jcraft.common.entity.stand.StandEntity;
import net.arna.jcraft.common.gravity.api.GravityChangerAPI;
import net.arna.jcraft.common.util.ICustomDamageHandler;
import net.arna.jcraft.common.util.IOwnable;
import net.arna.jcraft.common.util.JUtils;
import net.arna.jcraft.registry.JSoundRegistry;
import net.arna.jcraft.registry.JStatusRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
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

public class HGNetEntity extends JAttackEntity implements GeoEntity, ICustomDamageHandler {
    public static final EntityDataAccessor<Integer> SKIN;
    public static final EntityDataAccessor<Integer> STATE;
    public static final EntityDataAccessor<Boolean> CHARGED;

    private int animTimer = 0;
    private Vec3 target;

    private int lifeTime = 30 * 20 + 20;

    private static final int FIRE_COOLDOWN = 10 * 20;
    private static final int CONSTRICT_COOLDOWN = 10 * 20;
    private int fireCooldown = 0, constrictCooldown = 0;

    private boolean finalAttack = false;

    public HGNetEntity(EntityType<? extends LivingEntity> entityType, Level world) {
        super(entityType, world);
    }

    static {
        STATE = SynchedEntityData.defineId(HGNetEntity.class, EntityDataSerializers.INT);
        SKIN = SynchedEntityData.defineId(HGNetEntity.class, EntityDataSerializers.INT);
        CHARGED = SynchedEntityData.defineId(HGNetEntity.class, EntityDataSerializers.BOOLEAN);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(SKIN, 0);
        entityData.define(STATE, 0);
        entityData.define(CHARGED, true);
    }

    public boolean isCharged() {
        return entityData.get(CHARGED);
    }

    public void setCharged(boolean charged) {
        if (isCharged() != charged) {
            entityData.set(CHARGED, charged);
        }
    }

    public int getState() {
        return entityData.get(STATE);
    }

    public int getSkin() {
        return entityData.get(SKIN);
    }

    public void setState(int state) {
        if (getState() != state) {
            entityData.set(STATE, state);
        }
    }

    public void setSkin(int skin) {
        entityData.set(SKIN, skin);
    }

    public void tryFireAt(Vec3 target, boolean finalAttack) {
        if (isCharged() && JUtils.canAct(this) && getState() != 2) {
            playSound(JSoundRegistry.HG_SPLASH.get(), 1, 1);
            this.target = target;
            fireCooldown = FIRE_COOLDOWN;
            setCharged(false);

            if (finalAttack) {
                this.finalAttack = true;
                animTimer = 50;
            } else {
                animTimer = 25;
            }
        }
    }

    @Override
    public void tick() {
        if (getFeetBlockState().canOcclude()) {
            setDeltaMovement(0, 0, 0);
        }

        super.tick();

        if (!level().isClientSide) {
            if (--lifeTime <= 0 || master == null) {
                discard();
                return;
            }

            if (lifeTime <= 20) {
                setState(3);
                return;
            }

            if (JUtils.canAct(this)) {
                Vec3 upVec = GravityChangerAPI.getEyeOffset(this);

                if (tickCount == 1) {
                    Vec3 launchVec = upVec.scale(0.2);

                    JUtils.displayHitbox(level(), getBoundingBox());
                    getInsideEntities().forEach(
                            living -> {
                                if (!living.isPassengerOfSameVehicle(master)) {
                                    StandEntity.damageLogic(
                                            level(), living, launchVec, 15, 3, false, 5f, false, 10,
                                            level().damageSources().mobAttack(this), master, CommonHitPropertyComponent.HitAnimation.HIGH
                                    );
                                }
                            }
                    );
                }

                if (getState() == 2) {
                    if (animTimer == 0) {
                        JUtils.displayHitbox(level(), getBoundingBox());
                        getInsideEntities().forEach(
                                living -> {
                                    if (!JUtils.isBlocking(living) && !living.isPassengerOfSameVehicle(master)) {
                                        StandEntity.stun(living, 17, 0);
                                    }
                                }
                        );
                    } else if (animTimer <= -20) {
                        setState(0);
                    }
                } else {
                    if (animTimer > 0) {
                        if (animTimer % 8 == 0) {
                            for (int i = 0; i < 3; i++) {
                                EmeraldProjectile emerald = new EmeraldProjectile(level(), getMaster());

                                Vec3 heightOffset = upVec.scale(0.8);
                                Vec3 emeraldPos = position().add(heightOffset).add(JUtils.randUnitVec(getRandom()));
                                emerald.setPos(emeraldPos);
                                emerald.setDeltaMovement(target.subtract(emeraldPos).normalize().scale(1.5));
                                if (finalAttack) {
                                    emerald.withReflect();
                                }

                                level().addFreshEntity(emerald);
                            }
                        }
                    } else if (finalAttack) {
                        lifeTime = 20;
                    }
                }
            } else {
                if (animTimer > 0) {
                    setState(0);
                    animTimer = 0;
                }
            }

            if (--fireCooldown < 0) {
                setCharged(true);
            }
            constrictCooldown--;
            animTimer--;
        }

        //JCraft.LOGGER.info("STATE: " + getState() + " ltime: " + lifeTime);
    }

    private List<LivingEntity> getInsideEntities() {
        return level().getEntitiesOfClass(LivingEntity.class, getBoundingBox(),
                EntitySelector.LIVING_ENTITY_STILL_ALIVE.and(EntitySelector.NO_CREATIVE_OR_SPECTATOR).and(entity -> !entity.equals(this)));
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.is(DamageTypes.IN_WALL)) {
            return false;
        }
        return super.hurt(source, amount);
    }

    @Override
    public void push(Entity entity) {
        tryConstrict(entity);
    }

    @Override
    public void doPush(Entity entity) {
        tryConstrict(entity);
    }

    private void tryConstrict(Entity entity) {
        if (!JUtils.canAct(this)) {
            return;
        }
        if (entity == null) {
            return;
        }
        if (master == null || entity.isPassengerOfSameVehicle(master)) {
            return;
        }
        if (entity instanceof JAttackEntity attackEntity && attackEntity.getMaster() == master) {
            return;
        }
        if (entity instanceof StandEntity<?, ?> stand && stand.getUser() == master) {
            return;
        }

        // Not constricting or dying
        if (getState() < 2 && constrictCooldown <= 0) {
            setState(2);
            constrictCooldown = CONSTRICT_COOLDOWN;
            animTimer = 6;
        }
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.SLIME_HURT;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.CHORUS_FLOWER_DEATH;
    }

    @Override
    public boolean isNoGravity() {
        return false;
    }

    @Override
    public boolean startRiding(Entity entity, boolean force) {
        return false;
    }

    @Override
    public boolean addEffect(MobEffectInstance effect, @Nullable Entity source) {
        if (effect.getEffect() == JStatusRegistry.DAZED) {
            return super.addEffect(effect, source);
        }
        return false;
    }

    public static AttributeSupplier.Builder createNetAttributes() {
        return createLivingAttributes() // This must be used instead of DefaultAttributeContainer.builder() due to compatibility with step-height-entity-attribute
                .add(Attributes.MAX_HEALTH, 20)
                .add(Attributes.KNOCKBACK_RESISTANCE, 20)
                .add(Attributes.MOVEMENT_SPEED, 0)
                .add(Attributes.ARMOR, 10)
                .add(Attributes.ARMOR_TOUGHNESS, 10);
    }

    @Override
    public boolean reflectsDamage() {
        return false;
    }

    @Override
    public boolean handleDamage(Vec3 kbVec, int stunTicks, int stunLevel, boolean overrideStun, float damage, boolean lift, int blockstun, DamageSource source, Entity attacker, CommonHitPropertyComponent.HitAnimation hitAnimation, boolean canBackstab, boolean unblockable) {
        if (attacker == master || (attacker instanceof IOwnable ownable && ownable.getMaster() == master)) {
            return false;
        }
        return true;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("lifeTime", lifeTime);

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
        lifeTime = tag.getInt("lifeTime");

        boolean ownerIsPlayer = tag.getBoolean("playerOwner");
        if (ownerIsPlayer) {
            master = level().getPlayerByUUID(tag.getUUID("ownerUUID"));
        } else {
            master = (LivingEntity) level().getEntity(tag.getInt("ownerID")); // Always is living
        }
    }

    // Animations
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<GeoAnimatable>(this, "controller", 6, this::predicate));
    }

    private PlayState predicate(AnimationState<GeoAnimatable> state) {
        if (tickCount < 5) {
            state.setAnimation(RawAnimation.begin().thenPlay("animation.hg_nets.spawn"));
        } else {
            if (getState() == 3) {
                state.setAnimation(RawAnimation.begin().thenPlay("animation.hg_nets.wilt"));
            } else if (getState() == 2) {
                state.setAnimation(RawAnimation.begin().thenPlay("animation.hg_nets.constrict"));
            } else {
                state.setAnimation(RawAnimation.begin().thenLoop("animation.hg_nets.idle"));
            }
        }
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
