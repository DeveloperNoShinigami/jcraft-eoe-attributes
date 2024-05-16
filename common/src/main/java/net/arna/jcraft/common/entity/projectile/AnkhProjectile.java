package net.arna.jcraft.common.entity.projectile;

import mod.azure.azurelib.animatable.GeoEntity;
import mod.azure.azurelib.core.animatable.instance.AnimatableInstanceCache;
import mod.azure.azurelib.core.animation.AnimatableManager;
import mod.azure.azurelib.util.AzureLibUtil;
import net.arna.jcraft.common.component.living.CommonHitPropertyComponent;
import net.arna.jcraft.common.entity.stand.MagiciansRedEntity;
import net.arna.jcraft.common.util.JUtils;
import net.arna.jcraft.registry.JEntityTypeRegistry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;


public class AnkhProjectile extends AbstractArrow implements GeoEntity {
    private int ticksInAir;
    private boolean variation = false;
    private double orbitRange = 3;
    private double orbitOffset = 0;

    public AnkhProjectile(EntityType<? extends AnkhProjectile> entityType, Level world) {
        super(entityType, world);
    }

    public AnkhProjectile(Level world, LivingEntity owner) {
        super(JEntityTypeRegistry.ANKH.get(), owner, world);
        this.setOwner(owner);
        this.pickup = Pickup.DISALLOWED;
    }

    public void setOrbitRange(double range) {
        this.orbitRange = range;
    }

    public void setOrbitOffset(double offset) {
        this.orbitOffset = offset;
    }

    public void setVariation(boolean variation) {
        this.variation = variation;
    }

    @Override
    public ItemStack getPickupItem() {
        return new ItemStack(Items.AIR);
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    @Override
    protected boolean updateInWaterStateAndDoFluidPushing() {
        return false;
    }

    @Override
    public boolean isNoPhysics() {
        return this.variation;
    }

    @Override
    protected SoundEvent getDefaultHitGroundSoundEvent() {
        return SoundEvents.FIRECHARGE_USE;
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        if (level().isClientSide) {
            return;
        }
        Entity owner = getOwner();
        if (owner == null) {
            return;
        }
        Entity entity = entityHitResult.getEntity();
        if (owner.hasPassenger(entity) || entity == owner) {
            return;
        }

        entity.setSecondsOnFire(3);
        JUtils.projectileDamageLogic(this, level(), entity, Vec3.ZERO, 5, 1, false, 3.5f, 8, CommonHitPropertyComponent.HitAnimation.MID);
        discard();
    }

    @Override
    protected void onHitBlock(BlockHitResult blockHitResult) {
        MagiciansRedEntity.ignite(level(), blockHitResult.getBlockPos());
        super.onHitBlock(blockHitResult);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("variation", this.variation);
        tag.putShort("life", (short) this.ticksInAir);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.ticksInAir = tag.getShort("life");
        this.variation = tag.getBoolean("variation");
    }

    @Override
    public void tick() {
        super.tick();

        if (level().isClientSide()) {
            Vec3 vel = getDeltaMovement();
            this.level().addParticle(
                    ParticleTypes.FLAME,
                    getX() + random.nextFloat() * 0.5f - 0.25f,
                    getY() + random.nextFloat() * 0.5f - 0.25f,
                    getZ() + random.nextFloat() * 0.5f - 0.25f,
                    vel.x / 2, vel.y / 2, vel.z / 2
            );
        } else {
            if (this.inGround) {
                discard();
            } else {
                this.ticksInAir++;
                if (this.ticksInAir >= 600) {
                    discard();
                }
            }

            if (this.getOwner() instanceof LivingEntity owner) {
                if (owner.isAlive()) {
                    if (this.variation) {
                        this.inGround = false;
                        this.inGroundTime = 0;

                        // Orbiting logic
                        double orbitProg = Math.toRadians(this.tickCount * 3 + this.orbitOffset);
                        Vec3 orbitPos = owner.getEyePosition().add(
                                Math.sin(orbitProg) * this.orbitRange,
                                0.0,
                                Math.cos(orbitProg) * this.orbitRange
                        );

                        Vec3 towardsVel = orbitPos.subtract(this.position()).normalize().scale(0.2);
                        double stabilization = this.position().distanceTo(orbitPos);
                        if (stabilization > 0.8) {
                            stabilization = 0.8;
                        }
                        this.setDeltaMovement(this.getDeltaMovement().scale(stabilization).add(towardsVel));
                        this.hurtMarked = true;

                        // Entity hit logic, due to variations being noclipped
                        Vec3 pos = this.position();
                        Vec3 nextPos = pos.add(this.getDeltaMovement());
                        //HitResult hitResult = this.world.raycast(new RaycastContext(pos, nextPos, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, this));
                        //if (hitResult.getType() != HitResult.Type.MISS) nextPos = hitResult.getPos();
                        EntityHitResult entityHitResult = this.findHitEntity(pos, nextPos);
                        if (entityHitResult != null) {
                            this.onHitEntity(entityHitResult);
                        }
                    }
                } else {
                    this.variation = false;
                }
            } else {
                discard();
            }
        }
    }

    // Animations
    private final AnimatableInstanceCache cache = AzureLibUtil.createInstanceCache(this);


    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
