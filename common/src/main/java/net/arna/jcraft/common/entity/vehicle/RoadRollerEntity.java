package net.arna.jcraft.common.entity.vehicle;

import mod.azure.azurelib.core.animatable.GeoAnimatable;
import mod.azure.azurelib.core.animation.AnimatableManager;
import mod.azure.azurelib.core.animation.AnimationController;
import mod.azure.azurelib.core.animation.AnimationState;
import mod.azure.azurelib.core.animation.RawAnimation;
import mod.azure.azurelib.core.object.PlayState;
import net.arna.jcraft.common.component.living.CommonHitPropertyComponent;
import net.arna.jcraft.common.entity.stand.StandEntity;
import net.arna.jcraft.common.gravity.api.GravityChangerAPI;
import net.arna.jcraft.common.gravity.util.RotationUtil;
import net.arna.jcraft.common.util.JUtils;
import net.arna.jcraft.platform.JComponentPlatformUtils;
import net.arna.jcraft.registry.JEntityTypeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class RoadRollerEntity extends AbstractGroundVehicleEntity {
    public RoadRollerEntity(final Level level) {
        super(JEntityTypeRegistry.ROAD_ROLLER.get(), level);
    }

    private static final double INLINE_SPEED = 0.1d, TURN_RATE = 5.0d;
    private static final Map<BlockState, BlockState> flattenedBlockStates = Map.ofEntries(
            Map.entry(Blocks.DIRT.defaultBlockState(), Blocks.FARMLAND.defaultBlockState())
    );
    @Override
    public void movementTick(boolean w, boolean a, boolean s, boolean d, boolean space, boolean sneak) {
        double drag = 0.99;
        final Level level = level();
        final Direction gravity = GravityChangerAPI.getGravityDirection(this);
        oldYRot = getYRot();

        Vec3 movement = getDeltaMovement();
        if (Double.isNaN(movement.x)) movement = new Vec3(0, movement.y, movement.z);
        if (Double.isNaN(movement.y)) movement = new Vec3(movement.x, 0, movement.z);
        if (Double.isNaN(movement.z)) movement = new Vec3(movement.x, movement.y, 0);

        boolean grounded = onGround();
        if (grounded) {
            drag = getGroundFriction();
        } else { // Edge case coverage
            Optional<BlockPos> supporting = level.findSupportingBlock(this, getBoundingBox().inflate(0.1));
            if (supporting.isPresent()) {
                grounded = true;
                drag = level.getBlockState(supporting.get()).getBlock().getFriction();
            }
            // setOnGround(grounded);
        }

        if (grounded) {
            if (w || s) {
                double inline;
                if (w) inline = INLINE_SPEED;
                else inline = -INLINE_SPEED;

                if (a || d) {
                    double movementSpeed = movement.length(); // blocks per tick
                    if (Double.isNaN(movementSpeed)) movementSpeed = 0.0d;

                    float turnCW = 0.0f;
                    if (d) turnCW += Math.sqrt(movementSpeed);
                    else turnCW -= Math.sqrt(movementSpeed);
                    turnCW *= s ? -TURN_RATE : TURN_RATE;

                    setYRot(oldYRot + turnCW);
                }

                setDeltaMovement(
                        movement.add(getForward().scale(inline))
                );
            }
        } else if (movement.lengthSqr() == 0) { // Weird edge-case from standing on entities
            setDeltaMovement(
                    movement.add(RotationUtil.vecPlayerToWorld(new Vec3(0, -0.02, 0), gravity))
            );
        }

        if (level instanceof ServerLevel serverLevel) {
            for (int x = -2; x < 4; x++) {
                for (int y = 0; y < 3; y++) {
                    for (int z = -2; z < 4; z++) {
                        Vec3 offset = new Vec3(x, -0.1 * y, z);
                        offset = RotationUtil.vecPlayerToWorld(offset, gravity);
                        final BlockPos blockPos = blockPosition().offset(
                                Mth.floor(offset.x),
                                Mth.floor(offset.y),
                                Mth.floor(offset.z)
                        );

                        // JCraft.createParticle(serverLevel, blockPos.getX(), blockPos.getY(), blockPos.getZ(), y == 0 ? JParticleType.BACK_STAB : JParticleType.GO);

                        final BlockState state = serverLevel.getBlockState(blockPos);
                        if (flattenedBlockStates.containsKey(state))
                            serverLevel.setBlock(blockPos, flattenedBlockStates.get(state), Block.UPDATE_ALL);
                        else if (state.canBeReplaced()) {
                            serverLevel.setBlock(blockPos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
                            serverLevel.levelEvent(null, 2001, blockPos, Block.getId(state)); // Particles
                        }
                    }
                }
            }
        }

        setDeltaMovement(getDeltaMovement().scale(drag));
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        if (player.isSecondaryUseActive()) return InteractionResult.PASS;

        if (getFirstPassenger() == null) {
            if (!level().isClientSide()) {
                return player.startRiding(this) ? InteractionResult.CONSUME : InteractionResult.PASS;
            } else {
                return InteractionResult.SUCCESS;
            }
        }

        return InteractionResult.PASS;
    }

    @Override
    public void resetFallDistance() {
        final Level level = level();
        if (!level.isClientSide()) {
            if (fallDistance > 6.0) {
                final DamageSource ds = level().damageSources().cramming();
                final Set<LivingEntity> hurt = JUtils.generateHitbox(level(), position(), 3.5, Set.of(this));

                for (LivingEntity living : hurt) {
                    if (!JUtils.canDamage(ds, living)) {
                        continue;
                    }

                    final LivingEntity target = JUtils.getUserIfStand(living);
                    if (getOwner() != target) {
                        StandEntity.damageLogic(level(), target, target.position().subtract(position()), 25, 3,
                                false, 12.0f, false, 21, ds, getOwner(), CommonHitPropertyComponent.HitAnimation.CRUSH, false);
                    }
                }

                JComponentPlatformUtils.getShockwaveHandler(level).addShockwave(
                        position(),
                        Vec3.atLowerCornerOf(GravityChangerAPI.getGravityDirection(this).getNormal()),
                        4.5f
                        );

                final var poofPacket = new ClientboundLevelParticlesPacket(
                        ParticleTypes.POOF, false,
                        getX(), getY(), getZ(),
                        0.2f, 0.2f, 0.2f,
                        0.3f, 32
                );

                JUtils.tracking(this).forEach(
                        serverPlayer -> serverPlayer.connection.send(poofPacket)
                );

                playSound(SoundEvents.GENERIC_EXPLODE, 1.0f, 0.3f);
            }
        }
        super.resetFallDistance();
    }

    public double getPassengersRidingOffset() {
        return 1.0d;
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {}
    @Override
    public void addAdditionalSaveData(CompoundTag compound) {}

    // Animations
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "shake", 0, this::shakePredicate));
        controllers.add(new AnimationController<>(this, "movement", 5, this::movePredicate));
        controllers.add(new AnimationController<>(this, "steering", 5, this::steerPredicate));
        controllers.add(new AnimationController<>(this, "hit", 0, this::hitPredicate));
    }

    private static final RawAnimation NEUTRAL = RawAnimation.begin().thenLoop("steer_neutral");
    private static final RawAnimation LEFT = RawAnimation.begin().thenLoop("steer_left");
    private static final RawAnimation RIGHT = RawAnimation.begin().thenLoop("steer_right");
    private <T extends GeoAnimatable> PlayState steerPredicate(AnimationState<T> state) {
        if (steeringLeft()) return state.setAndContinue(LEFT);
        if (steeringRignt()) return state.setAndContinue(RIGHT);
        return state.setAndContinue(NEUTRAL);
    }

    private static final RawAnimation FORWARD = RawAnimation.begin().thenLoop("forward");
    private static final RawAnimation BACK = RawAnimation.begin().thenLoop("back");
    private <T extends GeoAnimatable> PlayState movePredicate(AnimationState<T> state) {
        if (movingForward()) return state.setAndContinue(FORWARD);
        if (movingBack()) return state.setAndContinue(BACK);
        return PlayState.STOP;
    }

    private static final RawAnimation SHAKE = RawAnimation.begin().thenLoop("shake");
    private <T extends GeoAnimatable> PlayState shakePredicate(AnimationState<T> state) {
        if (isVehicle()) return state.setAndContinue(SHAKE);
        return PlayState.STOP;
    }

    private static final RawAnimation HIT = RawAnimation.begin().thenLoop("hit");
    private <T extends GeoAnimatable> PlayState hitPredicate(AnimationState<T> state) {
        if (getHurtTime() > 0) return state.setAndContinue(HIT);
        return PlayState.STOP;
    }
}
