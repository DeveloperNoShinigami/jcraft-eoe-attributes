package net.arna.jcraft.common.entity.stand;

import it.unimi.dsi.fastutil.ints.IntSet;
import lombok.NonNull;
import mod.azure.azurelib.core.animation.AnimatableManager;
import mod.azure.azurelib.core.animation.AnimationController;
import mod.azure.azurelib.core.animation.AnimationState;
import mod.azure.azurelib.core.animation.RawAnimation;
import mod.azure.azurelib.core.object.PlayState;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.attack.core.MoveInputType;
import net.arna.jcraft.common.attack.core.MoveMap;
import net.arna.jcraft.common.attack.core.MoveType;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.moves.shadowtheworld.STWCounterAttack;
import net.arna.jcraft.common.attack.moves.shared.*;
import net.arna.jcraft.common.attack.moves.theworld.overheaven.LungeAttack;
import net.arna.jcraft.common.component.living.CommonHitPropertyComponent;
import net.arna.jcraft.common.component.world.CommonShockwaveHandlerComponent;
import net.arna.jcraft.common.config.JServerConfig;
import net.arna.jcraft.common.util.JParticleType;
import net.arna.jcraft.common.util.JUtils;
import net.arna.jcraft.common.attack.MobilityType;
import net.arna.jcraft.common.util.StandAnimationState;
import net.arna.jcraft.platform.JComponentPlatformUtils;
import net.arna.jcraft.registry.JSoundRegistry;
import net.arna.jcraft.registry.JStatusRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public final class ShadowTheWorldEntity extends AbstractTheWorldEntity<ShadowTheWorldEntity, ShadowTheWorldEntity.State> {
    public static final UppercutAttack<ShadowTheWorldEntity> UPPERCUT = new UppercutAttack<ShadowTheWorldEntity>((int) (JCraft.LIGHT_COOLDOWN * 1.5),
            10, 16, 0.75f, 6f, 20, 1.5f, 0.25f, -0.6f, 1.0f)
            .withAnim(State.UPPERCUT)
            .withImpactSound(JSoundRegistry.IMPACT_1.get())
            .withExtraHitBox(0, 0.35, 1.25)
            .withHitSpark(JParticleType.HIT_SPARK_2)
            .withInfo(
                    Component.literal("Uppercut"),
                    Component.literal("slower combo starter, launches vertically")
            );
    public static final SimpleAttack<ShadowTheWorldEntity> LIGHT = SimpleAttack.<ShadowTheWorldEntity>lightAttack(
                    5, 7, 0.75f, 5, 10, 0.1f, -0.1f)
            .withImpactSound(JSoundRegistry.IMPACT_1.get())
            // .withFollowup(LIGHT_FOLLOWUP)
            .withCrouchingVariant(UPPERCUT)
            .withInfo(
                    Component.literal("Punch"),
                    Component.literal("quick combo starter")
            );
    public static final KnockdownAttack<ShadowTheWorldEntity> GUARD_CANCEL = new KnockdownAttack<ShadowTheWorldEntity>(7 * 20,
            10, 16, 0.75f, 7f, 12, 1.75f, 2f, 0f, 25)
            .withAnim(State.GUARD_CANCEL)
            .withHyperArmor()
            .withSound(JSoundRegistry.STW_WARBLE.get())
            .withImpactSound(JSoundRegistry.TW_KICK_HIT.get())
            .withHitSpark(JParticleType.HIT_SPARK_3)
            .withLaunch()
            .withInfo(
                    Component.literal("Shoulder Bash"),
                    Component.literal("uninterruptible get-off-me tool, brief knockdown")
            );
    public static final LungeAttack LUNGE = new LungeAttack(40, 14, 20, 0.75f,
            8f, 19, 1.6f, 2f, 0f, 10, 6)
            .withCrouchingVariant(GUARD_CANCEL)
            .withSound(JSoundRegistry.STW_WARBLE.get())
            .withImpactSound(JSoundRegistry.TW_KICK_HIT.get())
            .withHitSpark(JParticleType.HIT_SPARK_2)
            .withHitAnimation(CommonHitPropertyComponent.HitAnimation.CRUSH)
            .withInfo(
                    Component.literal("Lunge"),
                    Component.literal("medium speed launcher")
            );
    public static final KnockdownAttack<ShadowTheWorldEntity> KNOCKDOWN = new KnockdownAttack<ShadowTheWorldEntity>(0,
            2, 4, 0.85f, 5f, 20, 1.75f, 2f, 0, 35)
            .withImpactSound(JSoundRegistry.TW_KICK_HIT.get())
            .withHitSpark(JParticleType.HIT_SPARK_2)
            .withLaunch()
            .withInfo(
                    Component.literal("3 Hit Combo (Finisher)"),
                    Component.empty()
            );
    public static final SimpleMultiHitAttack<ShadowTheWorldEntity> THREE_HIT = new SimpleMultiHitAttack<ShadowTheWorldEntity>(100,
            24, 0.85f, 4f, 15, 1.5f, 0.35f, 0.2f, IntSet.of(6, 14))
            .withFinisher(20, KNOCKDOWN)
            .withImpactSound(JSoundRegistry.IMPACT_1.get())
            .withInfo(
                    Component.literal("3 Hit Combo"),
                    Component.literal("knocks down")
            );
    public static final SimpleAttack<ShadowTheWorldEntity> IMPALING_THRUST_HIT = new SimpleAttack<ShadowTheWorldEntity>(0,
            0, 10, 0.8f, 0, 0, 0, 0, 0)
            .withInitAction(ShadowTheWorldEntity::impalingThrust);
    public static final HoldableMove<ShadowTheWorldEntity, State> IMPALING_THRUST = new HoldableMove<>(200,
            61, 60, 0.75f, IMPALING_THRUST_HIT, State.IMPALING_THRUST_HIT, 10)
            .withInfo(
                    Component.literal("Impaling Thrust"),
                    Component.literal("chargeable attack, Shadow The World prepares an attack, then stops time and hits everything between the start and end")
            )
            .markRanged()
            .withMobilityType(MobilityType.TELEPORT);
    public static final ChargeAttack<ShadowTheWorldEntity, ShadowTheWorldEntity.State> CHARGE = new ChargeAttack<>(
            280, 5, 19, 5.0f, 5f, 20, 1.5f, 0.25f, 0, State.CHARGE_HIT)
            .withSound(JSoundRegistry.TW_CHARGE.get())
            .withSound(JSoundRegistry.STW_WARBLE.get())
            .withImpactSound(JSoundRegistry.TW_CHARGE_HIT.get())
            .withHitSpark(JParticleType.HIT_SPARK_2)
            .withHitAnimation(CommonHitPropertyComponent.HitAnimation.CRUSH)
            .withBlockStun(11)
            .withInfo(
                    Component.literal("Forward Charge"),
                    Component.literal("The World detaches from the user and lunges forward, combo starter")
            );
    public static final TimeSkipMove<ShadowTheWorldEntity> TIME_SKIP = new TimeSkipMove<ShadowTheWorldEntity>(200, 7)
            .withSound(JSoundRegistry.TIME_SKIP.get())
            .withSound(JSoundRegistry.STW_ZAP.get())
            .withInfo(
                    Component.literal("Timeskip"),
                    Component.literal("7m range")
            );
    public static final TimeStopMove<ShadowTheWorldEntity> TIME_STOP = new TimeStopMove<ShadowTheWorldEntity>(1400,
            20, 30, JServerConfig.STW_TIME_STOP_DURATION::getValue)
            .withSound(JSoundRegistry.STW_TS.get())
            .withInfo(
                    Component.literal("Timestop"),
                    Component.literal("2.5 seconds")
            );
    public static final STWCounterAttack COUNTER = new STWCounterAttack(400, 5, 20, 0.75f)
            .withInfo(
                    Component.literal("Counter"),
                    Component.literal("""
                                            if struck by an opponent, you will stun them and teleport behind them
                                            during this, you may not use your spec or move
                                            """)
            );
    private int desummonTime = 6;
    private static final EntityDataAccessor<Boolean> DESUMMONING;
    static { DESUMMONING = SynchedEntityData.defineId(ShadowTheWorldEntity.class, EntityDataSerializers.BOOLEAN); }
    public ShadowTheWorldEntity(Level worldIn) {
        super(StandType.SHADOW_THE_WORLD, worldIn, JSoundRegistry.STW_WARBLE.get());

        freespace = """
                The user is allowed to use spec moves as soon as Shadow The World is performing one.
                Desummons itself upon finishing a move.
                """;
        idleRotation = -45f;

        proCount = 5;
        conCount = 4;

        auraColors = new Vector3f[]{
                new Vector3f(0.5f, 0.1f, 0.7f),
                new Vector3f(0.8f, 0.2f, 0.4f),
                new Vector3f(0.2f, 0.6f, 8.0f),
                new Vector3f(0.7f, 0.3f, 1.0f)
        };
    }

    private static void impalingThrust(ShadowTheWorldEntity attacker, LivingEntity user, MoveContext ctx) {
        final ServerLevel world = (ServerLevel) attacker.level();
        final CommonShockwaveHandlerComponent shockwaveHandler = JComponentPlatformUtils.getShockwaveHandler(world);

        final Vec3 start = user.getEyePosition(), end = user.getEyePosition().add(user.getLookAngle().scale(8));
        HitResult hitResult = world.clip(new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, user));
        final Vec3 pos2 = hitResult.getLocation();
        final Vec3 towardsVec = pos2.subtract(start);

        final DamageSource playerSource = world.damageSources().mobAttack(user);

        user.teleportToWithTicket(pos2.x, pos2.y, pos2.z);

        final double count = Math.round(start.distanceTo(pos2));

        boolean hitAny = false;
        Set<LivingEntity> processed = new HashSet<>();
        for (int i = 0; i < count; i++) {
            final Vec3 curPos = start.add(towardsVec.scale(i / count));
            if (i % 3 == 0) shockwaveHandler.addShockwave(curPos, towardsVec, 2.25f);

            final Vec3 vec1 = curPos.add(-1, -1, -1);
            final Vec3 vec2 = curPos.add(1, 1, 1);

            JUtils.displayHitbox(world, vec1, vec2);

            final List<LivingEntity> hurt = world.getEntitiesOfClass(LivingEntity.class, new AABB(vec1, vec2),
                    EntitySelector.NO_CREATIVE_OR_SPECTATOR.and(e -> e != attacker && e != user));
            hurt.removeIf(processed::contains);
            if (processed.addAll(hurt)) {
                hitAny = true;
                JCraft.createParticle(world,
                        curPos.x + attacker.random.nextGaussian(),
                        curPos.y + attacker.random.nextGaussian(),
                        curPos.z + attacker.random.nextGaussian(),
                        JParticleType.HIT_SPARK_2
                );
                for (LivingEntity ent : hurt) {
                    final LivingEntity target = JUtils.getUserIfStand(ent);
                    // +6 on hit/-4 on block launcher
                    // +0 if you count STW desummon not letting you block
                    StandEntity.damageLogic(world, target,
                            target.position().subtract(curPos).normalize(), 10 + attacker.desummonTime, 3, false,
                            8.0f, true, 12, playerSource, user, CommonHitPropertyComponent.HitAnimation.LAUNCH);
                    target.addEffect(new MobEffectInstance(JStatusRegistry.KNOCKDOWN.get(), 35, 0, true, false));
                }
            }
        }

        if (hitAny) {
            attacker.playSound(JSoundRegistry.IMPACT_1.get(), 1.0f, 1.0f);
        }

        attacker.playSound(JSoundRegistry.TIME_SKIP.get(), 1f, 1f);
        attacker.playSound(JSoundRegistry.STW_ZAP.get(), 1f, 1f);
    }

    private static final Vector3f INVIS_AURA = new Vector3f(0, 0, 0);
    @Override
    public Vector3f getAuraColor() {
        if (getState() == State.COUNTER) return INVIS_AURA;
        return super.getAuraColor();
    }

    @Override
    public void queueMove(MoveInputType type) {
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(DESUMMONING, false);
    }

    @Override
    protected void registerMoves(MoveMap<ShadowTheWorldEntity, State> moves) {
        moves.registerImmediate(MoveType.LIGHT, LIGHT, State.LIGHT);
        moves.registerImmediate(MoveType.HEAVY, LUNGE, State.LUNGE);
        moves.register(MoveType.BARRAGE, THREE_HIT, State.THREE_HIT);

        moves.register(MoveType.SPECIAL1, COUNTER, State.COUNTER);
        moves.register(MoveType.SPECIAL2, CHARGE, State.CHARGE);
        moves.register(MoveType.SPECIAL3, IMPALING_THRUST, State.IMPALING_THRUST_CHARGE);

        moves.register(MoveType.ULTIMATE, TIME_STOP, State.TIME_STOP);

        moves.register(MoveType.UTILITY, TIME_SKIP, State.IDLE);
    }

    public void startAnimatedDesummon() {
        entityData.set(DESUMMONING, true);
        //todo: playSound(JSoundRegistry.SHADOW_THE_WORLD_DESUMMON);
        if (isFree()) return;
        setFree(true);
        setFreePos(position().toVector3f());
    }

    public boolean isAnimatedDesummoning() {
        return entityData.get(DESUMMONING);
    }

    @Override
    public boolean allowMoveHandling() {
        if (isAnimatedDesummoning()) return false;
        if (getState() == State.CHARGE_HIT) return false;
        final boolean noMove = getCurrentMove() == null;
        return noMove || getCurrentMove().getMoveType() == MoveType.SPECIAL3;
    }

    @Override
    public void cancelMove() {
        if (isAnimatedDesummoning()) return;
        super.cancelMove();
    }

    @Override
    public void tick() {
        super.tick();
        if (isAnimatedDesummoning()) {
            if (--desummonTime < 1) discard();
        }
        if (level().isClientSide()) {
            //stw particles?
            return;
        }
        if (tsTime < 1) {
            if ( (getCurrentMove() != null || getState() == State.CHARGE_HIT) && getMoveStun() == 1 && getState() != State.COUNTER) {
                // Stay in final attack pose
                setCurrentMove(null);
                setMoveStun(desummonTime);
                startAnimatedDesummon();
            }
        }
    }

    @Override
    public boolean defaultToNear() {
        return !isAnimatedDesummoning();
    }

    @Override
    public boolean isInvulnerable() {
        if (isAnimatedDesummoning()) return true;
        return super.isInvulnerable();
    }

    @Override
    @NonNull
    public ShadowTheWorldEntity getThis() {
        return this;
    }

    // Animation code
    public enum State implements StandAnimationState<ShadowTheWorldEntity> {
        IDLE(builder -> builder.setAnimation(RawAnimation.begin().thenLoop("animation.shadow_the_world.idle"))),
        LIGHT(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.shadow_the_world.light"))),
        BLOCK(builder -> builder.setAnimation(RawAnimation.begin().thenLoop("animation.shadow_the_world.block"))),
        LUNGE(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.shadow_the_world.lunge"))),
        GUARD_CANCEL(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.shadow_the_world.guard_cancel"))),
        THREE_HIT(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.shadow_the_world.3hit"))),
        IMPALING_THRUST_CHARGE(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.shadow_the_world.impaling_thrust_charge"))),
        IMPALING_THRUST_HIT(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.shadow_the_world.impaling_thrust_hit"))),
        CHARGE(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.shadow_the_world.charge"))),
        CHARGE_HIT(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.shadow_the_world.charge_hit"))),
        UPPERCUT(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.shadow_the_world.uppercut"))),
        COUNTER(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.shadow_the_world.counter"))),
        TIME_STOP(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.shadow_the_world.timestop"))),
        ;

        private final BiConsumer<ShadowTheWorldEntity, AnimationState<ShadowTheWorldEntity>> animator;

        State(Consumer<AnimationState<ShadowTheWorldEntity>> animator) {
            this((stand, builder) -> animator.accept(builder));
        }

        State(BiConsumer<ShadowTheWorldEntity, AnimationState<ShadowTheWorldEntity>> animator) {
            this.animator = animator;
        }

        @Override
        public void playAnimation(ShadowTheWorldEntity attacker, AnimationState<ShadowTheWorldEntity> builder) {
            animator.accept(attacker, builder);
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        super.registerControllers(controllers);
        controllers.add(new AnimationController<>(getThis(), "desummon", 0, this::desummonPredicate));
    }

    private static final RawAnimation DESUMMON_SQUEEZE = RawAnimation.begin().thenPlayAndHold("animation.shadow_the_world.desummon");
    private PlayState desummonPredicate(AnimationState<ShadowTheWorldEntity> state) {
        if (isAnimatedDesummoning()) {
            state.getController().setAnimation(DESUMMON_SQUEEZE);
            return PlayState.CONTINUE;
        }
        return PlayState.STOP;
    }

    @Override
    protected ShadowTheWorldEntity.State[] getStateValues() {
        return ShadowTheWorldEntity.State.values();
    }

    @Override
    protected @NotNull String getSummonAnimation() {
        return "animation.shadow_the_world.summon";
    }

    @Override
    public ShadowTheWorldEntity.State getBlockState() {
        return ShadowTheWorldEntity.State.BLOCK;
    }
}
