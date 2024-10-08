package net.arna.jcraft.common.entity.stand;

import it.unimi.dsi.fastutil.ints.IntSet;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import mod.azure.azurelib.core.animation.AnimationState;
import mod.azure.azurelib.core.animation.RawAnimation;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.attack.core.BlockableType;
import net.arna.jcraft.common.attack.core.MoveMap;
import net.arna.jcraft.common.attack.core.MoveType;
import net.arna.jcraft.common.attack.moves.cream.BallChargeAttack;
import net.arna.jcraft.common.attack.moves.cream.BallModeMove;
import net.arna.jcraft.common.attack.moves.cream.ConsumeAttack;
import net.arna.jcraft.common.attack.moves.cream.CreamComboAttack;
import net.arna.jcraft.common.attack.moves.cream.DestroyAttack;
import net.arna.jcraft.common.attack.moves.cream.SurpriseMove;
import net.arna.jcraft.common.attack.moves.shared.ChargeBarrageAttack;
import net.arna.jcraft.common.attack.moves.shared.EffectInflictingAttack;
import net.arna.jcraft.common.attack.moves.shared.GrabAttack;
import net.arna.jcraft.common.attack.moves.shared.KnockdownAttack;
import net.arna.jcraft.common.attack.moves.shared.SimpleAttack;
import net.arna.jcraft.common.attack.moves.shared.SimpleMultiHitAttack;
import net.arna.jcraft.common.component.living.CommonHitPropertyComponent;
import net.arna.jcraft.common.gravity.api.GravityChangerAPI;
import net.arna.jcraft.common.util.JParticleType;
import net.arna.jcraft.common.util.JUtils;
import net.arna.jcraft.common.util.StandAnimationState;
import net.arna.jcraft.registry.JSoundRegistry;
import net.arna.jcraft.registry.JStatusRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static net.arna.jcraft.common.attack.moves.cream.SurpriseMove.OUT_DIR;
import static net.arna.jcraft.common.attack.moves.cream.SurpriseMove.OUT_POS;

/**
 * The {@link StandEntity} for <a href="https://jojowiki.com/Cream">Cream</a>.
 * @see StandType#CREAM
 * @see net.arna.jcraft.client.model.entity.stand.CreamModel CreamModel
 * @see net.arna.jcraft.client.renderer.entity.stands.CreamRenderer CreamRenderer
 * @see BallChargeAttack
 * @see BallModeMove
 * @see ConsumeAttack
 * @see CreamComboAttack
 * @see DestroyAttack
 * @see SurpriseMove
 */
