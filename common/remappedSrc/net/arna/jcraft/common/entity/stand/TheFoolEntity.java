package net.arna.jcraft.common.entity.stand;

import it.unimi.dsi.fastutil.ints.IntSet;
import lombok.NonNull;
import net.arna.jcraft.common.attack.core.BlockableType;
import net.arna.jcraft.common.attack.core.MoveMap;
import net.arna.jcraft.common.attack.core.MoveType;
import net.arna.jcraft.common.attack.moves.base.AbstractMove;
import net.arna.jcraft.common.attack.moves.shared.EffectInflictingAttack;
import net.arna.jcraft.common.attack.moves.shared.SimpleAttack;
import net.arna.jcraft.common.attack.moves.shared.SimpleMultiHitAttack;
import net.arna.jcraft.common.attack.moves.thefool.*;
import net.arna.jcraft.common.component.living.CommonHitPropertyComponent;
import net.arna.jcraft.common.util.JParticleType;
import net.arna.jcraft.common.util.JUtils;
import net.arna.jcraft.common.util.StandAnimationState;
import net.arna.jcraft.registry.JBlockRegistry;
import net.arna.jcraft.registry.JSoundRegistry;
import net.arna.jcraft.registry.JStatusRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class TheFoolEntity extends StandEntity<TheFoolEntity, TheFoolEntity.State> {
    public static final SimpleMultiHitAttack<TheFoolEntity> DRILL = new SimpleMultiHitAttack<TheFoolEntity>(
            20, 14, 1.5f, 2.5f, 7, 1.5f, 0.2f, 0.25f, IntSet.of(5, 8, 11))
            .withAnim(State.DRILL)
            .withBlockStun(4)
            .withHitAnimation(CommonHitPropertyComponent.HitAnimation.LOW)
            .withExtraHitBox(1.75, -0.1, 0.75)
            .withInfo(
                    Component.literal("Drill"),
                    Component.literal("fast, multi-hitting combo starter, low stun and blockstun")
            );
    public static final SimpleAttack<TheFoolEntity> LIGHT_FOLLOWUP = new SimpleAttack<TheFoolEntity>(
            0, 9, 16, 1.5f, 6f, 9, 2f, 1.5f, 0)
            .withAnim(State.LIGHT_FOLLOWUP)
            .withImpactSound(JSoundRegistry.IMPACT_2.get())
            .withLaunch()
            .withBlockStun(4)
            .withExtraHitBox(0, 0.25, 1)
            .withHitSpark(JParticleType.HIT_SPARK_2)
            .withInfo(
                    Component.literal("Swipe"),
                    Component.literal("quick combo finisher")
            );
    public static final SimpleAttack<TheFoolEntity> LIGHT = new SimpleAttack<TheFoolEntity>(30, 7,
            14, 1.5f, 6, 15, 2, 0.5f, -0.1f)
            .withImpactSound(JSoundRegistry.IMPACT_2.get())
            .withExtraHitBox(0, 0.25, 1)
            .withFollowup(LIGHT_FOLLOWUP)
            .withCrouchingVariant(DRILL)
            .withInfo(
                    Component.literal("Swipe"),
                    Component.literal("slow, long-reaching poke")
            );
    public static final AirBarrageAttack AIR_BARRAGE = new AirBarrageAttack(240, 0, 30,
            1f, 1f, 10, 2f, 0.1f, 0f, 3)
            .withHitAnimation(CommonHitPropertyComponent.HitAnimation.HIGH)
            .withInfo(
                    Component.literal("Burn Rubber"),
                    Component.literal("slows down all movement, combo starter/extender")
            );
    public static final TFComboAttack COMBO = new TFComboAttack(200, 29, 1.5f, 4.5f,
            20, 1.75f, 0.1f, -0.1f, IntSet.of(6, 14, 18, 19))
            .withAerialVariant(AIR_BARRAGE)
            .withImpactSound(JSoundRegistry.IMPACT_2.get())
            .withExtraHitBox(0.5, 0, 1.25)
            .withHitSpark(JParticleType.HIT_SPARK_2)
            .withInfo(
                    Component.literal("3-hit Combo"), Component.literal("fast knockdown provider"));
    public static final EffectInflictingAttack<TheFoolEntity> LAUNCH = new EffectInflictingAttack<TheFoolEntity>(240,
            16, 20, 1.25f, 8f, 25, 2f, 0.5f, -0.3f,
            List.of(new MobEffectInstance(MobEffects.LEVITATION, 5, 19, true, false)))
            .withSound(JSoundRegistry.FOOL_LAUNCH.get())
            .withInitAction((attacker, user, ctx) -> attacker.setSand(true))
            .withExtraHitBox(1.5)
            .withHitSpark(JParticleType.HIT_SPARK_3)
            .withHyperArmor()
            .withHitAnimation(CommonHitPropertyComponent.HitAnimation.CRUSH)
            .withInfo(
                    Component.literal("Launch"),
                    Component.literal("uninterruptible, slow, vertically launching uppercut")
            );
    public static final SlamAttack SLAM = new SlamAttack(0, 4, 10, 1.25f, 4f,
            24, 2f, 0.2f, 0.1f)
            .withBlockStun(5)
            .withSound(JSoundRegistry.FOOL_BARK1.get())
            .withImpactSound(JSoundRegistry.IMPACT_2.get())
            .withHitAnimation(CommonHitPropertyComponent.HitAnimation.CRUSH)
            .withHitSpark(JParticleType.HIT_SPARK_2)
            .withInfo(
                    Component.literal("Slam"),
                    Component.literal("")
            );
    public static final PoundAttack POUND = new PoundAttack(220, 7, 22, 1.25f,
            4f, 25, 1.5f, 0.1f, -0.1f)
            .withFollowup(SLAM)
            .withSound(JSoundRegistry.FOOL_BARK2.get())
            .withImpactSound(JSoundRegistry.IMPACT_2.get())
            .withLift(false)
            .withHitAnimation(CommonHitPropertyComponent.HitAnimation.LOW)
            .withHitSpark(JParticleType.HIT_SPARK_2)
            .withInfo(Component.literal("Pound"), Component.literal("""
                    has followups which create different sand patterns based on which key was pressed:
                    SPECIAL 1 - no sand
                    SPECIAL 2 - semicircle
                    SPECIAL 3 - diagonal pattern (influenced by where the user is looking)"""));
    public static final SandCloneMove SAND_CLONE = new SandCloneMove(300, 7, 11, 1f)
            .withSound(SoundEvents.SAND_PLACE)
            .withInfo(
                    Component.literal("Sand Manipulation"),
                    Component.literal("creates a blinding sand cloud, then a clone or (if crouching) circles of sand")
            );
    public static final GlideMove GLIDE = new GlideMove(300, 5, 125, 0f)
            .withSound(JSoundRegistry.FOOL_GLIDE.get())
            .withInfo(
                    Component.literal("Glider"),
                    Component.literal("turns The Fool into a glider for 6s")
            );
    public static final SandWaveAttack SAND_WAVE = new SandWaveAttack(340, 0, 80, 0f,
            1f, 0, 2f, 0.1f, 0f, 3)
            .withAerialVariant(GLIDE)
            .withBackstab(false)
            .withInfo(
                    Component.literal("Sandwave"),
                    Component.literal("The Fool turns into a quick sandwave that knocks anything it touches down")
            );
    public static final SandTornadoMove SAND_TORNADO = new SandTornadoMove(280, 12, 13, 1f)
            .withSound(JSoundRegistry.FOOL_LAUNCH.get())
            .withInfo(
                    Component.literal("Sand Tornado"),
                    Component.literal("summons a slow, stunning sand tornado")
            );
    public static final TFChargeAttack CHARGE = new TFChargeAttack(220, 5, 20, 7f,
            6f, 10, 1.5f, 1.2f, 0f, State.CHARGE_HIT)
            .withSound(JSoundRegistry.FOOL_CHARGE.get())
            .withImpactSound(JSoundRegistry.IMPACT_2.get())
            .withAerialVariant(SAND_TORNADO)
            .withLaunch()
            .withBackstab(false)
            .withHitSpark(JParticleType.HIT_SPARK_2)
            .withInfo(
                    Component.literal("Charge"),
                    Component.literal("The Fool detaches from the user and charges forward, launches on hit")
            );
    public static final SandstormAttack SANDSTORM = new SandstormAttack(800, 28, 41, 1.5f,
            7f, 20, 2f, 0.1f, 0f)
            .withSound(JSoundRegistry.FOOL_ULT.get())
            .withImpactSound(JSoundRegistry.TW_KICK_HIT.get())
            .withExtraHitBox(1.5)
            .withHyperArmor()
            .withBlockableType(BlockableType.NON_BLOCKABLE_EFFECTS_ONLY)
            .withHitAnimation(CommonHitPropertyComponent.HitAnimation.CRUSH)
            .withInfo(
                    Component.literal("Suffocating Sandstorm"),
                    Component.literal("very slow, traps the opponent in a cloud of blinding and slowing sand")
            );
    private static final BlockState sandState = Blocks.SAND.defaultBlockState();
    private static final EntityDataAccessor<Boolean> IS_SAND;
    private static final EntityDataAccessor<Boolean> IS_WAVE;

    static {
        IS_SAND = SynchedEntityData.defineId(TheFoolEntity.class, EntityDataSerializers.BOOLEAN);
        IS_WAVE = SynchedEntityData.defineId(TheFoolEntity.class, EntityDataSerializers.BOOLEAN);
    }

    public TheFoolEntity(Level worldIn) {
        super(StandType.THE_FOOL, worldIn);
        idleRotation = 225f;
        idleDistance = 2f;


        proCount = 5;
        conCount = 3;

        freespace =
                """
                        CROUCHING reduces attack distance by half, allowing better space control
                                                
                        BNBs:
                            Light>Pound~Slam>Launch>Light>Burn Rubber>Finisher*
                            Burn Rubber>Light>Pound~Slam>Launch>Finisher*
                            Launch>Light>Burn Rubber>Light>Pound~Slam>Finisher*

                            Stylish:
                            the social distancing
                            Light>Pound~Slam>Light>Combo>Charge>Sandwave
                            the pancake flip
                            Launch>Pound~Slam>Light>Burn Rubber>Finisher*

                            *Finisher: Light>...
                                       Charge/Tornado>...
                                       Sand Clone/Sandwave""";

        auraColors = new Vector3f[]{
                new Vector3f(1.0f, 0.8f, 0.4f),
                new Vector3f(0.8f, 0.3f, 1.0f),
                new Vector3f(1.0f, 0.6f, 0.2f),
                new Vector3f(0.4f, 0.5f, 1.0f)
        };
    }

    @Override
    protected void registerMoves(MoveMap<TheFoolEntity, State> moves) {
        moves.registerImmediate(MoveType.LIGHT, LIGHT, State.SWIPE);

        moves.register(MoveType.HEAVY, LAUNCH, State.LAUNCH);
        moves.register(MoveType.BARRAGE, COMBO, State.COMBO).withAerialVariant(State.AIR_BARRAGE);

        moves.register(MoveType.SPECIAL1, POUND, State.POUND_UP);
        moves.register(MoveType.SPECIAL2, CHARGE, State.CHARGE).withAerialVariant(State.TORNADO);
        moves.register(MoveType.SPECIAL3, SAND_CLONE, State.CREATE);
        moves.register(MoveType.ULTIMATE, SANDSTORM, State.SANDSTORM);

        moves.register(MoveType.UTILITY, SAND_WAVE, State.SAND_WAVE).withAerialVariant(State.GLIDE);
    }

    @Override
    public boolean initMove(MoveType type) {
        switch (type) {
            case SPECIAL1, SPECIAL2, SPECIAL3 -> {
                if (curMove != null && curMove.getOriginalMove() == POUND && getMoveStun() <= 11) {
                    initSlam(switch (type) {
                        default -> 1;
                        case SPECIAL2 -> 2;
                        case SPECIAL3 -> 3;
                    });

                    return true;
                }

                boolean s = super.initMove(type);
                if (type == MoveType.SPECIAL2 && !getUserOrThrow().onGround() || type == MoveType.SPECIAL3) {
                    setSand(true);
                }

                return s;
            }
            case LIGHT -> {
                if (curMove != null && curMove.getMoveType() == MoveType.LIGHT && getMoveStun() < curMove.getWindupPoint()) {
                    AbstractMove<?, ? super TheFoolEntity> followup = curMove.getFollowup();
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

    private void initSlam(int type) {
        getMoveContext().setInt(SlamAttack.VARIANT, type);
        setMove(SLAM, State.POUND_DOWN);
        playSound(JSoundRegistry.FOOL_BARK1.get(), 1, 1);
    }

    public boolean isSand() {
        return this.entityData.get(IS_SAND);
    }

    public void setSand(boolean b) {
        this.entityData.set(IS_SAND, b);
    }

    public boolean isWave() {
        return this.entityData.get(IS_WAVE);
    }

    public void setWave(boolean b) {
        setAlphaOverride(b ? 1.0F : -1.0F);
        this.entityData.set(IS_WAVE, b);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        getEntityData().define(IS_SAND, false);
        getEntityData().define(IS_WAVE, false);
    }

    @Override
    public void standBlock() {
        LivingEntity user = getUser();
        if (user == null) {
            return;
        }

        // Clear glider
        if (getState() == State.GLIDE) {
            cancelMove();
        }

        // The Fool does a special block depending on your height
        boolean sand = user.getBbHeight() < 1.8f;
        setSand(sand);
        if (sand) {
            this.setDistanceOffset(0);
        }

        // Projectile deflection
        List<Projectile> toDeflect = level().getEntitiesOfClass(Projectile.class, getBoundingBox().inflate(0.75f), EntitySelector.ENTITY_STILL_ALIVE);

        for (Projectile projectile : toDeflect) {
            if (projectile.getOwner() == user) {
                continue;
            }
            projectile.setDeltaMovement(projectile.getDeltaMovement().scale(-0.5).add(0, -0.1, 0));
            projectile.hurtMarked = true;
        }

        user.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 2, 9, false, false, true));
        stun(user, 2, 2);
        user.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 5, 4, false, false, true));
    }

    @Override
    public boolean shouldOffsetHeight() {
        if (getState() == State.GLIDE || getState() == State.SAND_WAVE || getState() == State.BLOCK) {
            return false;
        }
        return super.shouldOffsetHeight();
    }

    @Override
    public boolean canAttack() {
        if (hasUser()) {
            LivingEntity user = getUserOrThrow();
            if (JUtils.isAffectedByTimeStop(user) || user.hasEffect(JStatusRegistry.DAZED.get())) {
                return false;
            }
            if (curMove != null && curMove.getOriginalMove() == GLIDE) {
                return true;
            }
            return getMoveStun() <= 0;
        }
        return false;
    }

    @Override
    public void setMove(AbstractMove<?, ? super TheFoolEntity> move, @Nullable State animState) {
        if (getUser() != null && getUser().isShiftKeyDown()) {
            setSand(true);
            super.setMove(move.copy().withMoveDistance(move.getMoveDistance() / 2f), animState);
        } else {
            super.setMove(move, animState);
        }
    }

    @Override
    public void desummon() {
        // Remove everything that The Fool summoned before removing the stand itself
        SAND_CLONE.discardClone(this);
        SANDSTORM.discardSands(this);
        super.desummon();
    }

    public static void createFoolishSand(Level world, BlockPos pos, Vec3 vel) {
        BlockPos midBlockPos = pos.offset(0, 1, 0);
        if (world.getBlockState(midBlockPos).canOcclude()) {
            return;
        }
        FallingBlockEntity sand = FallingBlockEntity.fall(world, midBlockPos, JBlockRegistry.FOOLISH_SAND_BLOCK.get().defaultBlockState());
        sand.setHurtsEntities(5f, 5);
        sand.setDeltaMovement(vel);
        sand.hurtMarked = true;
        sand.hasImpulse = true;
        sand.blocksBuilding = false;
        sand.dropItem = false;
        world.addFreshEntity(sand);
    }

    @Override
    public void tick() {
        super.tick();

        if (!hasUser()) {
            return;
        }

        if (level().isClientSide) {
            if (tickCount % 2 != 0) {
                return;
            }
            Vec3 pos = position();
            // If the fool is using any morphing attack, the amount of sand multiplies, and the stand itself changes color
            int particleNum = isWave() ? 32 : 1 + Mth.clamp(getMoveStun() / 2, 0, 5) * (isSand() ? 2 : 1);
            int height = isWave() || blocking ? 1 : 2;

            for (int i = 0; i < particleNum; i++) {
                ParticleOptions effect = (isWave() && random.nextFloat() * 0.5f > 0) ?
                        new BlockParticleOption(ParticleTypes.BLOCK, sandState) :
                        new BlockParticleOption(ParticleTypes.FALLING_DUST, sandState);
                level().addParticle(
                        effect,
                        pos.x + random.triangle(0, 1),
                        pos.y + random.triangle(height / 2f, height / 2f),
                        pos.z + random.triangle(0, 1),
                        0, 0, 0);
            }

            return;
        }

        AbstractMove<?, ? super TheFoolEntity> move = curMove;
        if (lastRemoteInputTime - tickCount > 4) {
            updateRemoteInputs(0, 0, false, false);
        }
        if (move != null) {
            int slamType = moveContext.getInt(SlamAttack.VARIANT);
            if (move.getOriginalMove() == SLAM && slamType != 1) {
                queuedMove = null;
            }
        } else if (!blocking && getMoveStun() < 1) { // If idle, reset back to normal material
            setSand(false);
            setWave(false);
        }

        SANDSTORM.tickSandstorm(this);
        SAND_CLONE.tickClone(this);
    }

    @Override
    @NonNull
    public TheFoolEntity getThis() {
        return this;
    }

    // Animation code
    public enum State implements StandAnimationState<TheFoolEntity> {
        IDLE(builder -> builder.setAnimation(RawAnimation.begin().thenLoop("animation.thefool.idle"))),
        SWIPE(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.thefool.light"))),
        BLOCK((theFool, builder) -> builder.setAnimation(RawAnimation.begin().thenLoop("animation.thefool." +
                (theFool.isSand() ? "crouchblock" : "block")))),
        COMBO(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.thefool.combo"))),
        AIR_BARRAGE(builder -> builder.setAnimation(RawAnimation.begin().thenLoop("animation.thefool.airbarrage"))),
        LAUNCH(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.thefool.launch"))),
        POUND_UP(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.thefool.poundup"))),
        POUND_DOWN(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.thefool.pounddown"))),
        CHARGE(builder -> builder.setAnimation(RawAnimation.begin().thenLoop("animation.thefool.charge"))),
        CHARGE_HIT(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.thefool.charge_hit"))),
        CREATE(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.thefool.create"))),
        SAND_WAVE(builder -> builder.setAnimation(RawAnimation.begin().thenLoop("animation.thefool.sandwave"))),
        SANDSTORM(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.thefool.sandstorm"))),
        GLIDE(builder -> builder.setAnimation(RawAnimation.begin().thenLoop("animation.thefool.glide"))),
        TORNADO(builder -> builder.setAnimation(RawAnimation.begin().thenLoop("animation.thefool.tornado"))),
        DRILL(builder -> builder.setAnimation(RawAnimation.begin().thenLoop("animation.thefool.drill"))),
        LIGHT_FOLLOWUP(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.thefool.light_followup")));

        private final BiConsumer<TheFoolEntity, AnimationState> animator;

        State(Consumer<AnimationState> animator) {
            this((fool, builder) -> animator.accept(builder));
        }

        State(BiConsumer<TheFoolEntity, AnimationState> animator) {
            this.animator = animator;
        }

        @Override
        public void playAnimation(TheFoolEntity attacker, AnimationState builder) {
            animator.accept(attacker, builder);
        }
    }

    @Override
    protected State[] getStateValues() {
        return State.values();
    }

    @Override
    protected @Nullable String getSummonAnimation() {
        return "animation.thefool.summon";
    }

    @Override
    public State getBlockState() {
        return State.BLOCK;
    }
}
