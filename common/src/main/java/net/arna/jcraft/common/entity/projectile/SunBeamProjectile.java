package net.arna.jcraft.common.entity.projectile;

import mod.azure.azurelib.core.animatable.GeoAnimatable;
import mod.azure.azurelib.core.animatable.instance.AnimatableInstanceCache;
import mod.azure.azurelib.core.animation.AnimatableManager;
import mod.azure.azurelib.core.animation.AnimationController;
import mod.azure.azurelib.core.animation.AnimationState;
import mod.azure.azurelib.core.animation.RawAnimation;
import mod.azure.azurelib.core.object.PlayState;
import mod.azure.azurelib.util.AzureLibUtil;
import mod.azure.azurelib.util.RenderUtils;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.component.living.CommonHitPropertyComponent;
import net.arna.jcraft.common.entity.damage.JDamageSources;
import net.arna.jcraft.common.entity.stand.TheSunEntity;
import net.arna.jcraft.common.util.JParticleType;
import net.arna.jcraft.common.util.JUtils;
import net.arna.jcraft.registry.JEntityTypeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static net.arna.jcraft.common.entity.stand.StandEntity.damageLogic;
import static net.arna.jcraft.common.util.JUtils.canDamage;

public class SunBeamProjectile extends AbstractArrow implements GeoAnimatable {
    private final int stun = 10;
    private int length = 0;
    private static final int MAX_LENGTH = 64;
    private @Nullable TheSunEntity sun;

    public static final EntityDataAccessor<Integer> SKIN;

    static {
        SKIN = SynchedEntityData.defineId(SunBeamProjectile.class, EntityDataSerializers.INT);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(SKIN, 0);
    }

    public int getSkin() {
        return entityData.get(SKIN);
    }

    public void setSkin(int skin) {
        entityData.set(SKIN, skin);
    }

    public SunBeamProjectile(Level world) {
        super(JEntityTypeRegistry.SUN_BEAM.get(), world);
        this.setNoGravity(true);
        noCulling = true;
    }

    public void assignSun(TheSunEntity sunEntity) {
        this.sun = sunEntity;
    }

    @Override
    protected ItemStack getPickupItem() {
        return ItemStack.EMPTY;
    }

    // Light isn't very heavy
    @Override
    public void push(Entity entity) {
    }

    @Override
    public boolean canCollideWith(Entity other) {
        return false;
    }

    @Override
    public boolean isOnFire() {
        return false;
    }

    private boolean allowRotation = true;
    @Override
    public void setXRot(float xRot) {
        if (allowRotation) super.setXRot(xRot);
    }

    @Override
    public void setYRot(float yRot) {
        if (allowRotation) super.setYRot(yRot);
    }

    @Override
    public void tick() {
        allowRotation = false;
        super.tick();

        if (sun != null) setPos(position().add(JUtils.deltaPos(sun)));
        Vec3 curPos = position();

        if (tickCount > 5 && tickCount <= 10) {
            length += MAX_LENGTH / 5;
        }

        if (level().isClientSide()) {
            if (tickCount <= 20) {
                Vec3 velocity = getDeltaMovement().scale(random.nextDouble() * length * 10.0);
                level().addParticle(
                        getSkin() == 2 ? ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.FLAME,
                        curPos.x + random.nextGaussian() * 0.25,
                        curPos.y + random.nextGaussian() * 0.25,
                        curPos.z + random.nextGaussian() * 0.25,
                        velocity.x,
                        velocity.y,
                        velocity.z
                );
            }
        } else {
            if (tickCount <= 20) {
                if (tickCount % 3 == 0 && getOwner() instanceof LivingEntity owner) {
                    Set<Entity> filter = new HashSet<>();
                    filter.add(owner);
                    filter.add(sun);
                    filter.add(this);
                    if (owner.isVehicle()) {
                        filter.addAll(owner.getPassengers());
                    }

                    DamageSource damageSource = JDamageSources.create(level(), DamageTypes.MOB_ATTACK, owner);

                    // Recursive hitbox check between current and previous position
                    Vec3 towardsVec = getDeltaMovement().normalize();
                    List<LivingEntity> hurtAll = new ArrayList<>();
                    double hitboxSize = 2.0;
                    for (double i = 0.0; i < length / hitboxSize; i++) {
                        Vec3 laserPos = curPos.add(towardsVec.scale(i * hitboxSize));
                        Set<LivingEntity> targets = JUtils.generateHitbox(level(), laserPos, hitboxSize, filter);
                        targets.removeIf(hurtAll::contains);
                        hurtAll.addAll(targets);
                        TheSunEntity.dryOut((ServerLevel) level(), BlockPos.containing(laserPos));
                    }
                    hurtAll.removeIf(e -> !canDamage(damageSource, e));

                    if (!hurtAll.isEmpty()) {
                        for (LivingEntity l : hurtAll) {
                            LivingEntity target = JUtils.getUserIfStand(l);
                            damageLogic(level(), target, Vec3.ZERO, stun, 1, false, 1f,
                                    true, 2, damageSource, owner, CommonHitPropertyComponent.HitAnimation.values()[random.nextInt(3)]);
                        }

                        Vec3 hitPos = hurtAll.get(0).position();
                        JCraft.createParticle((ServerLevel) level(),
                                hitPos.x + random.nextGaussian() * 0.25,
                                hitPos.y + random.nextGaussian() * 0.25,
                                hitPos.z + random.nextGaussian() * 0.25,
                                JParticleType.HIT_SPARK_1);
                    }
                }
            } else if (tickCount >= 24) {
                kill();
            }
        }
    }

    public void updateRotation() {
        super.updateRotation();
    }

    // Animations
    private final AnimatableInstanceCache cache = AzureLibUtil.createInstanceCache(this);

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    private static final RawAnimation fire = RawAnimation.begin().thenLoop("animation.sunbeam.fire");
    private PlayState predicate(AnimationState<SunBeamProjectile> state) {
        return state.setAndContinue(fire);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public double getTick(Object entity) {
        return RenderUtils.getCurrentTick();
    }
}
