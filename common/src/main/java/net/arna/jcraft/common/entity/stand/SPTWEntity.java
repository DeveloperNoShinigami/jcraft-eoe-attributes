package net.arna.jcraft.common.entity.stand;

import com.mojang.datafixers.util.Either;
import lombok.NonNull;
import mod.azure.azurelib.core.animation.AnimationState;
import mod.azure.azurelib.core.animation.RawAnimation;
import net.arna.jcraft.api.StandData;
import net.arna.jcraft.api.StandInfo;
import net.arna.jcraft.api.SummonData;
import net.arna.jcraft.api.attack.MoveSet;
import net.arna.jcraft.api.attack.MoveSetManager;
import net.arna.jcraft.api.attack.StateContainer;
import net.arna.jcraft.common.attack.actions.EffectAction;
import net.arna.jcraft.common.attack.core.MoveClass;
import net.arna.jcraft.common.attack.core.MoveMap;
import net.arna.jcraft.common.attack.moves.shared.*;
import net.arna.jcraft.common.attack.moves.starplatinum.theworld.SPTWGroundSlamAttack;
import net.arna.jcraft.common.attack.moves.starplatinum.theworld.TimeStrikeAttack;
import net.arna.jcraft.common.component.living.CommonHitPropertyComponent;
import net.arna.jcraft.common.config.JServerConfig;
import net.arna.jcraft.common.util.JParticleType;
import net.arna.jcraft.common.util.StandAnimationState;
import net.arna.jcraft.registry.JSoundRegistry;
import net.arna.jcraft.registry.JStandTypeRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import org.joml.Vector3f;

import java.util.function.Consumer;

/**
 * The {@link StandEntity} for <a href="https://jojowiki.com/Star_Platinum">Star Platinum The World</a>.
 * @see JStandTypeRegistry#STAR_PLATINUM_THE_WORLD
 * @see net.arna.jcraft.client.model.entity.stand.StarPlatinumModel StarPlatinumModel
 * @see net.arna.jcraft.client.renderer.entity.stands.SPTWRenderer SPTWRenderer
 * @see SPTWGroundSlamAttack
 */
public final class SPTWEntity extends AbstractStarPlatinumEntity<SPTWEntity, SPTWEntity.State> {
    public static final MoveSet<SPTWEntity, State> MOVE_SET = MoveSetManager.create(JStandTypeRegistry.STAR_PLATINUM_THE_WORLD,
            SPTWEntity::registerMoves, State.class);
    public static final StandData DATA = StandData.builder()
            .idleRotation(315f)
            .evolution(true)
            .info(StandInfo.builder()
                    .name(Component.translatable("entity.jcraft.sptw"))
                    .proCount(4)
                    .conCount(2)
                    .freeSpace(Component.literal("""
                        BNBs:
                            -the superman
                            Punch>cr.Time Strike>Backhand>What an Ugly Watch>delay Punch>Timestop~Star Breaker>dash/Timeskip>Barrage>Light"""))
                    .skinName(Component.literal("Judge, Jury, Executioner"))
                    .skinName(Component.literal("Diamond"))
                    .skinName(Component.literal("Over Heaven"))
                    .build())
            .summonData(SummonData.of(JSoundRegistry.STAR_PLATINUM_SUMMON))
            .build();

