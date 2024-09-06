package net.arna.jcraft.common.entity.stand;

import it.unimi.dsi.fastutil.ints.IntList;
import lombok.NonNull;
import mod.azure.azurelib.core.object.Color;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.attack.core.BlockableType;
import net.arna.jcraft.common.attack.core.MoveMap;
import net.arna.jcraft.common.attack.core.MoveType;
import net.arna.jcraft.common.attack.moves.base.AbstractMove;
import net.arna.jcraft.common.attack.moves.shared.*;
import net.arna.jcraft.common.attack.moves.theworld.overheaven.*;
import net.arna.jcraft.common.component.living.CommonHitPropertyComponent;
import net.arna.jcraft.common.config.JServerConfig;
import net.arna.jcraft.common.util.JParticleType;
import net.arna.jcraft.common.util.StandAnimationState;
import net.arna.jcraft.registry.JSoundRegistry;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import mod.azure.azurelib.core.animation.AnimationState;
import mod.azure.azurelib.core.animation.RawAnimation;


import java.util.List;
import java.util.function.Consumer;

public class TheWorldOverHeavenEntity extends StandEntity<TheWorldOverHeavenEntity, TheWorldOverHeavenEntity.State> {
    public static final LungeAttack LUNGE = new LungeAttack(0, 10, 16, 0.75f,
            8f, 10, 1.75f, 1f, 0f, 11, 5)
            .withAnim(State.LUNGE)
            .withSound(JSoundRegistry.MUDA_DA.get())
            .withImpactSound(JSoundRegistry.TW_KICK_HIT.get())
            .withHitSpark(JParticleType.HIT_SPARK_2)
            .withLaunch()
            .withInfo(
                    Component.literal("Lunge"),
                    Component.literal("medium speed launcher")
            );
    public static final SimpleAttack<TheWorldOverHeavenEntity> LOW_KICK = SimpleAttack.<TheWorldOverHeavenEntity>lightAttack(
                    6, 12, 0.75f, 6f, 14, 0.25f, 0.25f)
            .withAnim(State.LOW_KICK)
            .withFollowup(LUNGE)
            .withImpactSound(JSoundRegistry.IMPACT_1.get())
            .withBlockStun(7)
            .withInfo(
                    Component.literal("Low Kick"),
                    Component.literal("quick combo starter")
            );
    public static final SimpleAttack<TheWorldOverHeavenEntity> LIGHT_FOLLOWUP = new SimpleAttack<TheWorldOverHeavenEntity>(
            0, 9, 13, 0.75f, 6f, 8, 1.75f, 1.25f, -0.1f)
            .withAnim(State.LIGHT_FOLLOWUP)
            .withImpactSound(JSoundRegistry.IMPACT_1.get())
            .withLaunch()
            .withBlockStun(4)
            .withExtraHitBox(0, 0.25, 1)
            .withHitSpark(JParticleType.HIT_SPARK_2)
            .withInfo(
                    Component.literal("Roundhouse"),
                    Component.literal("quick combo finisher")
            );
    public static final SimpleAttack<TheWorldOverHeavenEntity> PUNCH = SimpleAttack.<TheWorldOverHeavenEntity>lightAttack(
                    4, 7, 0.75f, 5f, 11, 0.2f, -0.1f)
            .withFollowup(LIGHT_FOLLOWUP)
            .withCrouchingVariant(LOW_KICK)
            .withImpactSound(JSoundRegistry.IMPACT_1.get())
            .withInfo(
                    Component.literal("Punch"),
                    Component.literal("quick combo starter")
            );
    public static final MainBarrageAttack<TheWorldOverHeavenEntity> BARRAGE = new MainBarrageAttack<TheWorldOverHeavenEntity>(
            280, 0, 40, 0.75f, 1f, 30, 2f, 0.1f, 0f, 3, Blocks.OBSIDIAN.defaultDestroyTime())
            .withSound(JSoundRegistry.TWOH_BARRAGE.get())
            .withImpactSound(JSoundRegistry.IMPACT_1.get())
            .withInfo(
                    Component.literal("Barrage"),
                    Component.literal("fast reliable combo starter/extender, high stun")
            );
    public static final SingularityAttack SINGULARITY = new SingularityAttack(260, 11, 23,
            1f, 0f, 25, 2f, 0.4f, 0.2f, true)
            .withSound(JSoundRegistry.TWOH_SINGULARITY.get())
            .withAnim(State.SINGULARITY)
            .withImpactSound(JSoundRegistry.IMPACT_12.get())
            .withBlockableType(BlockableType.NON_BLOCKABLE_EFFECTS_ONLY)
            .withHitAnimation(CommonHitPropertyComponent.HitAnimation.CRUSH)
            .withHitSpark(JParticleType.HIT_SPARK_3)
            .withInfo(
                    Component.literal("Singularity"),
                    Component.literal("block bypass (stun will always hit, but the opponent can stay blocking)")
            );
    public static final UppercutAttack<TheWorldOverHeavenEntity> OVERHEAD_KICK = new UppercutAttack<TheWorldOverHeavenEntity>(
            200, 10, 20, 1.25f, 8f, 20, 1.5f, 0.3f, 0f, -1)
            //.withSound(JSoundRegistry.TWOH_HEAVY)
            .withAnim(State.AIR_HEAVY)
            .withImpactSound(JSoundRegistry.IMPACT_1.get())
            .withHitAnimation(CommonHitPropertyComponent.HitAnimation.CRUSH)
            .withHitSpark(JParticleType.HIT_SPARK_3)
            .withExtraHitBox(1, 0.75, 1)
            .withExtraHitBox(1, -0.5, 1)
            .withInfo(
                    Component.literal("Overhead Kick"),
                    Component.literal("high damage, good reach, launches down")
            );
    public static final SingularityAttack TRUE_STRIKE = new SingularityAttack(200, 10, 22,
            1f, 0f, 20, 2f, 0.3f, 0f, false)
            .withBlockStun(20)
            .withAerialVariant(OVERHEAD_KICK)
            .withCrouchingVariant(SINGULARITY)
            .withSound(JSoundRegistry.TWOH_HEAVY.get())
            .withImpactSound(JSoundRegistry.IMPACT_12.get())
            .withBlockableType(BlockableType.NON_BLOCKABLE_EFFECTS_ONLY)
            .withHitAnimation(CommonHitPropertyComponent.HitAnimation.CRUSH)
            .withHitSpark(JParticleType.HIT_SPARK_2)
            .withInfo(
                    Component.literal("True Strike"),
                    Component.literal("damage ignores potions and enchantments, low stun, high blockstun, medium windup")
            );
    public static final SmiteAttack AIR_SMITE = new SmiteAttack(300, 10, 20, 1f,
            6f, 21, 3f, 0f, 0f, true)
            .withSound(JSoundRegistry.TWOH_SMITE.get())
            .withBlockStun(13)
            .withHitAnimation(CommonHitPropertyComponent.HitAnimation.HIGH)
            .withInfo(
                    Component.literal("You won't run away!"),
                    Component.literal("summons a weaker lightning bolt at the aimed position")
            );
    public static final SmiteAttack SMITE = new SmiteAttack(300, 10, 20, 1f,
            8f, 21, 3f, 0f, 0f, false)
            .withAerialVariant(AIR_SMITE)
            .withSound(JSoundRegistry.TWOH_SMITE.get())
            .withBlockStun(13)
            .withHitAnimation(CommonHitPropertyComponent.HitAnimation.HIGH)
            .withInfo(
                    Component.literal("Evaporate"),
                    Component.literal("summons a powerful lightning bolt that deals high damage and stun")
            );
    public static final OverwriteAttack OVERWRITE = new OverwriteAttack(0, 7, 23, 1f,
            0f, 40, 2f, 1f, 0f)
            .withSound(JSoundRegistry.TWOH_OVERWRITE.get())
            .withImpactSound(JSoundRegistry.IMPACT_5.get())
            .withLaunch()
            .withHyperArmor()
            .withBlockableType(BlockableType.NON_BLOCKABLE)
            .withHitSpark(JParticleType.HIT_SPARK_3)
            .withInfo(
                    Component.literal("Overwrite (Hit)"),
                    Component.empty()
            );
    // Does absolutely nothing on its own.
    public static final NoOpMove<TheWorldOverHeavenEntity> CHARGE_OVERWRITE = new NoOpMove<TheWorldOverHeavenEntity>(
            360, 70, 1f)
            .withFollowup(OVERWRITE)
            .withSound(JSoundRegistry.TWOH_CHARGE_OVERWRITE.get())
            .withInfo(Component.literal("Reality Overwrite"), Component.literal("""
                    charges (for a minimum of 1s) an unblockable punch that changes the reality of the hit victims
                    While charging, (de)activate overwrite by pressing:
                    SPECIAL 1 - makes victims unable to look at you (stops if TW:OH is desummoned)
                    SPECIAL 2 - applies every damage over time effect to victims
                    SPECIAL 3 - heals and enslaves mobs"""));

