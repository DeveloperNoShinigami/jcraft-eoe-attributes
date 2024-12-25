package net.arna.jcraft.common.entity.stand;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import mod.azure.azurelib.core.animation.AnimationState;
import mod.azure.azurelib.core.animation.RawAnimation;
import net.arna.jcraft.common.attack.actions.CancelSpecMoveAction;
import net.arna.jcraft.common.attack.actions.EffectAction;
import net.arna.jcraft.common.attack.actions.MetallicaAddIronAction;
import net.arna.jcraft.common.attack.actions.UserAnimationAction;
import net.arna.jcraft.common.attack.conditions.MetallicaIronCondition;
import net.arna.jcraft.common.attack.core.MoveClass;
import net.arna.jcraft.common.attack.core.MoveInputType;
import net.arna.jcraft.common.attack.core.MoveMap;
import net.arna.jcraft.common.attack.core.StunType;
import net.arna.jcraft.common.attack.core.data.MoveSet;
import net.arna.jcraft.common.attack.core.data.StateContainer;
import net.arna.jcraft.common.attack.moves.metallica.*;
import net.arna.jcraft.common.attack.moves.shared.*;
import net.arna.jcraft.common.component.living.CommonHitPropertyComponent;
import net.arna.jcraft.common.component.living.CommonMiscComponent;
import net.arna.jcraft.common.entity.projectile.MetallicaForksEntity;
import net.arna.jcraft.common.entity.projectile.ScalpelProjectile;
import net.arna.jcraft.common.gravity.api.GravityChangerAPI;
import net.arna.jcraft.common.util.JParticleType;
import net.arna.jcraft.common.util.StandAnimationState;
import net.arna.jcraft.platform.JComponentPlatformUtils;
import net.arna.jcraft.registry.JSoundRegistry;
import net.arna.jcraft.registry.JStatusRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * The {@link StandEntity} for <a href="https://jojowiki.com/Metallica">Metallica</a>.
 * @see StandType#METALLICA
 * @see net.arna.jcraft.client.model.entity.stand.MetallicaModel MetallicaModel
 * @see net.arna.jcraft.client.renderer.entity.stands.MetallicaRenderer MetallicaRenderer
 * @see HarvestMove
 */
public class MetallicaEntity extends StandEntity<MetallicaEntity, MetallicaEntity.State> {
    public static final MoveSet<MetallicaEntity, State> MOVE_SET = MoveSet.create(StandType.METALLICA,
            MetallicaEntity::registerMoves, State.class);