    public static final SPTWGroundSlamAttack GROUND_SLAM = new SPTWGroundSlamAttack(20, 12, 19,
            0.75f, 7f, 11, 1.8f, 0f, 0.8f)
            .withAnim(State.GROUND_SLAM)
            .withImpactSound(JSoundRegistry.IMPACT_8)
            .withLaunchNoShockwave()
            .withHitSpark(JParticleType.HIT_SPARK_2)
            .withInfo(
                    Component.translatable("jcraft.sptw.crm1"),
                    Component.literal("low hitbox, decent damage, launches")
            );
    public static final SimpleAttack<SPTWEntity> LIGHT_FOLLOWUP = new SimpleAttack<SPTWEntity>(
            0, 5, 14, 0.75f, 6, 12, 1.5f, 1f, -0.1f)
            .withAnim(State.LIGHT_FOLLOWUP)
            .withImpactSound(JSoundRegistry.IMPACT_1)
            .withLaunch()
            .withBlockStun(4)
            .withExtraHitBox(0, 0.25, 1)
            .withHitSpark(JParticleType.HIT_SPARK_2)
            .withInfo(
                    Component.translatable("jcraft.starplatinum.m1m1"),
                    Component.literal("quick combo finisher")
            );
    public static final SimpleAttack<SPTWEntity> PUNCH = SimpleAttack.<SPTWEntity>lightAttack(5, 7,
                    0.75f, 5f, 10, 0.2f, -0.1f)
            .withFollowup(LIGHT_FOLLOWUP)
            .withCrouchingVariant(GROUND_SLAM)
            .withImpactSound(JSoundRegistry.IMPACT_1)
            .withInfo(
                    Component.translatable("jcraft.starplatinum.m1"),
                    Component.literal("quick combo starter, low knockback")
            );
    public static final MainBarrageAttack<SPTWEntity> BARRAGE = new MainBarrageAttack<SPTWEntity>(280, 0,
            40, 0.75f, 1f, 30, 2f, 0.25f, 0f, 3, Blocks.OBSIDIAN.defaultDestroyTime())
            .withSound(JSoundRegistry.STAR_PLATINUM_BARRAGE)
            .withInfo(
                    Component.translatable("jcraft.generic.barrage"),
                    Component.literal("fast reliable combo starter/extender, high stun")
            );
    public static final TimeStrikeAttack TIME_STRIKE = new TimeStrikeAttack(300, 7,
            11, 0.75f, 5f, 12, 1.5f, 0.6f, -0.25f)
            .withImpactSound(JSoundRegistry.IMPACT_1)
            .withExtraHitBox(1f)
            .withInfo(
                    Component.translatable("jcraft.sptw.sp1"),
                    Component.literal("""
                    Teleports forward 2.5m after a short windup, then delivers a fast, low stun hit.
                    Crouch to turn around after teleport.""")
            );
    public static final SimpleAttack<SPTWEntity> BACKHAND = new SimpleAttack<SPTWEntity>(240, 7, 12,
            0.75f, 6f, 20, 1.5f, 0.25f, 0f)
            .withSound(JSoundRegistry.SPTW_BACKHAND)
            .withImpactSound(JSoundRegistry.IMPACT_1)
            .withExtraHitBox(1f)
            .withHitAnimation(CommonHitPropertyComponent.HitAnimation.HIGH)
            .withHitSpark(JParticleType.HIT_SPARK_2)
            .withInfo(
                    Component.translatable("jcraft.sptw.sp2"),
                    Component.literal("fast poke, great stun")
            );
    public static final KnockdownAttack<SPTWEntity> GRAB_SLAM = new KnockdownAttack<SPTWEntity>(0,
            16, 24, 1f, 9f, 10, 1.75f, 0.4f, 0f, 25)
            .withSound(JSoundRegistry.SPTW_UPPERCUT)
            .withImpactSound(JSoundRegistry.IMPACT_1)
            .withHyperArmor()
            .withHitAnimation(CommonHitPropertyComponent.HitAnimation.CRUSH)
            .withHitSpark(JParticleType.HIT_SPARK_3)
            .withInfo(
                    Component.translatable("jcraft.sptw.crsp3hit"),
                    Component.empty()
            );
    public static final GrabAttack<SPTWEntity, State> GRAB2 = new GrabAttack<>(280, 8, 20,
            1f, 2f, 20, 1.5f, 0.1f, 0f, GRAB_SLAM, StateContainer.of(State.GRAB_HIT2))
            .withSound(JSoundRegistry.SPTW_GRAB)
            .withImpactSound(JSoundRegistry.SPTW_GRABHIT)
            .withHitAnimation(null)
            .withInfo(
                    Component.translatable("jcraft.sptw.sp3"),
                    Component.literal("grab, high damage combo-finishing knockdown")
            );
    public static final SimpleAttack<SPTWEntity> GRAB_HIT = new SimpleAttack<SPTWEntity>(0,
            16, 24, 1f, 6f, 20, 1.75f, 0.4f, 0f)
            .withSound(JSoundRegistry.SPTW_UPPERCUT)
            .withImpactSound(JSoundRegistry.IMPACT_6)
            .withAction(EffectAction.inflict(MobEffects.LEVITATION, 5, 10, true, false))
            .withLaunch()
            .withHyperArmor()
            .withHitAnimation(CommonHitPropertyComponent.HitAnimation.CRUSH)
            .withHitSpark(JParticleType.HIT_SPARK_2)
            .withInfo(
                    Component.translatable("jcraft.sptw.sp3hit"),
                    Component.empty()
            );
    public static final GrabAttack<SPTWEntity, State> GRAB = new GrabAttack<>(280, 8, 20,
            1f, 2f, 20, 1.5f, 0.1f, 0f, GRAB_HIT, StateContainer.of(State.GRAB_HIT))
            .withCrouchingVariant(GRAB2)
            .withSound(JSoundRegistry.SPTW_GRAB)
            .withImpactSound(JSoundRegistry.SPTW_GRABHIT)
            .withHitAnimation(null)
            .withInfo(
                    Component.translatable("jcraft.sptw.sp3"),
                    Component.literal("grab, combo-starting uppercut")
            );
    public static final TimeStopMove<SPTWEntity> TIME_STOP = new TimeStopMove<SPTWEntity>(600, 5, 10,
            Either.right(JServerConfig.SPTW_TIME_STOP_DURATION))
            .withSound(JSoundRegistry.STAR_PLATINUM_THE_WORLD)
            .withInfo(
                    Component.translatable("jcraft.generic.ts"),
                    Component.literal("1.75 seconds, extremely low windup")
            );
    public static final TimeSkipMove<SPTWEntity> TIME_SKIP = new TimeSkipMove<SPTWEntity>(300, 14)
            .withSound(JSoundRegistry.STAR_PLATINUM_TIMESKIP)
            .withInfo(
                    Component.translatable("jcraft.generic.tp"),
                    Component.literal("14m range")
            );

