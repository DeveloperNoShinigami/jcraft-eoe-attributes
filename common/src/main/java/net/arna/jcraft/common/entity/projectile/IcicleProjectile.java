package net.arna.jcraft.common.entity.projectile;

import mod.azure.azurelib.animatable.GeoEntity;
import mod.azure.azurelib.core.animatable.instance.AnimatableInstanceCache;
import mod.azure.azurelib.core.animation.AnimatableManager;
import mod.azure.azurelib.util.AzureLibUtil;
import net.arna.jcraft.common.component.living.CommonHitPropertyComponent;
import net.arna.jcraft.common.util.JUtils;
import net.arna.jcraft.registry.JEntityTypeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class IcicleProjectile extends AbstractArrow implements GeoEntity {
    private int ticksInAir;
    private int bouncesLeft = 5;
    private boolean reflect = false;

    public IcicleProjectile(EntityType<? extends IcicleProjectile> entityType, Level world) {
        super(entityType, world);
    }

    public IcicleProjectile(Level world) {
        super(JEntityTypeRegistry.ICICLE.get(), world);
    }

    public IcicleProjectile(Level world, LivingEntity owner) {
        super(JEntityTypeRegistry.ICICLE.get(), owner, world);
        // setNoGravity(true);
        setOwner(owner);
        setSoundEvent(SoundEvents.GLASS_BREAK);
    }

    public void withReflect() {
        reflect = true;
    }

    @Override
    public ItemStack getPickupItem() {
        return ItemStack.EMPTY;
    }

    private static final BlockParticleOption ICE_PARTICLE = new BlockParticleOption(ParticleTypes.BLOCK, Blocks.ICE.defaultBlockState());

    @Override
    protected void tickDespawn() {
        discard();
    }

    @Override
    public void tick() {
        super.tick();

        if (!inGround) {
            ++ticksInAir;
        } else {
            // Landing
            if (level().isClientSide) {
                double x = getX();
                double y = getY();
                double z = getZ();

                for (int i = 0; i < 8; i++) {
                    level().addParticle(ICE_PARTICLE, x, y, z,
                            random.nextGaussian(), random.nextGaussian(), random.nextGaussian());
                }
            }
        }

        if (level().isClientSide) {
            // Flight
            if (random.nextGaussian() < -0.002) {
                double x = getX();
                double y = getY();
                double z = getZ();
                Vec3 velocity = getDeltaMovement();
                level().addParticle(ParticleTypes.SNOWFLAKE, x, y, z,
                        velocity.x, velocity.y, velocity.z);
            }
            return;
        }

        if (ticksInAir > 200) {
            discard();
        }
    }

    @Override
    protected void onHit(HitResult hitResult) {
        if (reflect) {
            HitResult.Type type = hitResult.getType();
            if (type == HitResult.Type.ENTITY) {
                this.onHitEntity((EntityHitResult) hitResult);
                this.level().gameEvent(GameEvent.PROJECTILE_LAND, hitResult.getLocation(), GameEvent.Context.of(this, null));
            } else if (type == HitResult.Type.BLOCK) {
                BlockHitResult blockHitResult = (BlockHitResult) hitResult;
                if (bouncesLeft-- > 0) {
                    Vec3i normal = blockHitResult.getDirection().getNormal();
                    setDeltaMovement(getDeltaMovement().add(Vec3.atLowerCornerOf(normal)).normalize());
                } else {
                    this.onHitBlock(blockHitResult);
                    BlockPos blockPos = blockHitResult.getBlockPos();
                    this.level().gameEvent(GameEvent.PROJECTILE_LAND, blockPos, GameEvent.Context.of(this, this.level().getBlockState(blockPos)));
                }
            }
        } else {
            super.onHit(hitResult);
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        if (level().isClientSide) {
            return;
        }
        Entity entity = entityHitResult.getEntity();
        Entity owner = this.getOwner();

        if (owner != null && owner.hasPassenger(entity) || entity == owner) {
            return;
        }
        if (entity instanceof JAttackEntity attackEntity && attackEntity.getMaster() == owner) {
            return;
        }

        int blockstun = 4;
        int stunT = 10;

        JUtils.projectileDamageLogic(this, level(), entity, Vec3.ZERO, stunT, 1, false, 1, blockstun, CommonHitPropertyComponent.HitAnimation.MID);
        playSound(SoundEvents.GLASS_BREAK, 1, 1);
        discard();
    }

    @Override
    protected float getWaterInertia() {
        // Not actually drag, just a multiplier
        return 0.97F;
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
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) { }
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
