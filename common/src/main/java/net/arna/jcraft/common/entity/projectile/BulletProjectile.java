package net.arna.jcraft.common.entity.projectile;

import net.arna.jcraft.common.component.living.CommonHitPropertyComponent;
import net.arna.jcraft.common.entity.stand.StandEntity;
import net.arna.jcraft.common.util.JUtils;
import net.arna.jcraft.registry.JEntityTypeRegistry;
import net.arna.jcraft.registry.JSoundRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import mod.azure.azurelib.animatable.GeoEntity;
import mod.azure.azurelib.core.animatable.instance.AnimatableInstanceCache;
import mod.azure.azurelib.core.animation.AnimatableManager;
import mod.azure.azurelib.util.AzureLibUtil;
import org.jetbrains.annotations.NotNull;

public class BulletProjectile extends AbstractArrow implements GeoEntity {
    private int stunTicks;
    private float damage;
    private float mass; // Used for penetration calculation

    private static final EntityDataAccessor<Float> CALIBER; //mm

    static {
        CALIBER = SynchedEntityData.defineId(BulletProjectile.class, EntityDataSerializers.FLOAT);
    }

    public void setCaliber(float cal) {
        entityData.set(CALIBER, cal);
    }

    public float getCaliber() {
        return entityData.get(CALIBER);
    }

    @Override
    protected void defineSynchedData() {
        entityData.define(CALIBER, 9f);
        super.defineSynchedData();
    }

    public BulletProjectile(Level world) {
        super(JEntityTypeRegistry.BULLET.get(), world);
    }

    public BulletProjectile(Level world, LivingEntity owner, float caliber, float length, int stunTicks, float damage) {
        super(JEntityTypeRegistry.BULLET.get(), owner, world);

        setCaliber(caliber);
        this.stunTicks = stunTicks;
        this.damage = damage;
        this.mass = (length * caliber * caliber * Mth.PI) * 0.000000013f; // Volume of a cylinder (mm^3) * Density of lead (kg/mm^3)

        setSoundEvent(JSoundRegistry.BULLET_RICOCHET.get());
    }

    @Override
    protected void onHit(HitResult hitResult) {
        final HitResult.Type type = hitResult.getType();

        if (type == HitResult.Type.ENTITY) {
            this.onHitEntity((EntityHitResult) hitResult);
            level().gameEvent(GameEvent.PROJECTILE_LAND, hitResult.getLocation(), GameEvent.Context.of(this, null));
        } else if (type == HitResult.Type.BLOCK) {
            final BlockHitResult blockHitResult = (BlockHitResult) hitResult;
            final BlockPos blockPos = blockHitResult.getBlockPos();
            final BlockState blockState = level().getBlockState(blockPos);
            if (blockState.isAir()) {
                return;
            }

            // Calculate penetrative value and decide if it should land
            final Vec3i intNormal = blockHitResult.getDirection().getNormal();
            final Vec3 normal = new Vec3(intNormal.getX(), intNormal.getY(), intNormal.getZ());
            final Vec3 impactVec = getDeltaMovement();

            // a*b = |a|*|b|*cos(φ) , a*b = a.dotProduct(b)
            final double impactAngleRad = Math.acos(normal.dot(impactVec.normalize())) - Math.PI / 2.0;
            final double impactAngleDeg = Math.toDegrees(impactAngleRad);
            //JCraft.LOGGER.info("Impact Angle: " + impactAngle + "");

            // Ek = mv^2/2
            final double kineticEnergy = mass * impactVec.lengthSqr() / 2;
            double hardness = blockState.getBlock().defaultDestroyTime();
            if (hardness < 0) { // Unbreakable block
                hardness = Double.MAX_VALUE;
            }

            final double penAngle = 45.0 + hardness * 5; // This is bs but so is minecraft physics

            //if (getOwner() instanceof PlayerEntity player) player.sendMessage(Text.of("Impact Angle: " + impactAngleDeg + "\n Hardness: " + hardness + "\n Penetration Angle: " + penAngle + "\n Kinetic Energy: " + kineticEnergy));

            final boolean lowEnergy = kineticEnergy < 0.001;
            if (impactAngleDeg > penAngle || lowEnergy) { // If penetrated or ran out of energy
                final boolean through = hardness <= 1.0; // Go straight through?
                if (lowEnergy || !through) { // Lodged inside block
                    this.onHitBlock(blockHitResult);
                    this.level().gameEvent(GameEvent.PROJECTILE_LAND, blockPos, GameEvent.Context.of(this, blockState));
                    discard();
                } else if (!level().isClientSide) {
                    JUtils.serverPlaySound(JSoundRegistry.BULLET_PENETRATE.get(), (ServerLevel) level(), position(), 32);
                }
            } else { // Ricochet
                setDeltaMovement(impactVec.add(normal).scale(0.5 / hardness));
                if (!level().isClientSide) {
                    JUtils.serverPlaySound(JSoundRegistry.BULLET_RICOCHET.get(), (ServerLevel) level(), position(), 32);
                }
            }
        }
    }