    public SPTWEntity(Level worldIn) {
        super(JStandTypeRegistry.STAR_PLATINUM_THE_WORLD.get(), worldIn);

        auraColors = new Vector3f[]{
                new Vector3f(0.8f, 0.6f, 1.0f),
                new Vector3f(1.0f, 0.4f, 0.8f),
                new Vector3f(0.7f, 0.7f, 1.0f),
                new Vector3f(0.8f, 1.0f, 1.0f)
        };
    }

    private static void registerMoves(MoveMap<SPTWEntity, State> moves) {
        moves.registerImmediate(MoveClass.LIGHT, PUNCH, State.PUNCH);

        moves.register(MoveClass.HEAVY, STAR_BREAKER, State.HEAVY).withCrouchingVariant(State.GROUND_BREAKER);
        moves.register(MoveClass.BARRAGE, BARRAGE, State.BARRAGE);

        moves.register(MoveClass.SPECIAL1, TIME_STRIKE, State.TIME_STRIKE);
        moves.register(MoveClass.SPECIAL2, BACKHAND, State.BACKHAND);
        moves.register(MoveClass.SPECIAL3, GRAB, State.GRAB).withCrouchingVariant(State.GRAB);
        moves.register(MoveClass.ULTIMATE, TIME_STOP, State.TIME_STOP);

        moves.register(MoveClass.UTILITY, TIME_SKIP, State.TIME_SKIP);
    }

    @Override
    public boolean initMove(final MoveClass moveClass) {
        if (tryFollowUp(moveClass, MoveClass.LIGHT)) return true;
        return super.initMove(moveClass);
    }

    @Override
    public void desummon() {
        if (tsTime > 0) {
            return;
        }
        super.desummon();
    }

    @Override
    @NonNull
    public SPTWEntity getThis() {
        return this;
    }

    // Animation code
    public enum State implements StandAnimationState<SPTWEntity> {
        IDLE(builder -> builder.setAnimation(RawAnimation.begin().thenLoop("animation.sptw.idle"))),
        PUNCH(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.sptw.punch"))),
        BLOCK(builder -> builder.setAnimation(RawAnimation.begin().thenLoop("animation.sptw.block"))),
        HEAVY(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.sptw.heavy"))),
        GROUND_BREAKER(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.sptw.ground_break"))),
        BARRAGE(builder -> builder.setAnimation(RawAnimation.begin().thenLoop("animation.sptw.barrage"))),
        TIME_STRIKE(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.sptw.timestrike"))),
        TIME_STOP(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.sptw.timestop"))),
        BACKHAND(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.sptw.backhand"))),
        GRAB(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.sptw.grab"))),
        GRAB_HIT(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.sptw.grabhit"))),
        GRAB_HIT2(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.sptw.grabhit2"))),
        TIME_SKIP(builder -> builder.setAnimation(RawAnimation.begin().thenLoop("animation.sptw.idle"))),
        GROUND_SLAM(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.sptw.ground_slam"))),
        LIGHT_FOLLOWUP(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.sptw.light_followup")));

        private final Consumer<AnimationState<SPTWEntity>> animator;

        State(Consumer<AnimationState<SPTWEntity>> animator) {
            this.animator = animator;
        }

        @Override
        public void playAnimation(SPTWEntity attacker, AnimationState<SPTWEntity> builder) {
            animator.accept(builder);
        }
    }

    @Override
    protected State[] getStateValues() {
        return State.values();
    }

    @Override
    protected String getSummonAnimation() {
        return "animation.sptw.summon";
    }

    @Override
    public State getBlockState() {
        return State.BLOCK;
    }
}