public class CreamEntity extends StandEntity<CreamEntity, CreamEntity.State> {
    public static final EffectInflictingAttack<CreamEntity> BITE = new EffectInflictingAttack<CreamEntity>(20,
            7, 13, 0.75f, 6f, 20, 1.75f, 0.75f, 0.3f,
            List.of(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 1)))
            .withAnim(State.BITE)
            .withHitAnimation(CommonHitPropertyComponent.HitAnimation.LOW)
            .withSound(SoundEvents.EVOKER_FANGS_ATTACK)
            .withHitSpark(JParticleType.HIT_SPARK_2)
            .withInfo(
                    Component.literal("Bite"),
                    Component.literal("applies Slowness II (2s) on hit"));
    public static final SimpleAttack<CreamEntity> LIGHT_FOLLOWUP = new SimpleAttack<CreamEntity>(
            0, 7, 14, 0.75f, 6f, 8, 1.75f, 1.1f, -0.1f)
            .withAnim(State.LIGHT_FOLLOWUP)
            .withImpactSound(JSoundRegistry.IMPACT_3.get())
            .withLaunch()
            .withBlockStun(4)
            .withExtraHitBox(0, 0.25, 1)
            .withHitSpark(JParticleType.HIT_SPARK_2)
            .withInfo(
                    Component.literal("Chop"),
                    Component.literal("quick combo finisher"));
    public static final SimpleAttack<CreamEntity> PUNCH = SimpleAttack.<CreamEntity>lightAttack(6, 14,
                    0.75f, 5f, 20, 0.3f, -0.1f)
            .withFollowup(LIGHT_FOLLOWUP)
            .withCrouchingVariant(BITE)
            .withImpactSound(JSoundRegistry.IMPACT_4.get())
            .withInfo(
                    Component.literal("Backhand"),
                    Component.literal("quick combo starter"));
    public static final SimpleAttack<CreamEntity> VERTICAL_CHOP = new SimpleAttack<CreamEntity>(200, 20,
            30, 1f, 8f, 40, 1.5f, 0.8f, 0f)
            .withSound(JSoundRegistry.CREAM_HEAVY.get())
            .withImpactSound(JSoundRegistry.IMPACT_3.get())
            .withHitSpark(JParticleType.HIT_SPARK_3)
            .withHyperArmor()
            .withHitAnimation(CommonHitPropertyComponent.HitAnimation.HIGH)
            .withInfo(
                    Component.literal("Vertical Chop"),
                    Component.literal("slow, uninterruptible combo starter"));
    public static final CreamComboAttack COMBO = new CreamComboAttack(280, 36, 0.75f,
            5f, 20, 2f, 0.2f, 0f, IntSet.of(10, 17, 25))
            .withSound(JSoundRegistry.CREAM_COMBO.get())
            .withImpactSound(JSoundRegistry.IMPACT_3.get())
            .withHitSpark(JParticleType.HIT_SPARK_2)
            .withInfo(
                    Component.literal("Assault"),
                    Component.literal("medium windup, good stun"));
    public static final SimpleAttack<CreamEntity> GRAB_HIT = new SimpleAttack<CreamEntity>(0, 13, 20,
            1f, 6f, 5, 2f, 1.5f, 0f)
            .withImpactSound(JSoundRegistry.IMPACT_1.get())
            .withLaunch()
            .withHitSpark(JParticleType.HIT_SPARK_2)
            .withInfo(
                    Component.literal("Grab (Hit)"),
                    Component.empty());
    public static final GrabAttack<CreamEntity, State> GRAB = new GrabAttack<>(320, 8, 20,
            1f, 3f, 30, 1.5f, 0f, 0f, GRAB_HIT, State.GRAB_HIT)
            .withSound(JSoundRegistry.CREAM_GRAB.get())
            .withInfo(
                    Component.literal("Grab"),
                    Component.literal("unblockable, knocks back"));
    public static final SurpriseMove SURPRISE = new SurpriseMove(300, 14, 24, 1f)
            .withSound(JSoundRegistry.CREAM_SUMMON.get())
            .withInitAction((attacker, user, ctx) -> {
                Vec3 rotVec = user.getLookAngle();
                if (user.isShiftKeyDown()) {
                    attacker.getMoveContext().set(OUT_POS, user.position().add(rotVec).toVector3f());
                } else {
                    final Vec3 eyePos = user.getEyePosition();
                    HitResult hitResult = attacker.level().clip(new ClipContext(eyePos, eyePos.add(rotVec.scale(16)),
                            ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, user));
                    attacker.getMoveContext().set(OUT_POS, hitResult.getLocation().toVector3f());
                }
            })
            .withAction((attacker, user, ctx, targets) -> {
                final Vector3f outDir = GravityChangerAPI.getGravityDirection(attacker).step();
                outDir.mul(-1f);
                ctx.set(OUT_DIR, outDir);
            })
            .withInfo(
                    Component.literal("Surprise"),
                    Component.literal("""
                            Cream disappears into the ground, then pops out in a nearby looked location.
                            If used while crouching, Cream appears in front of the user.
                            """));
    public static final ChargeBarrageAttack<CreamEntity> CHARGE = new ChargeBarrageAttack<CreamEntity>(200, 15, 30,
            4f, 2f, 10, 1.5f, 0.5f, 0f, 3, false)
            .withAction(
                    (attacker, user, ctx, targets) ->
                            targets.forEach(target -> target.addEffect(
                                    new MobEffectInstance(JStatusRegistry.KNOCKDOWN.get(), 25, 0, true, false)
                                    )
                            )

            )
            .withLaunchNoShockwave()
            .withImpactSound(JSoundRegistry.IMPACT_5.get())
            .withBlockableType(BlockableType.NON_BLOCKABLE)
            .withInfo(
                    Component.literal("Charge"),
                    Component.literal("4 block range, unblockable knockdown"));
    public static final DestroyAttack DESTROY = new DestroyAttack(320, 21, 30, 1f,
            8f, 5, 2f, 1.25f, 0f)
            .withCrouchingVariant(CHARGE)
            .withSound(JSoundRegistry.CREAM_OVERHEAD.get())
            .withImpactSound(JSoundRegistry.IMPACT_5.get())
            .withLaunch()
            .withHyperArmor()
            .withBlockableType(BlockableType.NON_BLOCKABLE)
            .withInfo(
                    Component.literal("Destroy"),
                    Component.literal("slow, uninterruptible, unblockable knockdown"));
    public static final ConsumeAttack CONSUME = new ConsumeAttack(640, 35, 40, 1f,
            2f, 0, 2f, 0f, 0f)
            .withSound(JSoundRegistry.CREAM_CONSUME.get())
            .withInfo(
                    Component.literal("Void"),
                    Component.literal("high windup, 6 seconds"));
    public static final BallModeMove ENTER = new BallModeMove(40, 10, 15, 0f, true)
            .withSound(JSoundRegistry.CREAM_ENTER.get())
            .withInfo(
                    Component.literal("Enter Cream"),
                    Component.literal("Cream consumes itself and the user halfway, increasing mobility and decreasing defense"));
    public static final BallModeMove EXIT = new BallModeMove(40, 5, 15, 0f, false)
            .withSound(JSoundRegistry.CREAM_EXIT.get())
            .withInfo(
                    Component.literal("Exit Cream"),
                    Component.literal("Cream and its user return from the void"));
    public static final SimpleAttack<CreamEntity> SWIPE = new SimpleAttack<CreamEntity>(20, 7,
            14, 0.5f, 5f, 20, 2f, 0.75f, 0.2f)
            .withImpactSound(JSoundRegistry.IMPACT_3.get())
            .withHitAnimation(CommonHitPropertyComponent.HitAnimation.HIGH)
            .withInfo(
                    Component.literal("Swipe"),
                    Component.literal("quick air-to-ground poke"));
    public static final KnockdownAttack<CreamEntity> OVERHEAD_SMASH = new KnockdownAttack<CreamEntity>(160,
            14, 20, 0.5f, 9f, 15, 2f, 1.25f, 0.3f, 35)
            .withSound(JSoundRegistry.CREAM_SMASH.get())
            .withImpactSound(JSoundRegistry.TW_KICK_HIT.get())
            .withHitSpark(JParticleType.HIT_SPARK_3)
            .withHyperArmor()
            .withLaunch()
            .withInfo(
                    Component.literal("Overhead Smash"),
                    Component.literal("slow, uninterruptible launcher"));
    public static final SimpleMultiHitAttack<CreamEntity> BALL_COMBO = new SimpleMultiHitAttack<CreamEntity>(200,
            36, 0.5f, 7f, 15, 2f, 0.1f, 0.3f, IntSet.of(10, 17, 25))
            .withSound(JSoundRegistry.CREAM_COMBO.get())
            .withImpactSound(JSoundRegistry.IMPACT_3.get())
            .withHitAnimation(CommonHitPropertyComponent.HitAnimation.HIGH)
            .withInfo(
                    Component.literal("Aerial Assault"),
                    Component.literal("less stun than grounded version"));
    public static final BallChargeAttack BALL_CHARGE = new BallChargeAttack(300, 13, 28, 1f)
            .withSound(JSoundRegistry.CREAM_BALLDASH.get())
            .withInfo(
                    Component.literal("Void Charge"),
                    Component.literal("Cream quickly transforms into a black hole and charges in the pointed direction"));
    public static final SurpriseMove DETACH_CHARGE = new SurpriseMove(300, 13, 28, 1f)
            .withSound(JSoundRegistry.CREAM_BALLDASH.get())
            .withInitAction((attacker, user, ctx) -> {
                attacker.endHalfBall();
                attacker.getMoveContext().set(OUT_POS, user.position().toVector3f());
                ctx.set(OUT_DIR, user.getLookAngle().scale(0.75).toVector3f());
            })
            .withInfo(
                    Component.literal("Detaching Void Charge"),
                    Component.literal("""
                            Cream quickly transforms into a black hole and charges in the pointed direction.
                            The user exits cream upon performing this move."""));
    public static final BallChargeAttack BALL_DESTROY = new BallChargeAttack(300, 13, 28, 1f)
            .withSound(JSoundRegistry.CREAM_BALLDASH.get())
            .withInfo(
                    Component.literal("Destroy"),
                    Component.literal("Cream quickly transforms into a black hole and charges in a downward curve"));

    private static final EntityDataAccessor<Integer> VOID_TIME;
    private static final EntityDataAccessor<Boolean> HALF_BALL;
    @Setter
    private Vec3 chargeDir;
    @Getter
    @Setter
    private boolean charging = false;

    static {
        VOID_TIME = SynchedEntityData.defineId(CreamEntity.class, EntityDataSerializers.INT);
        HALF_BALL = SynchedEntityData.defineId(CreamEntity.class, EntityDataSerializers.BOOLEAN);
    }

    public CreamEntity(Level worldIn) {
        super(StandType.CREAM, worldIn, JSoundRegistry.CREAM_SUMMON.get());

        idleRotation = 220f;

        proCount = 4;
        conCount = 3;

        freespace = """
                BNBs (i. - in Cream):
                    Light>Assault>Light>Grab
                    i.Light>land+s.OFF>s.ON+Assault>Light>Charge>Grab
                    Chop>Destroy>Surprise
                    Chop>Void""";

        auraColors = new Vector3f[]{
                new Vector3f(0.5f, 0.1f, 0.3f),
                new Vector3f(0.5f, 0.6f, 0.8f),
                new Vector3f(1.0f, 0.5f, 0.7f),
                new Vector3f(1.0f, 0.5f, 0.5f)
        };
    }

    @Override
    public Vector3f getAuraColor() {
        if (getVoidTime() > 0) {
            return new Vector3f();
        }
        return super.getAuraColor();
    }

    public void beginHalfBall() {
        entityData.set(HALF_BALL, true);
        idleDistance = 0f;
        blockDistance = 0f;
        maxStandGauge = 45f;

        registerMoves();
    }

    public void endHalfBall() {
        entityData.set(HALF_BALL, false);
        idleDistance = 1.25f;
        blockDistance = 0.75f;
        maxStandGauge = 90f;

        registerMoves();
    }

    public boolean isHalfBall() {
        return entityData.get(HALF_BALL);
    }

    public int getVoidTime() {
        return entityData.get(VOID_TIME);
    }

    public void setVoidTime(int vTime) {
        entityData.set(VOID_TIME, vTime);
        if (vTime == 0) {
            setReset(true);
        }
    }

    @Override
    protected void registerMoves(MoveMap<CreamEntity, State> moves) {
        if (isHalfBall()) {
            moves.register(MoveType.LIGHT, SWIPE, State.BALL_LIGHT);

            moves.register(MoveType.HEAVY, OVERHEAD_SMASH, State.BALL_HEAVY);
            moves.register(MoveType.BARRAGE, BALL_COMBO, State.BALL_COMBO);

            moves.register(MoveType.SPECIAL1, BALL_CHARGE, State.BALL_CONSUME);
            moves.register(MoveType.SPECIAL2, DETACH_CHARGE, State.BALL_CONSUME);
            moves.register(MoveType.SPECIAL3, BALL_DESTROY, State.BALL_CONSUME);

            moves.register(MoveType.UTILITY, EXIT, State.EXIT);
        } else {
            moves.registerImmediate(MoveType.LIGHT, PUNCH, State.LIGHT);

            moves.register(MoveType.HEAVY, VERTICAL_CHOP, State.HEAVY);
            moves.register(MoveType.BARRAGE, COMBO, State.COMBO);

            moves.register(MoveType.SPECIAL1, GRAB, State.GRAB);
            moves.register(MoveType.SPECIAL2, SURPRISE, State.SURPRISE);
            moves.register(MoveType.SPECIAL3, DESTROY, State.DESTROY).withCrouchingVariant(State.CHARGE);

            moves.register(MoveType.UTILITY, ENTER, State.ENTER);
        }

        moves.register(MoveType.ULTIMATE, CONSUME, State.CONSUME);
    }

    @Override
    public boolean initMove(MoveType type) {
        if (tryFollowUp(type, MoveType.LIGHT)) return true;
        return super.initMove(type);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        getEntityData().define(VOID_TIME, 0);
        getEntityData().define(HALF_BALL, false);
    }

    @Override
    public boolean canAttack() {
        if (hasUser() && !(getUser() instanceof Player) && getVoidTime() > 0) {
            return false; // Prevents mobs from attacking while in void state and cancelling void early
        }
        return super.canAttack();
    }

    @Override
    public boolean shouldOffsetHeight() {
        if (isHalfBall()) {
            return false;
        }
        return super.shouldOffsetHeight();
    }

    @Override
    protected @NonNull AABB makeBoundingBox() {
        final double x = getX(), y = getY(), z = getZ();

        if (isHalfBall()) {
            return new AABB(x - 0.6, y + 0.0, z - 0.6, x + 0.6, y + 1.4, z + 0.6);
        }
        if (getState() == State.SURPRISE) {
            return new AABB(x - 0.6, y + 0, z - 0.6, x + 0.6, y + 0.3, z + 0.6);
        }
        return super.makeBoundingBox();
    }

    @Override
    public void desummon() {
        // Stop voiding if voiding
        if (this.getVoidTime() > 0) {
            this.setVoidTime(0);
            return;
        }

        // Real desummon if not voiding
        super.desummon();
    }

    @Override
    public boolean defaultToNear() {
        if (charging) {
            return false;
        }
        return super.defaultToNear();
    }

    @Override
    public void tick() {
        super.tick();
        boolean server = !level().isClientSide();

        if (!hasUser()) {
            return;
        }
        final LivingEntity user = getUserOrThrow();
        final boolean userIsPlayer;
        boolean notCreativeOrSpectator = false;

        final Vec3 pos = getEyePosition();
        int voidTime = getVoidTime();
        boolean voiding = (voidTime > 0);

        // Players get creative flight, and mobs get gravity removed and y level equalization with target; see: handleAIVoid()
        if (user instanceof Player playerEntity) {
            notCreativeOrSpectator = (!playerEntity.isCreative() && !playerEntity.isSpectator());
            if (notCreativeOrSpectator && !charging && !isFree()) {
                playerEntity.getAbilities().flying = voiding;
            }
            userIsPlayer = true;
        } else {
            userIsPlayer = false;
        }

        if (server) {
            if (!charging) {
                if (getCurrentMove() != null) {
                    setVoidTime(0);
                    resetAlphaOverride();
                    voiding = false;
                }
                idleOverride = getVoidTime() > 0;
            }

            user.setInvulnerable(getVoidTime() > 0);
        }

        if (voiding) {
            if (server) {
                if (level().getGameRules().getBoolean(JCraft.STAND_GRIEFING)) {
                    final BlockPos blockPos = blockPosition();
                    // Unfun 3x4x3 void code
                    for (int x = -1; x < 2; x++) {
                        for (int y = -1; y < 3; y++) {
                            for (int z = -1; z < 2; z++) {
                                final BlockPos curPos = blockPos.offset(x, y, z);
                                if (level().getBlockState(curPos).getBlock().getExplosionResistance() > 100.1f) {
                                    continue;
                                }
                                level().setBlockAndUpdate(curPos, Block.stateById(0));
                            }
                        }
                    }
                }

                // Blind normal players while in void
                if (notCreativeOrSpectator && !isFree()) {
                    user.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 25, 0, false, false));
                }

                if (charging) {
                    if (isFree()) { // Surprise move
                        final Vector3f newPos = getFreePos();
                        newPos.add(getMoveContext().get(SurpriseMove.OUT_DIR));
                        setFreePos(newPos);
                        if (getMoveStun() == 1) {
                            setFree(false);
                        }
                    } else if (chargeDir != null) { // Void Charge move
                        user.setDeltaMovement(chargeDir);
                        user.hurtMarked = true;
                        if (user instanceof ServerPlayer player) {
                            player.connection.send(new ClientboundSetEntityMotionPacket(user));
                        }

                        if (getCurrentMove() != null && getCurrentMove().getOriginalMove() == BALL_DESTROY) {
                            chargeDir = chargeDir.add(
                                    new Vec3(GravityChangerAPI.getGravityDirection(user).step()).scale(0.1)
                            ).normalize().scale(0.5);
                        }
                    }
                } else { // Ultimate
                    setStateNoReset(State.IDLE);

                    if (!userIsPlayer) {
                        handleAIVoid(user, voidTime);
                    }
                }

                final AABB damageBox = new AABB(pos.add(1.5, 1.5, 1.5), pos.subtract(1.5, 1.5, 1.5));
                final List<Entity> toDamage = level().getEntitiesOfClass(Entity.class,
                        damageBox, EntitySelector.ENTITY_STILL_ALIVE);
                JUtils.displayHitbox(level(), damageBox);

                toDamage.remove(user);
                toDamage.remove(this);

                boolean hurt;
                int stun = 2;
                float damage = 1.5f;
                if (charging) {
                    hurt = getMoveStun() % 2 == 0; // More consistent
                    stun = 4;
                    damage = 5.0f;
                } else {
                    hurt = tickCount % 4 == 0;

                    setAlphaOverride(0);
                }

                for (Entity ent : toDamage) {
                    if (ent instanceof ItemEntity) {
                        ent.discard();
                        continue;
                    }
                    if (ent instanceof LivingEntity livingEntity) {
                        if (hurt) {
                            JCraft.stun(livingEntity, stun, 0, user);
                            JUtils.cancelMoves(livingEntity);
                        }

                        livingEntity.hurt(level().damageSources().fellOutOfWorld(), damage);
                    }
                }

                voidTime--;
                if (voidTime < 1) {
                    resetAlphaOverride();
                }
                setVoidTime(voidTime);
                setDistanceOffset(0);
            } else {
                for (int i = 0; i < 16; i++) {
                    level().addParticle(ParticleTypes.MYCELIUM,
                            pos.x + (random.nextFloat() - 0.5f) * 2f,
                            pos.y + (random.nextFloat() - 0.5f) * 2f,
                            pos.z + (random.nextFloat() - 0.5f) * 2f,
                            0, 0, 0);
                }
            }
        } else { // Not voiding
            if (isIdle() && charging) {
                charging = false;
                setFree(false);
            }

            if (!isHalfBall()) {
                return;
            }
            setAlphaOverride(0.1f);
            user.resetFallDistance();
            user.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 5, 9, true, false));

            // Player Half-Ball controls
            if (user instanceof ServerPlayer serverPlayer) {
                if (serverPlayer.isFallFlying()) {
                    serverPlayer.stopFallFlying();
                }

                if (lastRemoteInputTime - tickCount > 4) {
                    updateRemoteInputs(0, 0, false, false);
                }

                Vec3 finalSpeed = Vec3.ZERO;
                if (!blocking && !user.hasEffect(JStatusRegistry.DAZED.get())) {
                    final Vec3 gravityVec = new Vec3(GravityChangerAPI.getGravityDirection(this).step());

                    final Vec3 userVel = JUtils.deltaPos(user);
                    final Vec3 userPos = user.position();
                    final Vec3 groundPos = level().clip(
                            new ClipContext(
                                    userPos, userPos.add(gravityVec.scale(24)),
                                    ClipContext.Block.COLLIDER, ClipContext.Fluid.SOURCE_ONLY, user)).getLocation();

                    double groundDist = groundPos.distanceTo(pos);
                    if (groundDist < 2) {
                        groundDist = 2; // Prevents extremely high jumps
                    }
                    final Vec3 stabilization = userVel.multiply(gravityVec).scale(10 / groundDist);

                    if (getRemoteJumpInput()) {
                        user.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 10, 2, true, false));
                        if (groundDist < 5) {
                            GravityChangerAPI.addWorldVelocity(user, stabilization.subtract(gravityVec.scale(0.25 / groundDist)));
                        }
                    }

                    final Vec3 rotVec = Vec3.directionFromRotation(getXRot(), getYRot());
                    Vec3 moveRotVec = Vec3.ZERO;
                    float forward = (float) getRemoteForwardInput();
                    if (forward != 0) {
                        moveRotVec = moveRotVec.add(rotVec.scale(forward)); // Forward movement
                    }
                    float side = (float) getRemoteSideInput();
                    if (side != 0) {
                        moveRotVec = moveRotVec.add(rotVec.yRot(1.57079632679f * side)); // Side movement
                    }

                    finalSpeed = finalSpeed.add(moveRotVec.normalize().scale(0.034));

                    user.push(finalSpeed.x, finalSpeed.y, finalSpeed.z);
                    user.hurtMarked = true;
                }
            } else {
                resetAlphaOverride();
            }
        }
    }

    private void handleAIVoid(LivingEntity user, int voidTime) {
        double y = user.getY();
        Vec3 vel = new Vec3(user.getDeltaMovement().x, 0.0, user.getDeltaMovement().z);

        // Targeting priority
        var damageRecord = user.getCombatTracker().getMostSignificantFall();
        Entity targetEntity = null;
        if (damageRecord != null) {
            targetEntity = damageRecord.source().getEntity();
        }
        if (targetEntity == null && user instanceof Mob mob) {
            targetEntity = mob.getTarget();
        }
        if (targetEntity == null) {
            targetEntity = user.getLastHurtByMob();
        }

        // If target wasn't found, thrash around
        Vec3 target = targetEntity != null ? targetEntity.position() : this.position().add(Math.sin(this.tickCount * 0.2) * 2, Math.sin(this.tickCount * 0.2) / 4, Math.cos(this.tickCount * 0.2) * 2);

        final double dY = Mth.clamp(target.y() - y, -1, 1);
        y += dY;

        vel = vel.add(target.subtract(user.position().add(random.nextDouble() * 2, random.nextDouble() * 3, random.nextDouble() * 3)).normalize()).scale(0.3);

        user.setDeltaMovement(vel);
        user.setPosRaw(user.getX(), y, user.getZ());

        if (voidTime < 10) {
            user.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 5, 1, true, false));
        }
    }

    @Override
    @NonNull
    public CreamEntity getThis() {
        return this;
    }

    // Animation code
    public enum State implements StandAnimationState<CreamEntity> {
        IDLE((cream, builder) -> builder.setAnimation(RawAnimation.begin().thenLoop("animation.cream." + (cream.getVoidTime() > 0 ? "void" : cream.isHalfBall() ? "ball" : "") + "idle"))),
        LIGHT(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.cream.light"))),
        LIGHT_FOLLOWUP(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.cream.light_followup"))),
        BALL_LIGHT(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.cream.balllight"))),
        BLOCK(builder -> builder.setAnimation(RawAnimation.begin().thenLoop("animation.cream.block"))),
        BALL_BLOCK(builder -> builder.setAnimation(RawAnimation.begin().thenLoop("animation.cream.ballblock"))),
        HEAVY(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.cream.heavy"))),
        BALL_HEAVY(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.cream.ballheavy"))),
        COMBO(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.cream.combo"))),
        BALL_COMBO(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.cream.ballcombo"))),
        CONSUME(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.cream.consume"))),
        BALL_CONSUME(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.cream.ballconsume"))),
        SURPRISE(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.cream.surprise"))),
        CHARGE(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.cream.charge"))),
        GRAB(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.cream.grab"))),
        GRAB_HIT(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.cream.grab_hit"))),
        ENTER(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.cream.enter"))),
        EXIT(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.cream.exit"))),
        DESTROY(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.cream.destroy"))),
        BITE(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.cream.bite")));

        private final BiConsumer<CreamEntity, AnimationState<CreamEntity>> animator;

        State(Consumer<AnimationState<CreamEntity>> animator) {
            this((creamEntity, builder) -> animator.accept(builder));
        }

        State(BiConsumer<CreamEntity, AnimationState<CreamEntity>> animator) {
            this.animator = animator;
        }

        @Override
        public void playAnimation(CreamEntity attacker, AnimationState<CreamEntity> state) {
            animator.accept(attacker, state);
        }
    }

    @Override
    protected State[] getStateValues() {
        return State.values();
    }

    @Override
    protected String getSummonAnimation() {
        return "animation.cream.summon";
    }

    @Override
    public State getIdleState() {
        return State.IDLE;
    }

    @Override
    public State getBlockState() {
        return isHalfBall() ? State.BALL_BLOCK : State.BLOCK;
    }
}
