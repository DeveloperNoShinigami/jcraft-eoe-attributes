package net.arna.jcraft.common.entity.stand;

import lombok.NonNull;
import mod.azure.azurelib.core.animation.AnimationState;
import mod.azure.azurelib.core.animation.RawAnimation;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.attack.core.MoveInputType;
import net.arna.jcraft.common.attack.core.MoveMap;
import net.arna.jcraft.common.attack.core.MoveType;
import net.arna.jcraft.common.attack.core.StunType;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.moves.metallica.HarvestMove;
import net.arna.jcraft.common.attack.moves.shared.*;
import net.arna.jcraft.common.component.living.CommonHitPropertyComponent;
import net.arna.jcraft.common.component.living.CommonMiscComponent;
import net.arna.jcraft.common.entity.projectile.ScalpelProjectile;
import net.arna.jcraft.common.gravity.api.GravityChangerAPI;
import net.arna.jcraft.common.gravity.util.RotationUtil;
import net.arna.jcraft.common.spec.JSpec;
import net.arna.jcraft.common.util.CooldownType;
import net.arna.jcraft.common.util.JParticleType;
import net.arna.jcraft.common.util.JUtils;
import net.arna.jcraft.common.util.StandAnimationState;
import net.arna.jcraft.platform.JComponentPlatformUtils;
import net.arna.jcraft.registry.JSoundRegistry;
import net.arna.jcraft.registry.JStatusRegistry;
import net.arna.jcraft.registry.JTagRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class MetallicaEntity extends StandEntity<MetallicaEntity, MetallicaEntity.State> {
    private static final EntityDataAccessor<BlockPos> SIPHON_POS;
    private static final EntityDataAccessor<Float> IRON;
    private static final EntityDataAccessor<Boolean> INVISIBLE;
    public static final float IRON_MAX = 80.0f;
    public static final BlockPos NO_SIPHON = new BlockPos(0, Integer.MIN_VALUE, 0);
    static {
        SIPHON_POS = SynchedEntityData.defineId(MetallicaEntity.class, EntityDataSerializers.BLOCK_POS);
        IRON = SynchedEntityData.defineId(MetallicaEntity.class, EntityDataSerializers.FLOAT);
        INVISIBLE = SynchedEntityData.defineId(MetallicaEntity.class, EntityDataSerializers.BOOLEAN);
    }

    //todo: sword summon (LITERALLY MILLIA H DISC)
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
            .withImpactSound(JSoundRegistry.IMPACT_9.get())
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
            // .withSound(JSoundRegistry.METALLICA_BARRAGE.get())
            .withHitSpark(JParticleType.HIT_SPARK_1)
            .withImpactSound(JSoundRegistry.IMPACT_9.get())
            .withInfo(
                    Component.literal("Barrage"),
                    Component.literal("fast reliable combo starter/extender, high stun, smaller hitbox than most barrages")
            )
            .withInitAction((attacker, user, ctx) -> JUtils.playAnimIfUnoccupied(user, "mtl.brg"));
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
            .withInitAction((attacker, user, ctx) -> JUtils.playAnimIfUnoccupied(user, "mtl.swp"))
            .withCondition(metallica -> metallica.getIron() >= IRON_MAX / 4.0f);
    public static final UppercutAttack<MetallicaEntity> SMASH = new UppercutAttack<MetallicaEntity>(200,
            11, 21, 1.0f, 7.5f, 18,2.0f,  1.5f, 0.2f, -0.5f)
            .withCrouchingVariant(SWEEP)
            .withLaunch()
            .withImpactSound(JSoundRegistry.IMPACT_1.get())
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
            .withInitAction((attacker, user, ctx) -> JUtils.playAnimIfUnoccupied(user, "mtl.sms"))
            .withCondition(metallica -> metallica.getIron() >= IRON_MAX / 2.0f);
    public static final SimpleAttack<MetallicaEntity> FAN_TOSS = new SimpleAttack<MetallicaEntity>(
            60, 7, 12, 0.75f, 0, 0, 0, 0, 0)
            .withInfo(
                    Component.literal("Scalpel Toss (Precise)"),
                    Component.literal("""
                                    Decently fast, very low cooldown.
                                    Fires 5 scalpels in a fan pattern.""")
            )
            .markRanged()
            .withAction(MetallicaEntity::fanToss)
            .withInitAction((attacker, user, ctx) -> JUtils.playAnimIfUnoccupied(user, "mtl.ft"))
            .withCondition(metallica -> metallica.getIron() >= ScalpelProjectile.IRON_COST);
    private static void fanToss(MetallicaEntity stand, LivingEntity user, MoveContext context, Set<LivingEntity> livingEntities) {
        final float offset = 10.0F;
        int index = 0;
        // 0 -> 1 -> -1 -> 2 -> -2
        for (int i = 0; i < 5; i++) {
            ScalpelProjectile scalpel = ScalpelProjectile.fromMetallica(stand);
            if (scalpel == null) continue;

            if (i % 2 == 0) index -= i;
            else index += i;

            final float pitch = user.getXRot();
            final float yaw = user.getYRot() + index * offset;
            Vec3 rotVec = RotationUtil.vecPlayerToWorld(RotationUtil.rotToVec(yaw, pitch), GravityChangerAPI.getGravityDirection(user));
            scalpel.shoot(rotVec.x, rotVec.y, rotVec.z, 1.75F, 0.1F);

            Vec3 upVec = GravityChangerAPI.getEyeOffset(stand.getUserOrThrow());
            Vec3 heightOffset = upVec.scale(0.75);
            scalpel.setPos(stand.getBaseEntity().position().add(heightOffset));

            stand.level().addFreshEntity(scalpel);
        }
    }
    public static final SimpleAttack<MetallicaEntity> PRECISE_TOSS = new SimpleAttack<MetallicaEntity>(
            60, 7, 12, 0.75f, 0, 0, 0, 0, 0)
            .withCrouchingVariant(FAN_TOSS)
            .withInfo(
                    Component.literal("Scalpel Toss (Precise)"),
                    Component.literal("""
                                    Decently fast, very low cooldown.
                                    Fires 3 scalpels in the exact pointed direction.""")
            )
            .markRanged()
            .withAction(MetallicaEntity::preciseToss)
            .withInitAction((attacker, user, ctx) -> JUtils.playAnimIfUnoccupied(user, "mtl.pt"))
            .withCondition(metallica -> metallica.getIron() >= ScalpelProjectile.IRON_COST);

    private static void preciseToss(MetallicaEntity stand, LivingEntity user, MoveContext context, Set<LivingEntity> livingEntities) {
        Vec3 pos = stand.position();
        Vec3 upVec = GravityChangerAPI.getEyeOffset(user);
        for (int i = 1; i < 4; i++) {
            ScalpelProjectile scalpel = ScalpelProjectile.fromMetallica(stand);
            if (scalpel == null) continue;
            scalpel.setPos(pos.add(upVec.scale(0.25 * i)));
            scalpel.shootFromRotation(user, user.getXRot(), user.getYRot(), 0.0F, 1.25F, 0.0F);
            stand.level().addFreshEntity(scalpel);
        }
    }

    public static final SimpleAttack<MetallicaEntity> INTERNAL_ATTACK = new SimpleAttack<MetallicaEntity>(0,
            10, 15, 0, 0, 0, 0, 0, 0)
            .withAction(MetallicaEntity::internalAttack)
            .withInfo(
                    Component.literal("Internal Attack"),
                    Component.literal("""
                            12 meter range.
                            Uses the opponent's own iron to create a deadly remote attack.
                            Applies Hypoxia for 3 seconds.
                            Cannot attack hypoxic targets.
                            This attack does not interrupt other moves.""")
            )
            .withInitAction((attacker, user, ctx) -> JUtils.playAnimIfUnoccupied(user, "mtl.ita"));

    private static void internalAttack(MetallicaEntity metallica, LivingEntity user, MoveContext context, Set<LivingEntity> livingEntities) {
        final Vec3 eyePos = user.position().add(GravityChangerAPI.getEyeOffset(user));
        final Vec3 rotVec = user.getLookAngle();
        final HitResult hitResult = JUtils.raycastAll(user, eyePos, eyePos.add(rotVec.scale(12.0)), ClipContext.Fluid.NONE, EntitySelector.LIVING_ENTITY_STILL_ALIVE);
        // JCraft.createParticle((ServerLevel) user.level(), hitResult.getLocation().x, hitResult.getLocation().y, hitResult.getLocation().z, JParticleType.STUN_PIERCE);
        if (hitResult instanceof EntityHitResult entityHitResult && entityHitResult.getEntity() instanceof LivingEntity living) {
            final LivingEntity target = JUtils.getUserIfStand(living);
            if (target.hasEffect(JStatusRegistry.HYPOXIA.get())) {
                if (user instanceof ServerPlayer serverPlayer) {
                    serverPlayer.displayClientMessage(Component.literal("Cannot attack hypoxic targets."), true);
                }
            } else {
                ServerLevel serverWorld = (ServerLevel) user.level();
                final double x = target.getX(), y = target.getY(), z = target.getZ();
                final RandomSource random = metallica.getRandom();
                for (int i = 0; i < 3; i++) {
                    JCraft.createParticle(serverWorld,
                            x + random.nextGaussian(),
                            y + random.nextGaussian(),
                            z + random.nextGaussian(),
                            JParticleType.SWEEP_ATTACK
                    );
                }
                damage(3.5f, serverWorld.damageSources().sting(user), target);
                target.addEffect(
                        new MobEffectInstance(JStatusRegistry.HYPOXIA.get(), 60, 0, false, true)
                );
                JComponentPlatformUtils.getCooldowns(user).setCooldown(CooldownType.SPECIAL2, 200);
            }
        }
    }

    public static final EffectInflictingAttack<MetallicaEntity> GRAB_HIT_FINAL = new EffectInflictingAttack<MetallicaEntity>(0,
            18, 24, 0.5f, 4f, 9, 2f, 1.2f, 0f, List.of(
                    new MobEffectInstance(JStatusRegistry.HYPOXIA.get(), 200, 0, false, true)))
            // .withImpactSound(JSoundRegistry.IMPACT_1.get())
            .withHitSpark(JParticleType.HIT_SPARK_2)
            .withLaunch()
            .withInfo(
                    Component.literal("Grab (Final Hit)"),
                    Component.empty()
            )
            .withAction((attacker, user, ctx, targets) -> attacker.addIron(10.0f));
    public static final SimpleAttack<MetallicaEntity> GRAB_HIT = new SimpleAttack<MetallicaEntity>(0,
            13, 24, 0.5f, 4f, 10, 2f, 0f, 0f)
            .withStunType(StunType.UNBURSTABLE)
            .withInfo(
                    Component.literal("Grab (Second Hit)"),
                    Component.empty())
            .withFinisher(14, GRAB_HIT_FINAL)
            .withAction((attacker, user, ctx, targets) -> attacker.addIron(10.0f))
            .withInitAction((attacker, user, ctx) -> JUtils.playAnim(user, "mtl.grabh"));
    public static final GrabAttack<MetallicaEntity, State> GRAB = new GrabAttack<>(280,
            9, 20, 0.5f, 0, 15, 1.5f, 0, 0, GRAB_HIT, State.GRAB_HIT, 17, 0.4)
            .withInfo(
                    Component.literal("Grab"),
                    Component.literal("""
                            Unblockable, inflicts Hypoxia (10s).
                            Restores 25% of your iron meter.
                            Cannot be used alongside spec moves and will override them.""")
            )
            .withImpactSound(JSoundRegistry.IMPACT_9.get())
            .withInitAction((attacker, user, ctx) -> {
                JSpec<?, ?> spec = JComponentPlatformUtils.getSpecData(user).getSpec();
                if (spec != null && spec.getCurrentMove() != null) spec.cancelMove();
            })
            .withInitAction((attacker, user, ctx) -> JUtils.playAnim(user, "mtl.grab"));
    public static final SimpleAttack<MetallicaEntity> GO_INVISIBLE = new SimpleAttack<MetallicaEntity>(20,
            10, 15, 0, 0, 0, 0, 0, 0)
            .withInfo(
                    Component.literal("Invisibility"),
                    Component.literal("""
                            Projects a field of iron particles that reflect light away from the user.
                            Uses 10 iron per second.
                            Cannot be queued.""")
            )
            .withAction(MetallicaEntity::toggleInvisibility)
            .withInitAction((attacker, user, ctx) -> JUtils.playAnimIfUnoccupied(user, "mtl.ivs"));
    private static void toggleInvisibility(MetallicaEntity metallica, LivingEntity user, MoveContext context, Set<LivingEntity> livingEntities) {
        metallica.entityData.set(INVISIBLE, !metallica.entityData.get(INVISIBLE));
    }
    public static final HarvestMove HARVEST = new HarvestMove()
            .withCrouchingVariant(GO_INVISIBLE)
            .withInfo(
                    Component.literal("Harvest Iron"),
                    Component.literal("""
                            Harvests 1 iron with a 0.15s interval from the looked at block.
                            5m max range.
                            Cannot be queued.""")
            )
            .withHoldable()
            .withInitAction((attacker, user, ctx) -> attacker.entityData.set(SIPHON_POS, NO_SIPHON))
            .withAction(MetallicaEntity::harvest);
    private static void harvest(MetallicaEntity stand, LivingEntity user, MoveContext context, Set<LivingEntity> livingEntities) {
        BlockHitResult hitResult = JUtils.genericBlockRaycast(user.level(), user, 5, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE);
        if (hitResult.getType() == HitResult.Type.BLOCK && user.level().getBlockState(hitResult.getBlockPos()).is(JTagRegistry.IRON_BLOCKS)) {
            stand.entityData.set(SIPHON_POS, hitResult.getBlockPos());
            stand.addIron(1f);
        } else {
            stand.entityData.set(SIPHON_POS, NO_SIPHON);
        }
    }

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
    public void setUser(@Nullable LivingEntity user) {
        super.setUser(user);
        miscComponent = JComponentPlatformUtils.getMiscData(getUser());
        setIron(miscComponent.getMetallicaIron());
    }

    @Override
    protected void registerMoves(MoveMap<MetallicaEntity, MetallicaEntity.State> moves) {
        var light = moves.register(MoveType.LIGHT, LIGHT, State.LIGHT);
        light.withFollowUp(State.LIGHT_FOLLOWUP).withFollowUp(State.LIGHT_FINAL);
        moves.register(MoveType.BARRAGE, BARRAGE, State.BARRAGE);
        moves.register(MoveType.HEAVY, SMASH, State.SMASH).withCrouchingVariant(State.SWEEP);

        moves.register(MoveType.SPECIAL1, PRECISE_TOSS, State.PRECISE_TOSS).withCrouchingVariant(State.FAN_TOSS);
        moves.register(MoveType.SPECIAL2, INTERNAL_ATTACK, State.IDLE);
        moves.register(MoveType.SPECIAL3, GRAB, State.IDLE);
        moves.register(MoveType.UTILITY, HARVEST, State.HARVEST).withCrouchingVariant(State.IDLE);
    }

    @Override
    public boolean initMove(MoveType type) {
        if (tryFollowUp(type, MoveType.LIGHT)) return true;
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
        entityData.define(SIPHON_POS, NO_SIPHON);
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

    private static final MobEffectInstance INVISIBILITY = new MobEffectInstance(MobEffects.INVISIBILITY, 20, 0, true, false);
    private static final BlockParticleOption FAKE_BLOOD = new BlockParticleOption(ParticleTypes.BLOCK, Blocks.REDSTONE_WIRE.defaultBlockState());
    @Override
    public void tick() {
        super.tick();

        if (getState() == State.GRAB_HIT) {
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
        if (level().isClientSide()) return;
        boolean invisible = entityData.get(INVISIBLE);
        if (invisible && tickCount % 20 == 0) {
            boolean canStayInvis = drainIron(10.0f);
            if (!canStayInvis) {
                entityData.set(INVISIBLE, false);
            } else {
                getUserOrThrow().addEffect(INVISIBILITY);
            }
        }
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

    public BlockPos getSiphonPos() {
        return entityData.get(SIPHON_POS);
    }

    // Animations
    public enum State implements StandAnimationState<MetallicaEntity> {
        IDLE(builder -> builder.setAnimation(RawAnimation.begin().thenLoop("animation.metallica.idle"))),
        BLOCK(builder -> builder.setAnimation(RawAnimation.begin().thenLoop("animation.metallica.block"))),
        LIGHT(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.metallica.light"))),
        LIGHT_FOLLOWUP(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.metallica.light2"))),
        LIGHT_FINAL(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.metallica.light3"))),
        PRECISE_TOSS(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.metallica.precise_toss"))),
        FAN_TOSS(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.metallica.fan_toss"))),
        HARVEST(builder -> builder.setAnimation(RawAnimation.begin().thenLoop("animation.metallica.harvest"))),
        BARRAGE(builder -> builder.setAnimation(RawAnimation.begin().thenLoop("animation.metallica.barrage"))),
        SMASH(builder -> builder.setAnimation(RawAnimation.begin().thenLoop("animation.metallica.smash"))),
        SWEEP(builder -> builder.setAnimation(RawAnimation.begin().thenLoop("animation.metallica.sweep"))),
        GRAB_HIT(builder -> builder.setAnimation(RawAnimation.begin().thenLoop("animation.metallica.grab_hit"))),
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