    public static final AerialDivineFinisherAttack AERIAL_DIVINE_FINISHER = new AerialDivineFinisherAttack(280,
            16, 22, 0.75f, 0f, 20, 1.5f, 0f, 0f)
            .withSound(JSoundRegistry.TWOH_KNIFETHROW.get())
            .withBlockStun(6)
            .withInfo(
                    Component.literal("Aerial Divine Finisher"),
                    Component.empty()
            );
    public static final DivineFinisherAttack DIVINE_FINISHER = new DivineFinisherAttack(280, 16, 22,
            0.75f, 0f, 20, 1.5f, 0f, 0f)
            .withAerialVariant(AERIAL_DIVINE_FINISHER)
            .withSound(JSoundRegistry.TWOH_AIRKNIVES.get())
            .withBlockStun(6)
            .withInfo(
                    Component.literal("Divine Finisher"),
                    Component.literal("fires 4 stunning knives that launch at a delay/in air summons and launches 8 knives")
            );
    public static final TimeStopMove<TheWorldOverHeavenEntity> TIME_STOP = new TimeStopMove<TheWorldOverHeavenEntity>(
            1400, 45, 50, JServerConfig.TWOH_TIME_STOP_DURATION::getValue)
            .withSound(JSoundRegistry.TWOH_TS.get())
            .withInfo(
                    Component.literal("Timestop"),
                    Component.literal("5 seconds")
            );

