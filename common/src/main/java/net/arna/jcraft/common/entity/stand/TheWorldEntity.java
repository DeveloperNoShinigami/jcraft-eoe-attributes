package net.arna.jcraft.common.entity.stand;

import lombok.NonNull;
import mod.azure.azurelib.core.animation.AnimationState;
import mod.azure.azurelib.core.animation.RawAnimation;
import net.arna.jcraft.common.attack.core.MoveMap;
import net.arna.jcraft.common.attack.core.MoveType;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.moves.shared.*;
import net.arna.jcraft.common.attack.moves.theworld.FeignBarrageCounterAttack;
import net.arna.jcraft.common.attack.moves.theworld.TWDonutAttack;
import net.arna.jcraft.common.component.living.CommonHitPropertyComponent;
import net.arna.jcraft.common.config.JServerConfig;
import net.arna.jcraft.common.util.JParticleType;
import net.arna.jcraft.common.util.JUtils;
import net.arna.jcraft.common.util.StandAnimationState;
import net.arna.jcraft.registry.JSoundRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.function.Consumer;

public final class TheWorldEntity extends AbstractTheWorldEntity<TheWorldEntity, TheWorldEntity.State> {
    public static final SimpleAttack<TheWorldEntity> LOW_KICK = new SimpleAttack<TheWorldEntity>(20, 8, 14, 0.75f,
            6f, 17, 1.5f, 0.2f, 0.65f)
            .withAnim(State.LOW)
            .withImpactSound(JSoundRegistry.IMPACT_1.get())
            .withExtraHitBox(0, 0, 1)
            .withHitSpark(JParticleType.HIT_SPARK_2)
            .withHitAnimation(CommonHitPropertyComponent.HitAnimation.LOW)
            .withInfo(
                    Component.literal("Low Kick"),
                    Component.literal("slower, higher stun, low hitbox")
            );
    public static final SimpleAttack<TheWorldEntity> LIGHT_FOLLOWUP = new SimpleAttack<TheWorldEntity>(
            0, 7, 11, 0.75f, 6f, 8, 1.5f, 1f, 0)
            .withAnim(State.LIGHT_FOLLOWUP)
            .withImpactSound(JSoundRegistry.IMPACT_1.get())
            .withHitSpark(JParticleType.HIT_SPARK_2)
            .withLaunch()
            .withBlockStun(4)
            .withExtraHitBox(0, 0, 1)
            .withInfo(
                    Component.literal("Punch"),
                    Component.literal("quick combo finisher")
            );
    public static final SimpleAttack<TheWorldEntity> LIGHT = SimpleAttack.<TheWorldEntity>lightAttack(
                    5, 7, 0.75f, 5, 10, 0.1f, -0.1f)
            .withImpactSound(JSoundRegistry.IMPACT_1.get())
            .withFollowup(LIGHT_FOLLOWUP)
            .withCrouchingVariant(LOW_KICK)
            .withInfo(
                    Component.literal("Punch"),
                    Component.literal("quick combo starter")
            );
    public static final MainBarrageAttack<TheWorldEntity> BARRAGE = new MainBarrageAttack<TheWorldEntity>(280,
            0, 40, 0.75f, 1f, 30, 2, 0.25f, 0, 3, Blocks.OBSIDIAN.defaultDestroyTime())
            .withSound(JSoundRegistry.TW_BARRAGE.get())
            .withInfo(
                    Component.literal("Barrage"),
                    Component.literal("fast reliable combo starter/extender, high stun")
            );
    public static final SimpleAttack<TheWorldEntity> SWEEP = new SimpleAttack<TheWorldEntity>(40, 6, 16, 0.75f, 5f,
            16, 1.85f, 0.5f, 0.4f)
            .withSound(JSoundRegistry.TW_KICK.get())
            .withImpactSound(JSoundRegistry.TW_KICK_HIT.get())
            .withHitAnimation(CommonHitPropertyComponent.HitAnimation.LOW)
            .withHitSpark(JParticleType.HIT_SPARK_2)
            .withInfo(
                    Component.literal("Sweep"),
                    Component.literal("fast, decent stun")
            );
    public static final UppercutAttack<TheWorldEntity> ROUNDHOUSE = new UppercutAttack<TheWorldEntity>(20, 7, 13, 0.75f, 5f,
            10, 1.75f, 0.5f, -0.2f, 0.4f)
            .withCrouchingVariant(SWEEP)
            .withSound(JSoundRegistry.TW_KICK.get())
            .withImpactSound(JSoundRegistry.TW_KICK_HIT.get())
            .withHitAnimation(CommonHitPropertyComponent.HitAnimation.HIGH)
            .withHitSpark(JParticleType.HIT_SPARK_2)
            .withInfo(
                    Component.literal("Roundhouse"),
                    Component.literal("low stun")
            );
    public static final KnockdownAttack<TheWorldEntity> COUNTER_FOLLOWUP = new KnockdownAttack<TheWorldEntity>(0, 5, 9, 0.75f, 9f, 16, 1.75f, 0.7f, 0.1f, 35)
            .withSound(JSoundRegistry.TW_COUNTER.get())
            .withImpactSound(JSoundRegistry.IMPACT_4.get())
            .withExtraHitBox(1.25)
            .withLaunch()
            .withHyperArmor()
            .withInfo(
                    Component.literal("Counter (Hit)"),
                    Component.literal("quick, armored knockdown")
            );
    public static final FeignBarrageCounterAttack FEIGN_BARRAGE = new FeignBarrageCounterAttack(400, 5,
            50, 0.75f, COUNTER_FOLLOWUP)
            .withSound(JSoundRegistry.TW_BARRAGE.get())
            .withInfo(
                    Component.literal("Feign Barrage"),
                    Component.literal("counter, 0.25s windup, 2.25s duration, teleports and knocks down on hit")
            );
    public static final TWDonutAttack DONUT = new TWDonutAttack(220, 20, 42, 1f,
            9f, 52, 2f, 1f, 0f)
            .withSound(JSoundRegistry.TW_DONUT.get())
            .withImpactSound(JSoundRegistry.TW_DONUT_HIT.get())
            .withExtraHitBox(1.5)
            .withHyperArmor()
            .withLaunch()
            .withInfo(
                    Component.literal("Donut"),
                    Component.literal("slow, uninterruptible combo starter/extender, 1.5s stun on whiff")
            );
    public static final TimeSkipMove<TheWorldEntity> TIME_SKIP = new TimeSkipMove<TheWorldEntity>(300, 14)
            .withSound(JSoundRegistry.TIME_SKIP.get())
            .withInfo(
                    Component.literal("Timeskip"),
                    Component.literal("14m range")
            );
    public static final SimpleAttack<TheWorldEntity> LUNGE = new SimpleAttack<TheWorldEntity>(160, 9, 14,
            1f, 5f, 12, 1.5f, 0.6f, 0.2f)
            .withExtraHitBox(1)
            .withInitAction(TheWorldEntity::doCharge)
            .withSound(JSoundRegistry.TW_KICK.get())
            .withImpactSound(JSoundRegistry.TW_KICK_HIT.get())
            .withHitSpark(JParticleType.HIT_SPARK_2)
            .withLaunch()
            .withInfo(
                    Component.literal("Lunge"),
                    Component.literal("user & stand charge forward, launches")
            );