    public static final EntityDataAccessor<Optional<BlockPos>> SIPHON_POS = SynchedEntityData.defineId(MetallicaEntity.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);
    public static final EntityDataAccessor<Float> IRON = SynchedEntityData.defineId(MetallicaEntity.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Boolean> INVISIBLE = SynchedEntityData.defineId(MetallicaEntity.class, EntityDataSerializers.BOOLEAN);
    public static final float IRON_MAX = 80.0f;

    public static final SimpleAttack<MetallicaEntity> LIGHT_LAUNCH = new SimpleAttack<MetallicaEntity>(0,
            18, 22, 0.75f, 5f, 6,1.7f,  1.25f, 0.2f)
            .withLaunch()
            .withHitSpark(JParticleType.HIT_SPARK_2)
            .withInfo(
                    Component.literal("Launch"),
                    Component.empty()
            );
    public static final SimpleAttack<MetallicaEntity> LIGHT_FOLLOWUP_2 = new SimpleAttack<MetallicaEntity>(0,
                    12, 22, 0.75f, 3f, 10,1.6f,  0.25f, 0.2f)
            .withAnim(State.LIGHT_FINAL)
            .withImpactSound(JSoundRegistry.IMPACT_9)
            .withFinisher(16, LIGHT_LAUNCH)
            .withInfo(
                    Component.literal("Impale"),
                    Component.literal("quick combo starter")
            );
    public static final SimpleAttack<MetallicaEntity> LIGHT_FOLLOWUP = SimpleAttack.<MetallicaEntity>lightAttack(
                    6, 15, 0.75f, 4f, 14, 0.25f, 0.2f)
            .withAnim(State.LIGHT_FOLLOWUP)
            .withFollowup(LIGHT_FOLLOWUP_2)
            .withImpactSound(SoundEvents.PLAYER_ATTACK_SWEEP)
            .withInfo(
                    Component.literal("Slice (2nd Hit)"),
                    Component.literal("quick combo starter")
            );
    public static final SimpleAttack<MetallicaEntity> LIGHT = SimpleAttack.<MetallicaEntity>lightAttack(
                    6, 10, 0.75f, 4f, 11, 0.15f, 0.2f)
            .withFollowup(LIGHT_FOLLOWUP)
            // .withCrouchingVariant(CROUCHING_LIGHT)
            // .withAerialVariant(AIR_LIGHT)
            .withImpactSound(SoundEvents.PLAYER_ATTACK_SWEEP)
            .withInfo(
                    Component.literal("Slice"),
                    Component.literal("quick combo starter")
            );
    public static final BarrageAttack<MetallicaEntity> BARRAGE = new BarrageAttack<MetallicaEntity>(240, 0,
            30, 0.75f, 0.8f, 20, 1.6f, 0.25f, 0f, 3)
            // .withSound(JSoundRegistry.METALLICA_BARRAGE)
            .withHitSpark(JParticleType.HIT_SPARK_1)
            .withImpactSound(JSoundRegistry.IMPACT_9)
            .withInfo(
                    Component.literal("Barrage"),
                    Component.literal("fast reliable combo starter/extender, high stun, smaller hitbox than most barrages")
            )
            .withInitAction(UserAnimationAction.play("mtl.brg"));
    public static final KnockdownAttack<MetallicaEntity> SWEEP = new KnockdownAttack<MetallicaEntity>(40,
            7, 14, 0.75f, 5f, 8, 1.5f, 0.3f, 0.4f, 35)
            .withImpactSound(SoundEvents.PLAYER_ATTACK_SWEEP)
            .withHitSpark(JParticleType.SWEEP_ATTACK)
            .withHitAnimation(CommonHitPropertyComponent.HitAnimation.LOW)
            .withExtraHitBox(1.75, -0.4, 0.85)
            .withStaticY()
            .withInfo(
                    Component.literal("Sweep"),
                    Component.literal("""
                            Fast 1.5s knockdown.
                            §1Requires at least 25% iron to be usable.""")
            )
            .withInitAction(UserAnimationAction.play("mtl.swp"))
            .withCondition(MetallicaIronCondition.atLeast(IRON_MAX / 4.0f));
    public static final SimpleAttack<MetallicaEntity> CLEAVE = new SimpleAttack<MetallicaEntity>(0,
            12, 21, 1.5f, 6.5f, 11,2.5f,  2.0f, 0.2f)
            .withAnim(State.CLEAVE)
            .withLaunch()
            .withImpactSound(JSoundRegistry.IMPACT_1)
            .withHitSpark(JParticleType.HIT_SPARK_3)
            .withHitAnimation(CommonHitPropertyComponent.HitAnimation.CRUSH)
            .withExtraHitBox(3.0, 0.5, 1.5)
            .withInfo(
                    Component.literal("Cleave"),
                    Component.literal("""
                            Interruptible, very far-reaching followup.""")
            )
            .withInitAction(UserAnimationAction.play("mtl.clv"))
            .withCondition(MetallicaIronCondition.atLeast(IRON_MAX / 2.0f));
    public static final SimpleUppercutAttack<MetallicaEntity> SMASH = new SimpleUppercutAttack<MetallicaEntity>(200,
            11, 21, 1.0f, 7.5f, 18,2.0f,  2.0f, 0.2f, -0.5f)
            .withCrouchingVariant(SWEEP)
            .withFollowup(CLEAVE)
            .withImpactSound(JSoundRegistry.IMPACT_1)
            .withHitSpark(JParticleType.HIT_SPARK_3)
            .withHitAnimation(CommonHitPropertyComponent.HitAnimation.CRUSH)
            .withHyperArmor()
            .withExtraHitBox(2.0, 0.5, 1.5)
            .withInfo(
                    Component.literal("Smash"),
                    Component.literal("""
                            Uninterruptible combo starter.
                            Very far-reaching.
                            §1Requires at least 50% iron to be usable.""")
            )
            .withInitAction(UserAnimationAction.play("mtl.sms"))
            .withCondition(MetallicaIronCondition.atLeast(IRON_MAX / 2.0f));
    public static final FanTossAttack FAN_TOSS = new FanTossAttack(60, 7, 12, 0.75f)
            .withInfo(
                    Component.literal("Scalpel Toss (Precise)"),
                    Component.literal("""
                                    Decently fast, very low cooldown.
                                    Fires 5 scalpels in a fan pattern.""")
            )
            .withInitAction(UserAnimationAction.play("mtl.ft"))
            .withCondition(MetallicaIronCondition.atLeast(ScalpelProjectile.IRON_COST));
    public static final PreciseTossAtack PRECISE_TOSS = new PreciseTossAtack(60, 7, 12, 0.75f)
            .withCrouchingVariant(FAN_TOSS)
            .withInfo(
                    Component.literal("Scalpel Toss (Precise)"),
                    Component.literal("""
                                    Decently fast, very low cooldown.
                                    Fires 3 scalpels in the exact pointed direction.
                                    Scalpels disappear after 15s in the ground, and may be picked up to regain iron.""")
            )
            .withInitAction(UserAnimationAction.play("mtl.pt"))
            .withCondition(MetallicaIronCondition.atLeast(ScalpelProjectile.IRON_COST));
    public static final SummonForksAttack SUMMON_FORKS = new SummonForksAttack(0, 5, 15)
            .withInfo(
                    Component.literal("Summon Pitchforks"),
                    Component.literal("""
                            12 meter range.
                            Summons pitchforks from the ground that are guaranteed to knock down the opponent.""")
            )
            .withInitAction(UserAnimationAction.play("mtl.sfk"))
            .withCondition(MetallicaIronCondition.atLeast(MetallicaForksEntity.IRON_COST));
    public static final InternalAttack INTERNAL_ATTACK = new InternalAttack(0, 10, 15)
            .withCrouchingVariant(SUMMON_FORKS)
            .withInfo(
                    Component.literal("Internal Attack"),
                    Component.literal("""
                            12 meter range.
                            Uses the opponent's own iron to create a deadly remote attack.
                            Applies Hypoxia for 3 seconds.
                            Cannot attack hypoxic targets.
                            This attack does not interrupt other moves.""")
            )
            .withInitAction(UserAnimationAction.play("mtl.ita"));
    public static final SimpleAttack<MetallicaEntity> GRAB_HIT_FINAL = new SimpleAttack<MetallicaEntity>(0,
            18, 24, 0.5f, 4f, 9, 2f, 1.2f, 0f)
            // .withImpactSound(JSoundRegistry.IMPACT_1)
            .withAction(EffectAction.inflict(JStatusRegistry.HYPOXIA.get(), 200, 0, false, true))
            .withHitSpark(JParticleType.HIT_SPARK_2)
            .withLaunch()
            .withInfo(
                    Component.literal("Grab (Final Hit)"),
                    Component.empty()
            )
            .withAction(MetallicaAddIronAction.addIron(15.0f));
    public static final SimpleAttack<MetallicaEntity> GRAB_HIT = new SimpleAttack<MetallicaEntity>(0,
            13, 24, 0.5f, 4f, 10, 2f, 0f, 0f)
            .withStunType(StunType.UNBURSTABLE)
            .withInfo(
                    Component.literal("Grab (Second Hit)"),
                    Component.empty())
            .withFinisher(14, GRAB_HIT_FINAL)
            .withAction(MetallicaAddIronAction.addIron(10.0f))
            .withInitAction(UserAnimationAction.play("mtl.grab_hit").force());
    public static final GrabAttack<MetallicaEntity, State> GRAB = new GrabAttack<>(280,
            9, 20, 0.5f, 0, 15, 1.5f, 0, 0, GRAB_HIT,
            StateContainer.of(State.GRAB_HIT), 17, 0.4)
            .withInfo(
                    Component.literal("Grab"),
                    Component.literal("""
                            Unblockable, inflicts Hypoxia (10s).
                            Restores 30 iron.
                            Cannot be used alongside spec moves and will override them.""")
            )
            .withImpactSound(JSoundRegistry.IMPACT_9)
            .withInitAction(CancelSpecMoveAction.cancelSpecMove())
            .withInitAction(UserAnimationAction.play("mtl.grab"));
    public static final InvisibilityMove GO_INVISIBLE = new InvisibilityMove(20, 10, 15)
            .withInfo(
                    Component.literal("Invisibility"),
                    Component.literal("""
                            Projects a field of iron particles that reflect light away from the user.
                            Uses 10 iron per second.
                            Cannot be queued.""")
            )
            .withInitAction(UserAnimationAction.play("mtl.ivs"))
            .withCondition(MetallicaIronCondition.atLeast(10.0f));
    public static final HarvestMove HARVEST = new HarvestMove(60 * 20, 0.75f, 3)
            .withCrouchingVariant(GO_INVISIBLE)
            .withInfo(
                    Component.literal("Harvest Iron"),
                    Component.literal("""
                            Harvests 1 iron with a 0.15s interval from the looked at block.
                            3 times faster if harvesting from an iron block.
                            5m max range.
                            Cannot be queued.""")
            );
    public static final BisectAttack BISECT = new BisectAttack(0, 0, 10, 0.75f)
            .withInitAction(UserAnimationAction.play("mtl.bsc_fire"));
    public static final BisectChargeMove BISECT_CHARGE = new BisectChargeMove(30 * 20, 81, 80, 0.75f, 12)
            .withInfo(
                    Component.literal("Bisect"),
                    Component.literal("""
                            Chargeable projectile that consumes iron over time to become larger and more powerful.
                            Unblockable.""")
            )
            .withFollowup(BISECT)
            .withInitAction(UserAnimationAction.play("mtl.bsc"));
    private static final BlockParticleOption FAKE_BLOOD = new BlockParticleOption(ParticleTypes.BLOCK, Blocks.REDSTONE_WIRE.defaultBlockState());

    @Getter
    @Setter
    private int bisectChargeTime = 0;
    private CommonMiscComponent miscComponent;

    public MetallicaEntity(Level worldIn) {
        super(StandType.METALLICA, worldIn, JSoundRegistry.STAND_SUMMON.get());

        freespace = """
                Contains up to 80 units of iron.
                Requires iron to create objects used in attacks.""";
        idleDistance = 0;

        auraColors = new Vector3f[] {
                new Vector3f(0.1f, 0.1f, 0.4f),
                new Vector3f(0.5f, 0.2f, 0.3f),
                new Vector3f(0.8f, 0.8f, 0.5f),
                new Vector3f(0.8f, 0.2f, 0.6f),
        };
    }

    @Override
    public Vector3f getAuraColor() {
        if (isInvisible()) return new Vector3f(0.0f, 0.0f, 0.0f);
        return super.getAuraColor();
    }

    @Override
    public void setUser(@Nullable LivingEntity user) {
        super.setUser(user);
        if (user == null) return;
        miscComponent = JComponentPlatformUtils.getMiscData(getUser());
        setIron(miscComponent.getMetallicaIron());
    }

    private static void registerMoves(MoveMap<MetallicaEntity, MetallicaEntity.State> moves) {
        var light = moves.register(MoveClass.LIGHT, LIGHT, State.LIGHT);
        light.withFollowup(State.LIGHT_FOLLOWUP).withFollowup(State.LIGHT_FINAL);
        moves.register(MoveClass.BARRAGE, BARRAGE, State.BARRAGE);
        var heavy = moves.register(MoveClass.HEAVY, SMASH, State.SMASH);
        heavy.withFollowup(State.CLEAVE);
        heavy.withCrouchingVariant(State.SWEEP);

        moves.register(MoveClass.SPECIAL1, PRECISE_TOSS, State.PRECISE_TOSS).withCrouchingVariant(State.FAN_TOSS);
        moves.register(MoveClass.SPECIAL2, INTERNAL_ATTACK, State.NONE).withCrouchingVariant(State.NONE);
        moves.register(MoveClass.SPECIAL3, GRAB, State.NONE);

        moves.register(MoveClass.ULTIMATE, BISECT_CHARGE, State.BISECT).withFollowup(State.NONE);

        moves.register(MoveClass.UTILITY, HARVEST, State.HARVEST).withCrouchingVariant(State.NONE);
    }

    @Override
    public boolean initMove(MoveClass type) {
        if (tryFollowUp(type, MoveClass.LIGHT)) return true;
        if (tryFollowUp(type, MoveClass.HEAVY)) return true;
        return super.initMove(type);
    }

    @Override
    public void queueMove(MoveInputType type) {
        if (type == MoveInputType.UTILITY) return;
        super.queueMove(type);
    }

    @Override
    public boolean shouldOffsetHeight() {
        if (getState() == State.SWEEP) return false;
        return super.shouldOffsetHeight();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(SIPHON_POS, Optional.empty());
        entityData.define(IRON, IRON_MAX);
        entityData.define(INVISIBLE, false);
    }

    public float getIron() {
        return entityData.get(IRON);
    }

    public void setIron(float iron) {
        entityData.set(IRON, iron);
        miscComponent.setMetallicaIron(iron);
    }

    public void addIron(float add) {
        setIron(Mth.clamp(entityData.get(IRON) + add, 0f, IRON_MAX));
    }

    public boolean drainIron(float r) {
        float iron = getIron();
        if (iron < r) return false;
        setIron(iron - r);
        return true;
    }

    @Override
    public void tick() {
        super.tick();

        if (getState() != State.GRAB_HIT) return;

        final Vec3 toUser = getUserOrThrow().position().subtract(position()).normalize().scale(0.5);
        final Vec3 midVec = GravityChangerAPI.getEyeOffset(this).add(position());
        for (int i = 0; i < 3; i++) {
            level().addParticle(random.nextBoolean() ? ParticleTypes.ELECTRIC_SPARK : FAKE_BLOOD,
                    midVec.x + random.nextGaussian() * 0.2 - 0.1,
                    midVec.y + random.nextGaussian() * 0.2 - 0.1,
                    midVec.z + random.nextGaussian() * 0.2 - 0.1,
                    toUser.x, toUser.y, toUser.z
            );
        }
    }

    @Override
    public @Nullable Mob standUserPassiveAI() {
        final Mob mob = super.standUserPassiveAI();
        if (mob != null && hasUser()) {
            if (getIron() < IRON_MAX) {
                Objects.requireNonNull(getUser()).setShiftKeyDown(false);
                initMove(MoveClass.UTILITY);
            }
        }
        return mob;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource damageSource) {
        return !damageSource.is(DamageTypes.GENERIC_KILL) && !damageSource.is(DamageTypes.FELL_OUT_OF_WORLD);
    }

    @Override
    public boolean isInvisible() {
        return entityData.get(INVISIBLE);
    }

    @Override
    public @NonNull MetallicaEntity getThis() {
        return this;
    }

    public Optional<BlockPos> getSiphonPos() {
        return entityData.get(SIPHON_POS);
    }

    // Animations
    public enum State implements StandAnimationState<MetallicaEntity> {
        IDLE(builder -> builder.setAnimation(RawAnimation.begin().thenLoop("animation.metallica.idle"))),
        NONE(builder -> builder.setAnimation(RawAnimation.begin().thenLoop("animation.metallica.idle"))),
        BLOCK(builder -> builder.setAnimation(RawAnimation.begin().thenLoop("animation.metallica.block"))),
        LIGHT(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.metallica.light"))),
        LIGHT_FOLLOWUP(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.metallica.light2"))),
        LIGHT_FINAL(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.metallica.light3"))),
        PRECISE_TOSS(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.metallica.precise_toss"))),
        FAN_TOSS(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.metallica.fan_toss"))),
        HARVEST(builder -> builder.setAnimation(RawAnimation.begin().thenLoop("animation.metallica.harvest"))),
        BARRAGE(builder -> builder.setAnimation(RawAnimation.begin().thenLoop("animation.metallica.barrage"))),
        SMASH(builder -> builder.setAnimation(RawAnimation.begin().thenLoop("animation.metallica.smash"))),
        CLEAVE(builder -> builder.setAnimation(RawAnimation.begin().thenLoop("animation.metallica.cleave"))),
        SWEEP(builder -> builder.setAnimation(RawAnimation.begin().thenLoop("animation.metallica.sweep"))),
        GRAB_HIT(builder -> builder.setAnimation(RawAnimation.begin().thenLoop("animation.metallica.grab_hit"))),
        BISECT(builder -> builder.setAnimation(RawAnimation.begin().thenLoop("animation.metallica.bisect"))),
        ;

        private final Consumer<AnimationState<MetallicaEntity>> animator;

        State(Consumer<AnimationState<MetallicaEntity>> animator) {
            this.animator = animator;
        }

        @Override
        public void playAnimation(MetallicaEntity attacker, AnimationState<MetallicaEntity> builder) {
            animator.accept(builder);
        }
    }

    @Override
    protected MetallicaEntity.State[] getStateValues() {
        return MetallicaEntity.State.values();
    }

    @Override
    protected @Nullable String getSummonAnimation() {
        return "animation.metallica.summon";
    }

    @Override
    public MetallicaEntity.State getBlockState() {
        return MetallicaEntity.State.BLOCK;
    }
}