    public static final TimeSkipMove<TheWorldOverHeavenEntity> TIME_SKIP = new TimeSkipMove<TheWorldOverHeavenEntity>(
            300, 14)
            .withSound(JSoundRegistry.TWOH_TIMESKIP.get())
            .withInfo(
                    Component.literal("Timeskip"),
                    Component.literal("14m range")
            );
    private static final EntityDataAccessor<Integer> OVERWRITE_TYPE;

    static {
        OVERWRITE_TYPE = SynchedEntityData.defineId(TheWorldOverHeavenEntity.class, EntityDataSerializers.INT);
    }

    public TheWorldOverHeavenEntity(Level worldIn) {
        super(StandType.THE_WORLD_OVER_HEAVEN, worldIn, JSoundRegistry.TWOH_SUMMON.get());
        idleRotation = -45f;
        summonAnimDuration = 29;

        proCount = 4;
        conCount = 4;

        freespace =
                """
                        BNBs:
                            -the ultrakill
                            Light>Barrage>Light>Knives>Overwrite~S1/S2>dash>Singularity>Smite>Light~Light
                            
                            -JUDGE MENT
                            crouching Light~Light>dash>Barrage>...""";

        auraColors = new Vector3f[]{
                new Vector3f(0.1f, 0.1f, 0.1f),
                new Vector3f(1f, 0.6f, 0.8f),
                new Vector3f(0.9f, 0.9f, 1.0f),
                new Vector3f(1.0f, 0.0f, 0.2f)
        };
    }

