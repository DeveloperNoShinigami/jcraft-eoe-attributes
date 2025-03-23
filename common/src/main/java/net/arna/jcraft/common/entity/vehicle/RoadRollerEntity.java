package net.arna.jcraft.common.entity.vehicle;

import mod.azure.azurelib.core.animatable.GeoAnimatable;
import mod.azure.azurelib.core.animation.AnimatableManager;
import mod.azure.azurelib.core.animation.AnimationController;
import mod.azure.azurelib.core.animation.AnimationState;
import mod.azure.azurelib.core.animation.RawAnimation;
import mod.azure.azurelib.core.object.PlayState;
import net.arna.jcraft.common.gravity.api.GravityChangerAPI;
import net.arna.jcraft.common.gravity.util.RotationUtil;
import net.arna.jcraft.common.util.JUtils;
import net.arna.jcraft.registry.JEntityTypeRegistry;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class RoadRollerEntity extends AbstractGroundVehicleEntity {
    public RoadRollerEntity(Level level) {
        super(JEntityTypeRegistry.ROAD_ROLLER.get(), level);
    }

    private static final double INLINE_SPEED = 0.1d, TURN_RATE = 5.0d;
    @Override
    public void movementTick(boolean w, boolean a, boolean s, boolean d, boolean space, boolean sneak) {
        double drag = 0.99;

        oldYRot = getYRot();

        if (onGround()) {
            drag = getGroundFriction();
            if (w || s) {
                double inline;
                if (w) inline = INLINE_SPEED;
                else inline = -INLINE_SPEED;

                if (a || d) {
                    double movementSpeed = getDeltaMovement().length(); // blocks per tick
                    if (Double.isNaN(movementSpeed)) movementSpeed = 0.0d;

                    float turnCW = 0.0f;
                    if (d) turnCW += Math.sqrt(movementSpeed);
                    else turnCW -= Math.sqrt(movementSpeed);
                    turnCW *= s ? -TURN_RATE : TURN_RATE;

                    setYRot(oldYRot + turnCW);
                }

                addDeltaMovement(getForward().scale(inline));
            }
        } else {
            final double gravity = isNoGravity() ? 0.0 : -0.04;
            addDeltaMovement(new Vec3(0, gravity, 0));
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
        if (fallDistance > 6.0) {
            // stomp
            final Level level = level();
            if (level.isClientSide()) {
                final Vec3 pos = position();
                final Direction gravity = GravityChangerAPI.getGravityDirection(this);
                for (int i = 0; i < 360; i++) {
                    final Vec3 dir = RotationUtil.vecPlayerToWorld(Math.sin(JUtils.DEG_TO_RAD * i), 0, Math.cos(JUtils.DEG_TO_RAD * i), gravity);
                    level.addParticle(ParticleTypes.POOF, pos.x, pos.y, pos.z, dir.x, dir.y, dir.z);
                }

                playSound(SoundEvents.GENERIC_EXPLODE, 1.0f, 0.3f);
            } else {
                // damage + hitbox
            }
        }

        super.resetFallDistance();
    }

    public double getPassengersRidingOffset() {
        return 1.0d;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {}
    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {}

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
