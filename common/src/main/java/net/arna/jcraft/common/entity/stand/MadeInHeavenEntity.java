package net.arna.jcraft.common.entity.stand;

import lombok.NonNull;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.attack.core.MoveMap;
import net.arna.jcraft.common.attack.core.MoveType;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.moves.base.AbstractMove;
import net.arna.jcraft.common.attack.moves.madeinheaven.*;
import net.arna.jcraft.common.attack.moves.shared.EffectInflictingAttack;
import net.arna.jcraft.common.attack.moves.shared.KnockdownAttack;
import net.arna.jcraft.common.attack.moves.shared.MainBarrageAttack;
import net.arna.jcraft.common.attack.moves.shared.SimpleAttack;
import net.arna.jcraft.common.component.living.CommonCooldownsComponent;
import net.arna.jcraft.common.component.living.CommonHitPropertyComponent;
import net.arna.jcraft.common.config.JServerConfig;
import net.arna.jcraft.common.network.s2c.TimeAccelStatePacket;
import net.arna.jcraft.common.util.CooldownType;
import net.arna.jcraft.common.util.JParticleType;
import net.arna.jcraft.common.util.StandAnimationState;
import net.arna.jcraft.platform.JComponentPlatformUtils;
import net.arna.jcraft.registry.JSoundRegistry;
import net.arna.jcraft.registry.JStatusRegistry;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import mod.azure.azurelib.core.animation.AnimationState;
import mod.azure.azurelib.core.animation.RawAnimation;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class MadeInHeavenEntity extends StandEntity<MadeInHeavenEntity, MadeInHeavenEntity.State> {
    public static final EffectInflictingAttack<MadeInHeavenEntity> SPEED_CHOP = new EffectInflictingAttack<MadeInHeavenEntity>(
            JCraft.LIGHT_COOLDOWN, 6, 11, 0.75f, 3f, 8, 1.5f, 0.5f, -0.1f,
            List.of(new MobEffectInstance(JStatusRegistry.BLEEDING.get(), 80, 1, true, false, true)))
            .withAnim(State.SPEED_CHOP)
            .withImpactSound(SoundEvents.TRIDENT_HIT)
            .withAction(MadeInHeavenEntity::tryIncrementSpeedometer)
            .withHitAnimation(CommonHitPropertyComponent.HitAnimation.HIGH)
            .withInfo(
                    Component.literal("Speed Chop"),
                    Component.literal("tiny stun, procs bleed")
            );
    public static final SimpleAttack<MadeInHeavenEntity> LIGHT_FOLLOWUP = new SimpleAttack<MadeInHeavenEntity>(
            0, 6, 12, 0.75f, 5, 8, 1.5f, 1f, -0.1f)
            .withAnim(State.LIGHT_FOLLOWUP)
            .withImpactSound(JSoundRegistry.IMPACT_1.get())
            .withLaunch()
            .withBlockStun(4)
            .withExtraHitBox(0, 0.25, 1)
            .withAction(MadeInHeavenEntity::tryIncrementSpeedometer)
            .withInfo(
                    Component.literal("Kick"),
                    Component.literal("quick combo finisher")
            );
    public static final SimpleAttack<MadeInHeavenEntity> SLICE = new SimpleAttack<MadeInHeavenEntity>(JCraft.LIGHT_COOLDOWN,
            5, 8, 0.75f, 4f, 10, 1.5f, 0.15f, -0.1f)
            .withFollowup(LIGHT_FOLLOWUP)
            .withCrouchingVariant(SPEED_CHOP)
            .withImpactSound(SoundEvents.TRIDENT_HIT)
            .withAction(MadeInHeavenEntity::tryIncrementSpeedometer)
            .withInfo(
                    Component.literal("Slice"),
                    Component.literal("quick combo starter")
            );
    public static final SimpleAttack<MadeInHeavenEntity> BARRAGE_FINISHER = new SimpleAttack<MadeInHeavenEntity>(0,
            6, 9, 0.85f, 1f, 10, 1.5f, 1.1f, 0f)
            .withAction(MadeInHeavenEntity::tryIncrementSpeedometer)
            .withHitSpark(JParticleType.HIT_SPARK_2)
            .withLaunch()
            .withInfo(
                    Component.literal("Barrage (Final Hit)"),
                    Component.empty()
            );
    public static final MainBarrageAttack<MadeInHeavenEntity> BARRAGE = new MainBarrageAttack<MadeInHeavenEntity>(200,
            0, 32, 0.85f, 1f, 10, 2f, 0.1f, 0f, 2, Blocks.OAK_PLANKS.defaultDestroyTime())
            .withFinisher(23, BARRAGE_FINISHER)
            .withAction(MadeInHeavenEntity::tryIncrementSpeedometer)
            .withSound(JSoundRegistry.MIH_BARRAGE.get())
            .withImpactSound(JSoundRegistry.IMPACT_1.get())
            .withInfo(
                    Component.literal("Barrage"),
                    Component.literal("short, knocks back")
            );
    public static final SpeedSliceAttack SPEED_SLICE = new SpeedSliceAttack(300, 10, 11,
            1.25f, 6f, 1.5f, 1f)
            .withAction(MadeInHeavenEntity::tryIncrementSpeedometer)
            .withSound(JSoundRegistry.MIH_SPEEDSLICE.get())
            .withInfo(
                    Component.literal("Speed Slice"),
                    Component.literal("short windup, harming teleport with hitstun and light knockback")
            );
    public static final KnockdownAttack<MadeInHeavenEntity> LEG_CRUSHER = new KnockdownAttack<MadeInHeavenEntity>(
            80, 9, 19, 0.85f, 7f, 22, 1.5f, 0.35f, 0.2f, 45)
            .withAction(MadeInHeavenEntity::tryIncrementSpeedometer)
            .withSound(JSoundRegistry.MIH_LEGCRUSHER.get())
            .withImpactSound(JSoundRegistry.TW_KICK_HIT.get())
            .withExtraHitBox(0, -0.5, 1)
            .withHitAnimation(CommonHitPropertyComponent.HitAnimation.LOW)
            .withHitSpark(JParticleType.HIT_SPARK_2)
            .withInfo(
                    Component.literal("Leg Crusher"),
                    Component.literal("knocks down (2s)")
            );
    public static final SimpleAttack<MadeInHeavenEntity> LOW_KICK = new SimpleAttack<MadeInHeavenEntity>(80,
            8, 17, 0.85f, 6f, 26, 1.5f, 0.25f, 0.2f)
            .withAction(MadeInHeavenEntity::tryIncrementSpeedometer)
            .withCrouchingVariant(LEG_CRUSHER)
            .withSound(JSoundRegistry.MIH_LEGCRUSHER.get())
            .withImpactSound(JSoundRegistry.IMPACT_1.get())
            .withExtraHitBox(0, -0.5, 1)
            .withHitAnimation(CommonHitPropertyComponent.HitAnimation.LOW)
            .withHitSpark(JParticleType.HIT_SPARK_2)
            .withInfo(
                    Component.literal("Low Kick"),
                    Component.literal("combo starter/extender, mih hoofs the enemies legs in a quick, stunning attack")
            );
    public static final FuryChopAttack FURY_CHOP = new FuryChopAttack(200, 15, 24, 0.85f,
            7f, 20, 1.6f, 0.25f, 0.2f)
            .withSound(JSoundRegistry.MIH_FURYCHOP.get())
            .withImpactSound(JSoundRegistry.IMPACT_2.get())
            .withAction(MadeInHeavenEntity::tryIncrementSpeedometer)
            .withHitAnimation(CommonHitPropertyComponent.HitAnimation.HIGH)
            .withHitSpark(JParticleType.HIT_SPARK_2)
            .withInfo(
                    Component.literal("Fury Chop"),
                    Component.literal("combo extender, on hit gives haste(8s) to user and mining fatigue(8s) to victim, on whiff the fatigue goes to user")
            );
    public static final SimpleAttack<MadeInHeavenEntity> DONUT = new SimpleAttack<MadeInHeavenEntity>(200,
            26, 32, 0.75f, 8.5f, 40, 2f, -0.2f, 0.2f)
            .withSound(JSoundRegistry.STAND_DESUMMON.get())
            .withImpactSound(JSoundRegistry.IMPACT_7.get())
            .withAction(MadeInHeavenEntity::tryIncrementSpeedometer)
            .withHyperArmor()
            .withBlockStun(4)
            .withHitSpark(JParticleType.HIT_SPARK_3)
            .withHitAnimation(CommonHitPropertyComponent.HitAnimation.CRUSH)
            .withInfo(
                    Component.literal("Roundabout Donut"),
                    Component.literal("feigns stand desummon, uninterruptible combo starter")
            );
    public static final TimeAccelerationMove TIME_ACCELERATION = new TimeAccelerationMove(1400, 20,
            40, 1f, JServerConfig.MIH_TIME_ACCELERATION_DURATION::getValue)
            .withSound(JSoundRegistry.MIH_TACCEL.get())
            .withAction((attacker, user, ctx, targets) -> attacker.speedometer = 0) // Clear speedometer
            .withInfo(
                    Component.literal("Time Acceleration"),
                    Component.literal("""
                            allows charging the speedometer for 30s
                            it is charged by landing hits
                            the speedometer impacts the level of speed and haste granted by Time Acceleration
                            if the speedometer is full and the charging period finishes, enemies become standless for 15s"""));
    public static final CircleAttack CIRCLE = new CircleAttack(400, 13, 14, 1.25f)
            .withSound(JSoundRegistry.MIH_CIRCLE.get())
            .withAction(MadeInHeavenEntity::tryIncrementSpeedometer)
            .withInfo(
                    Component.literal("Heaven's Judgement"),
                    Component.literal("rapidly circles a looked-at target within 4m at a radius of 7m")
            );

    public static final JudgementAttack JUDGEMENT = new JudgementAttack(300, 20, 60, 1.25f, 2)
            .withCrouchingVariant(CIRCLE)
            .withAction(MadeInHeavenEntity::tryIncrementSpeedometer)
            .withSound(JSoundRegistry.MIH_JUDGEMENT.get())
            .withInfo(
                    Component.literal("Divine Severance"),
                    Component.literal("Made in Heaven rapidly speed slices an area, then finishes with a large, launching slice")
            );
    private static final EntityDataAccessor<Integer> ACCEL_TIME;
    private static final EntityDataAccessor<Integer> SPEEDOMETER;
    private static final EntityDataAccessor<Boolean> AFTER_IMAGE;
    private static final EntityDataAccessor<Integer> CIRCLING_TARGET;

    public static final int MAXIMUM_SPEEDOMETER = 30;

    private int speedometer = 0;

    static {
        ACCEL_TIME = SynchedEntityData.defineId(MadeInHeavenEntity.class, EntityDataSerializers.INT);
        SPEEDOMETER = SynchedEntityData.defineId(MadeInHeavenEntity.class, EntityDataSerializers.INT);
        AFTER_IMAGE = SynchedEntityData.defineId(MadeInHeavenEntity.class, EntityDataSerializers.BOOLEAN);
        CIRCLING_TARGET = SynchedEntityData.defineId(MadeInHeavenEntity.class, EntityDataSerializers.INT);
    }

    public MadeInHeavenEntity(Level worldIn) {
        super(StandType.MADE_IN_HEAVEN, worldIn, JSoundRegistry.MIH_SUMMON.get());
        idleRotation = -45f;

        proCount = 4;
        conCount = 2;

        freespace =
                """
                        PASSIVE: Speed I
                                        
                        BNBs:
                            -the flashbang
                            (Donut>Light>)Speed Slice>Low Kick>Fury Chop>Light>Barrage>dash>Light~Light
                            
                            -""";

        auraColors = new Vector3f[]{
                new Vector3f(0.9f, 0.8f, 0.8f),
                new Vector3f(1.0f, 0.0f, 0.0f),
                new Vector3f(0.0f, 0.0f, 0.0f),
                new Vector3f(0.5f, 0.0f, 1.0f)
        };
    }

    @Override
    protected void registerMoves(MoveMap<MadeInHeavenEntity, State> moves) {
        moves.registerImmediate(MoveType.LIGHT, SLICE, State.SLICE);

        moves.register(MoveType.HEAVY, DONUT, State.DONUT);
        moves.register(MoveType.BARRAGE, BARRAGE, State.BARRAGE);

        moves.register(MoveType.SPECIAL1, LOW_KICK, State.LOW_KICK).withCrouchingVariant(State.LEG_CRUSHER);
        moves.register(MoveType.SPECIAL2, FURY_CHOP, State.FURY_CHOP);
        moves.register(MoveType.SPECIAL3, JUDGEMENT, State.JUDGEMENT).withCrouchingVariant(State.CIRCLE_STARTUP);
        moves.register(MoveType.ULTIMATE, TIME_ACCELERATION, State.TIME_ACCELERATION);

        moves.register(MoveType.UTILITY, SPEED_SLICE, State.SPEED_SLICE);
    }

    @Override
    public boolean initMove(MoveType type) {
        if (!tryFollowUp(type, MoveType.LIGHT)) {
            return super.initMove(type);
        }

        return true;
    }

    public int getAccelTime() {
        return entityData.get(ACCEL_TIME);
    }

    public void setAccelTime(int aTime) {
        entityData.set(ACCEL_TIME, aTime);
    }

    public int getSpeedometer() {
        return entityData.get(SPEEDOMETER);
    }

    public void incrementSpeedometer() {
        if (speedometer >= MAXIMUM_SPEEDOMETER) {
            return;
        }

        speedometer++;
        //JCraft.LOGGER.info("Speedometer increased to: " + speedometer);
    }

    /**
     * Tracks the speedometer value every tick, for actual addition see incrementSpeedometer()
     */
    public void setSpeedometer(int speedometer) {
        entityData.set(SPEEDOMETER, speedometer);
    }

    public boolean getAfterimage() {
        return entityData.get(AFTER_IMAGE);
    }

    public void setAfterimage(boolean a) {
        entityData.set(AFTER_IMAGE, a);
    }

    public LivingEntity getCircleTarget() {
        return level().getEntity(entityData.get(CIRCLING_TARGET)) instanceof LivingEntity entity ? entity : null;
    }

    public void setCirclingTarget(LivingEntity target) {
        entityData.set(CIRCLING_TARGET, target == null ? -1 : target.getId());
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        getEntityData().define(ACCEL_TIME, 0);
        getEntityData().define(SPEEDOMETER, 0);
        getEntityData().define(AFTER_IMAGE, false);
        getEntityData().define(CIRCLING_TARGET, -1);
    }

    @Override
    public boolean handleMove(AbstractMove<?, ? super MadeInHeavenEntity> move, CooldownType cooldownType, State animState) {
        if (!move.canBeInitiated(this)) {
            return false;
        }
        LivingEntity player = getUserOrThrow();

        CommonCooldownsComponent cooldowns = JComponentPlatformUtils.getCooldowns(player);
        int cooldown = cooldowns.getCooldown(cooldownType);

        if (cooldown > 0) {
            return false;
        }

        int cdDiv = getAccelTime() > 0 ? 2 : 1;
        cooldowns.setCooldown(cooldownType, move.getCooldown() / cdDiv);

        setMove(move, animState);
        return true;
    }

    private static void tryIncrementSpeedometer(MadeInHeavenEntity attacker, LivingEntity user, MoveContext ctx, Set<LivingEntity> targets) {
        if (attacker.getAccelTime() > 0 && !targets.isEmpty()) {
            attacker.incrementSpeedometer();
        }
    }

    @Override
    public void desummon() {
        if (!level().isClientSide() && getAccelTime() > 0) {
            TimeAccelStatePacket.sendStop(getServer().getPlayerList(), this);
        }
        super.desummon();
    }

    @Override
    public void tick() {
        super.tick();

        if (!hasUser()) {
            return;
        }
        final LivingEntity user = getUserOrThrow();
        final int aTime = getAccelTime();

        // Circling
        CIRCLE.tickCircle(this);

        // Time Accel handling
        TIME_ACCELERATION.tickTimeAcceleration(this);

        if (!user.hasEffect(JStatusRegistry.DAZED.get())) {
            if (aTime > 0) {
                int amplifier = speedometer / 3;
                user.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 20, amplifier, true, false));
                user.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 20, amplifier, true, false));
            } else {
                user.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 40, 0, true, false));
            }
        }

        if (level().isClientSide) {
            final Entity clientCircleTarget = getCircleTarget();
            if (clientCircleTarget != null) {
                lookAtWithoutReset(user, EntityAnchorArgument.Anchor.EYES, clientCircleTarget.getEyePosition());
            }

            if (getAccelTime() > 1) { // Updating on the client, to make sure all is smooth
                CircleAttack.createSpeedParticles(this, this);

                final List<Entity> toCatch = level().getEntitiesOfClass(Entity.class, // Lower range by 32 to reduce lag
                        getBoundingBox().inflate(96), EntitySelector.NO_CREATIVE_OR_SPECTATOR);

                for (Entity entity : toCatch) {
                    if (entity instanceof LivingEntity) {
                        continue;
                    }
                    if (entity.position().distanceToSqr(new Vec3(entity.xo, entity.yo, entity.zo)) > 0) {
                        CircleAttack.createSpeedParticles(this, entity);
                    }
                    entity.tick();
                }
            }

            return;
        }

        // Tracking
        setSpeedometer(speedometer);
    }

    // Copied from Entity#lookAt(EntityAnchor, Vec3d), but doesn't set prevYaw, prevPitch and prevHeadYaw to get rid of jitter.
    private static void lookAtWithoutReset(LivingEntity entity, EntityAnchorArgument.Anchor anchorPoint, Vec3 target) {
        entity.yRotO = entity.getYRot();
        entity.yBodyRotO = entity.getVisualRotationYInDegrees();
        entity.yHeadRotO = entity.getYHeadRot();
        entity.xRotO = entity.getXRot();

        Vec3 vec3d = anchorPoint.apply(entity);
        double d = target.x - vec3d.x;
        double e = target.y - vec3d.y;
        double f = target.z - vec3d.z;
        double g = Math.sqrt(d * d + f * f);
        entity.setXRot(Mth.wrapDegrees((float) (-(Mth.atan2(e, g) * 57.2957763671875))));
        entity.setYRot(Mth.wrapDegrees((float) (Mth.atan2(f, d) * 57.2957763671875) - 90.0f));
        entity.setYHeadRot(entity.getYRot());
    }

    @Override
    @NonNull
    public MadeInHeavenEntity getThis() {
        return this;
    }

    // Animation code
    public enum State implements StandAnimationState<MadeInHeavenEntity> {
        IDLE(builder -> builder.setAnimation(RawAnimation.begin().thenLoop("animation.mih.idle"))),
        SLICE(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.mih.slice"))),
        BLOCK(builder -> builder.setAnimation(RawAnimation.begin().thenLoop("animation.mih.block"))),
        DONUT(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.mih.donut"))),
        BARRAGE(builder -> builder.setAnimation(RawAnimation.begin().thenLoop("animation.mih.barrage"))),
        SPEED_SLICE(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.mih.speedslice"))),
        JUDGEMENT(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.mih.judgement"))),
        LEG_CRUSHER(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.mih.legcrusher"))),
        FURY_CHOP(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.mih.furychop"))),
        TIME_ACCELERATION(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.mih.taccel"))),
        CIRCLE_STARTUP(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.mih.circlestartup"))),
        SPEED_CHOP(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.mih.speedchop"))),
        LIGHT_FOLLOWUP(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.mih.light_followup"))),
        LOW_KICK(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.mih.lowkick")));

        private final Consumer<AnimationState<MadeInHeavenEntity>> animator;

        State(Consumer<AnimationState<MadeInHeavenEntity>> animator) {
            this.animator = animator;
        }

        @Override
        public void playAnimation(MadeInHeavenEntity attacker, AnimationState<MadeInHeavenEntity> builder) {
            animator.accept(builder);
        }
    }

    @Override
    protected State[] getStateValues() {
        return State.values();
    }

    @Override
    protected @Nullable String getSummonAnimation() {
        return "animation.mih.summon";
    }

    @Override
    public State getBlockState() {
        return State.BLOCK;
    }
}