    @Override
    public Vector3f getAuraColor() {
        if (getSkin() > 0) {
            return super.getAuraColor();
        }
        Color auraColor = Color.ofHSB(tickCount % 360f / 360f, 0.5f, 0.5f);
        return new Vector3f(auraColor.getRed(), auraColor.getGreen(), auraColor.getBlue());
    }

    public int getOverwriteType() {
        return entityData.get(OVERWRITE_TYPE);
    }

    public void setOverwriteType(int type) {
        entityData.set(OVERWRITE_TYPE, type);
    }

    @Override
    public void desummon() {
        if (tsTime > 0) {
            return;
        }
        super.desummon();
    }

    @Override
    protected void registerMoves(MoveMap<TheWorldOverHeavenEntity, State> moves) {
        moves.registerImmediate(MoveType.LIGHT, PUNCH, State.LIGHT);

        moves.registerImmediate(MoveType.HEAVY, TRUE_STRIKE, State.HEAVY);
        moves.register(MoveType.BARRAGE, BARRAGE, State.BARRAGE);

        moves.register(MoveType.SPECIAL1, SMITE, State.SMITE);
        moves.register(MoveType.SPECIAL2, DIVINE_FINISHER, State.AIR_KNIVES).withAerialVariant(State.THROW);
        moves.register(MoveType.SPECIAL3, CHARGE_OVERWRITE, State.CHARGE_OVERWRITE);
        moves.register(MoveType.ULTIMATE, TIME_STOP, State.TIME_STOP);

        moves.register(MoveType.UTILITY, TIME_SKIP, State.TIME_SKIP);
    }

    @Override
    public boolean initMove(MoveType type) {
        switch (type) {
            case SPECIAL1, SPECIAL2, SPECIAL3 -> {
                if (curMove != null && curMove.getOriginalMove() == CHARGE_OVERWRITE && getMoveStun() < 50) {
                    initOverwrite(switch (type) {
                        default -> 1;
                        case SPECIAL2 -> 2;
                        case SPECIAL3 -> 3;
                    });
                } else {
                    return super.initMove(type);
                }
            }
            case ULTIMATE -> {
                if (tsTime <= 0) {
                    return super.initMove(type);
                } else if (hasUser()) {
                    JCraft.stopTimestop(getUserOrThrow());
                    tsTime = 0;
                }
            }
            case LIGHT -> {
                if (curMove != null && curMove.getMoveType() == MoveType.LIGHT && getMoveStun() < curMove.getWindupPoint()) {
                    AbstractMove<?, ? super TheWorldOverHeavenEntity> followup = curMove.getFollowup();
                    if (followup != null) {
                        setMove(followup, (State) followup.getAnimation());
                    }
                } else {
                    return super.initMove(type);
                }
            }
            default -> {
                return super.initMove(type);
            }
        }

        return true;
    }

    private void initOverwrite(int type) {
        setOverwriteType(type);
        setMove(OVERWRITE, State.OVERWRITE);
        playSound(JSoundRegistry.TWOH_OVERWRITE.get(), 1, 1);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(OVERWRITE_TYPE, 0);
    }