    private static void doCharge(TheWorldEntity attacker, LivingEntity user, MoveContext moveContext) {
        if (attacker.isFree()) {
            return;
        }
        JUtils.addVelocity(user, attacker.getLookAngle().scale(0.75));
    }

    public static final ChargeAttack<TheWorldEntity, State> CHARGE = new ChargeAttack<>(
            280, 5, 19, 7.5f, 5f, 20, 1.5f, 0.25f, 0, State.CHARGE_HIT)
            .withCrouchingVariant(LUNGE)
            .withSound(JSoundRegistry.TW_CHARGE.get())
            .withImpactSound(JSoundRegistry.TW_CHARGE_HIT.get())
            .withHitSpark(JParticleType.HIT_SPARK_2)
            .withHitAnimation(CommonHitPropertyComponent.HitAnimation.CRUSH)
            .withBlockStun(11)
            .withInfo(
                    Component.literal("Forward Charge"),
                    Component.literal("The World detaches from the user and lunges forward, combo starter")
            );
    public static final TimeStopMove<TheWorldEntity> TIME_STOP = new TimeStopMove<TheWorldEntity>(1400,
            45, 52, JServerConfig.TW_TIME_STOP_DURATION::getValue)
            .withSound(JSoundRegistry.TW_TS.get())
            .withInfo(
                    Component.literal("Timestop"),
                    Component.literal("4 seconds")
            );