    @Override
    public void setDeltaMovement(@NotNull Vec3 velocity) {
        super.setDeltaMovement(velocity);
        hurtMarked = true;
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        final Entity entity = entityHitResult.getEntity();
        if (entity instanceof LivingEntity living) {
            if (!level().isClientSide) {
                final Entity owner = getOwner();
                final LivingEntity target = JUtils.getUserIfStand(living);
                StandEntity.damageLogic(level(), target, getDeltaMovement().normalize(),
                        stunTicks, 1, false, damage, true, (int) (4 + damage),
                        level().damageSources().thrown(this, owner), owner, CommonHitPropertyComponent.HitAnimation.MID);
                JUtils.serverPlaySound(JSoundRegistry.BULLET_PENETRATE.get(), (ServerLevel) level(), position(), 32);
                discard();
            }
        } else {
            super.onHitEntity(entityHitResult);
        }
    }

    @Override
    public void tick() {
        super.tick();

        /*
        if (inGroundTime > 0) {
            if (getWorld().isClient() && inGroundTime == 2) {
                Vec3d rotVec = getRotationVector();
                BlockState blockState = world.getBlockState(getBlockPos().add(rotVec.x / 2, rotVec.y / 2, rotVec.z / 2));
                JCraft.LOGGER.info("Emitting impact effect with blockState " + blockState);
                Vec3d pos = getPos();
                for (int i = 0; i < 16; i++)
                    world.addParticle(
                            new BlockStateParticleEffect(ParticleTypes.BLOCK, blockState),
                            pos.x, pos.y, pos.z,
                            -rotVec.x * 5 + random.nextDouble() - 0.5,
                            -rotVec.y * 5 + random.nextDouble() - 0.5,
                            -rotVec.z * 5 + random.nextDouble() - 0.5
                    );
            } else if (inGroundTime > 10) {
                discard();
            }

            // DEBUG
            JCraft.LOGGER.info("CLIENT: Bullet position is " + getPos());
            if (inGround) {
                world.addParticle(
                        ParticleTypes.CAMPFIRE_SIGNAL_SMOKE,
                        getX(), getY(), getZ(),
                        0, 0, 0
                );
            } else {
                double l = 3.0;
                for (double i = 0; i < l; i++) {
                    world.addParticle(
                            ParticleTypes.DRAGON_BREATH,
                            MathHelper.lerp(i / l, getX(), prevX), MathHelper.lerp(i / l, getY(), prevY), MathHelper.lerp(i / l, getZ(), prevZ),
                            0, 0, 0
                    );
                }
            }
        }
         */
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putInt("StunTicks", stunTicks);
        nbt.putFloat("Mass", mass);
        nbt.putFloat("Damage", damage);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        stunTicks = nbt.getInt("StunTicks");
        mass = nbt.getFloat("Mass");
        damage = nbt.getFloat("Damage");
    }

    @Override
    protected @NotNull ItemStack getPickupItem() {
        //return BulletItem.ofCaliber(getCaliber());
        return ItemStack.EMPTY;
    }

    // Animations
    private final AnimatableInstanceCache cache = AzureLibUtil.createInstanceCache(this);

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