    @Override
    public void tick() {
        super.tick();

        if (!hasUser()) {
            return;
        }

        IntList overwriteTimes = moveContext.get(OverwriteAttack.OVERWRITE_TIMES);
        List<LivingEntity> overwriteTargets = moveContext.get(OverwriteAttack.OVERWRITE_TARGETS);
        LivingEntity user = getUserOrThrow();

        if (level().isClientSide) {
            return;
        }

        int moveStun = getMoveStun();
        if (moveStun <= 0 && getOverwriteType() != 0) {
            setOverwriteType(0);
        }

        for (int i = 0; i < overwriteTimes.size(); i++) {
            int time = overwriteTimes.getInt(i);
            overwriteTimes.set(i, time - 1);

            if (time < 1) {
                overwriteTimes.removeInt(i);
                overwriteTargets.remove(i);
                i--;
            } else {
                // Inability to look at master
                LivingEntity entity = overwriteTargets.get(i);

                double range = 1024.0;

                AABB box = entity
                        .getBoundingBox()
                        .expandTowards(entity.getViewVector(1.0F).scale(range))
                        .inflate(1.0D);
                EntityHitResult hitResult = ProjectileUtil.getEntityHitResult(
                        entity, entity.getEyePosition(),
                        entity.getEyePosition().add(entity.getLookAngle().scale(range)),
                        box, EntitySelector.NO_CREATIVE_OR_SPECTATOR, range);

                if (hitResult == null) {
                    continue;
                }
                Entity lookEntity = hitResult.getEntity();

                if (lookEntity != user && lookEntity != this) {
                    continue;
                }
                entity.lookAt(EntityAnchorArgument.Anchor.EYES, getEyePosition().add(
                        random.nextInt() * 10,
                        random.nextInt() * 10,
                        random.nextInt() * 10));
            }
        }
    }

    @Override
    protected void playSummonSound() {
        if (shouldNotPlaySummonSound()) {
            return;
        }

        playSound(JSoundRegistry.TWOH_SUMMON.get(), 1f, 1f);
        playSound(JSoundRegistry.TW_SUMMON.get(), 1f, 1f);
    }

    @Override
    @NonNull
    public TheWorldOverHeavenEntity getThis() {
        return this;
    }

    // Animation code
    public enum State implements StandAnimationState<TheWorldOverHeavenEntity> {
        IDLE(builder -> builder.setAnimation(RawAnimation.begin().thenLoop("animation.twoh.idle"))),
        LIGHT(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.twoh.light"))),
        BLOCK(builder -> builder.setAnimation(RawAnimation.begin().thenLoop("animation.twoh.block"))),
        HEAVY(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.twoh.heavy"))),
        BARRAGE(builder -> builder.setAnimation(RawAnimation.begin().thenLoop("animation.twoh.barrage"))),
        SMITE(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.twoh.smite"))),
        TIME_STOP(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.twoh.timestop"))),
        CHARGE_OVERWRITE(builder -> builder.setAnimation(RawAnimation.begin().thenLoop("animation.twoh.chargeoverwrite"))),
        OVERWRITE(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.twoh.overwrite"))),
        THROW(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.twoh.throw"))),
        AIR_KNIVES(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.twoh.airknives"))),
        TIME_SKIP(builder -> builder.setAnimation(RawAnimation.begin().thenLoop("animation.twoh.idle"))),
        LUNGE(builder -> builder.setAnimation(RawAnimation.begin().thenLoop("animation.twoh.lunge"))),
        LOW_KICK(builder -> builder.setAnimation(RawAnimation.begin().thenLoop("animation.twoh.low_kick"))),
        LIGHT_FOLLOWUP(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.twoh.light_followup"))),
        SINGULARITY(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.twoh.singularity"))),
        AIR_HEAVY(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.twoh.air_heavy"))),
        ;

        private final Consumer<AnimationState> animator;

        State(Consumer<AnimationState> animator) {
            this.animator = animator;
        }

        @Override
        public void playAnimation(TheWorldOverHeavenEntity attacker, AnimationState builder) {
            animator.accept(builder);
        }
    }

    @Override
    protected State[] getStateValues() {
        return State.values();
    }

    @Override
    protected @Nullable String getSummonAnimation() {
        return "animation.twoh.summon";
    }

    @Override
    public State getBlockState() {
        return State.BLOCK;
    }
}