    public TheWorldEntity(Level worldIn) {
        super(StandType.THE_WORLD, worldIn, JSoundRegistry.TW_SUMMON.get());

        proCount = 4;
        conCount = 2;

        freespace =
                """
                        BNBs:
                            -the sauce boss
                            (Light>)Charge>cr.Light>Roundhouse>Barrage>Light>Donut>Roundhouse>Light~Light
                            
                            -the afternoon coffee
                            Donut>Roundhouse>Charge>Light>Barrage>Roundhouse>Light~Light""";

        auraColors = new Vector3f[]{
                new Vector3f(1.0f, 0.7f, 0.3f),
                new Vector3f(1.0f, 0f, 0f),
                new Vector3f(1.0f, 0.6f, 0.0f),
                new Vector3f(0.7f, 0.3f, 1.0f)
        };
    }

    @Override
    protected void registerMoves(MoveMap<TheWorldEntity, State> moves) {
        moves.registerImmediate(MoveType.LIGHT, LIGHT, State.LIGHT);

        moves.register(MoveType.HEAVY, DONUT, State.DONUT);
        moves.register(MoveType.BARRAGE, BARRAGE, State.BARRAGE);

        moves.register(MoveType.SPECIAL1, ROUNDHOUSE, State.ROUNDHOUSE).withCrouchingVariant(State.SWEEP);
        moves.register(MoveType.SPECIAL2, CHARGE, State.CHARGE).withCrouchingVariant(State.LUNGE);
        moves.register(MoveType.SPECIAL3, FEIGN_BARRAGE, State.BARRAGE);
        moves.register(MoveType.ULTIMATE, TIME_STOP, State.TIME_STOP);

        moves.register(MoveType.UTILITY, TIME_SKIP, State.IDLE);
    }

    @Override
    public boolean initMove(MoveType type) {
        if (tryFollowUp(type, MoveType.LIGHT)) {
            return true;
        } else {
            return super.initMove(type);
        }
    }

    @Override
    public void setAttackRotationOffset() {
        // Prevents The World from going in front of the user while the Feign Barrage isn't active
        if (getCurrentMove() != null && getCurrentMove().getOriginalMove() == FEIGN_BARRAGE && getMoveStun() > FEIGN_BARRAGE.getDuration() - FEIGN_BARRAGE.getWindup()) {
            setRotationOffset(idleRotation);
            return;
        }
        super.setAttackRotationOffset();
    }

    @Override
    protected void playSummonSound() {
        if (shouldNotPlaySummonSound()) {
            return;
        }

        playSound(JSoundRegistry.TW_SUMMON.get(), 1f, 1f);
        playSound(JSoundRegistry.MUDA_DA.get(), 1f, 1f);
    }

    @Override
    @NonNull
    public TheWorldEntity getThis() {
        return this;
    }

    // Animation code
    public enum State implements StandAnimationState<TheWorldEntity> {
        IDLE(builder -> builder.setAnimation(RawAnimation.begin().thenLoop("animation.theworld.idle"))),
        LIGHT(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.theworld.light"))),
        BLOCK(builder -> builder.setAnimation(RawAnimation.begin().thenLoop("animation.theworld.block"))),
        DONUT(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.theworld.donut"))),
        BARRAGE(builder -> builder.setAnimation(RawAnimation.begin().thenLoop("animation.theworld.barrage"))),
        TIME_STOP(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.theworld.timestop"))),
        CHARGE(builder -> builder.setAnimation(RawAnimation.begin().thenLoop("animation.theworld.charge"))),
        CHARGE_HIT(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.theworld.charge_hit"))),
        ROUNDHOUSE(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.theworld.roundhouse"))),
        SWEEP(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.theworld.sweep"))),
        COUNTER_HIT(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.theworld.counter_hit"))),
        COUNTER_MISS(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.theworld.counter_miss"))),
        LOW(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.theworld.low"))),
        TIMESKIP(builder -> builder.setAnimation(RawAnimation.begin().thenLoop("animation.theworld.idle"))),
        LIGHT_FOLLOWUP(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.theworld.light_followup"))),
        LUNGE(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.theworld.lunge")));

        private final Consumer<AnimationState<TheWorldEntity>> animator;

        State(Consumer<AnimationState<TheWorldEntity>> animator) {
            this.animator = animator;
        }

        @Override
        public void playAnimation(TheWorldEntity attacker, AnimationState<TheWorldEntity> builder) {
            animator.accept(builder);
        }
    }

    @Override
    protected State[] getStateValues() {
        return State.values();
    }

    @Override
    protected @Nullable String getSummonAnimation() {
        return "animation.theworld.summon";
    }

    @Override
    public State getBlockState() {
        return State.BLOCK;
    }
}
