package net.arna.jcraft.common.entity.stand;

import it.unimi.dsi.fastutil.ints.IntSet;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.attack.core.MoveType;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.moves.shared.*;
import net.arna.jcraft.common.component.living.CommonHitPropertyComponent;
import net.arna.jcraft.common.entity.PurpleHazeCloudEntity;
import net.arna.jcraft.common.entity.projectile.PHCapsuleProjectile;
import net.arna.jcraft.common.gravity.api.GravityChangerAPI;
import net.arna.jcraft.common.gravity.util.RotationUtil;
import net.arna.jcraft.common.util.JParticleType;
import net.arna.jcraft.common.util.JUtils;
import net.arna.jcraft.common.util.MobilityType;
import net.arna.jcraft.common.util.StandAnimationState;
import net.arna.jcraft.registry.JSoundRegistry;
import net.arna.jcraft.registry.JStatusRegistry;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import java.util.Set;

import static net.arna.jcraft.registry.JStatusRegistry.PHPOISON;

public abstract sealed class AbstractPurpleHazeEntity<E extends AbstractPurpleHazeEntity<E, S>, S extends Enum<S> & StandAnimationState<E>> extends StandEntity<E, S>
        permits PurpleHazeDistortionEntity, PurpleHazeEntity {
    public enum PoisonType {
        HARMING,
        NULLIFYING,
        DEBILITATING;
        static final int count = values().length;
    }

    protected PoisonType poisonType = PoisonType.HARMING;

    protected void nextPoisonType() {
        int next = this.poisonType.ordinal() + 1;
        this.poisonType = PoisonType.values()[next % PoisonType.count];
    }

    public static final KnockdownAttack<AbstractPurpleHazeEntity<?, ?>> BACKHAND_FOLLOWUP = new KnockdownAttack<AbstractPurpleHazeEntity<?, ?>>(
            0, 13, 20, 0.75f, 6f, 13, 1.75f, 0.5f, 0.35f, 25)
            .withImpactSound(JSoundRegistry.IMPACT_2.get())
            .withHitSpark(JParticleType.HIT_SPARK_2)
            .withInfo(
                    Component.literal("Hammerfist"),
                    Component.literal("1s knockdown")
            );
    public static final UppercutAttack<AbstractPurpleHazeEntity<?, ?>> BACKHAND = new UppercutAttack<AbstractPurpleHazeEntity<?, ?>>(20,
            6, 14, 0.75f, 6f, 20, 1.5f, 0.25f, -0.6f, 0.5f)
            .withTargetPostProcessor((attacker, target, kbVec, damageSource, blocking) -> {
                if (!blocking) {
                    infect(target, 3 * 20);
                }
            })
            .withFollowup(BACKHAND_FOLLOWUP)
            .withImpactSound(JSoundRegistry.IMPACT_4.get())
            .withExtraHitBox(0, 0.35, 1.25)
            .withHitSpark(JParticleType.HIT_SPARK_2)
            .withHitAnimation(CommonHitPropertyComponent.HitAnimation.CRUSH)
            .withInfo(
                    Component.literal("Backhand"),
                    Component.literal("launches vertically, infects (3s) on hit")
            );

    public static final SimpleAttack<AbstractPurpleHazeEntity<?, ?>> LIGHT_FOLLOWUP = new SimpleAttack<AbstractPurpleHazeEntity<?, ?>>(
            0, 9, 20, 0.75f, 6f, 13, 1.6f, 1.25f, -0.1f)
            .withImpactSound(JSoundRegistry.IMPACT_1.get())
            .withHitSpark(JParticleType.HIT_SPARK_2)
            .withLaunch()
            .withInfo(
                    Component.literal("Kick"),
                    Component.literal("fast combo finisher")
            );

    public static final SimpleAttack<AbstractPurpleHazeEntity<?, ?>> LIGHT = new SimpleAttack<AbstractPurpleHazeEntity<?, ?>>(
            30, 6, 9, 0.75f, 5f, 11, 1.5f, 0.25f, 0.1f)
            .withImpactSound(JSoundRegistry.IMPACT_1.get())
            .withFollowup(LIGHT_FOLLOWUP)
            .withCrouchingVariant(BACKHAND)
            .withInfo(
                    Component.literal("Punch"),
                    Component.literal("fast combo starter")
            );

    public static final SimpleAttack<AbstractPurpleHazeEntity<?, ?>> HEAVY = new SimpleAttack<AbstractPurpleHazeEntity<?, ?>>(
            20 * 5, 10, 20, 0.75f, 7f, 14, 2.0f, 1.25f, -0.1f)
            .withImpactSound(JSoundRegistry.IMPACT_1.get())
            .withLaunch()
            .withInfo(
                    Component.literal("Uppercut"),
                    Component.literal("launcher")
            );

    public static final MainBarrageAttack<AbstractPurpleHazeEntity<?, ?>> BARRAGE = new MainBarrageAttack<AbstractPurpleHazeEntity<?, ?>>(280,
            0, 40, 0.75f, 1f, 30, 2f, 0.25f, 0f, 3, Blocks.DEEPSLATE.defaultDestroyTime())
            .withSound(JSoundRegistry.PH_BARRAGE.get())
            .withInfo(
                    Component.literal("Barrage"),
                    Component.literal("fast reliable combo starter/extender, high stun")
            );

    public static final SimpleAttack<AbstractPurpleHazeEntity<?, ?>> LAUNCH_CAPSULES = new SimpleAttack<AbstractPurpleHazeEntity<?, ?>>(
            8 * 20, 9, 18, 0.75f, 0, 0, 0, 0, 0)
            .withSound(JSoundRegistry.PH_CAPSULE2.get())
            .markRanged()
            .withAction(
                    (attacker, user, ctx, targets) -> {
                        LivingEntity shooter = (attacker.isRemote() && !attacker.remoteControllable()) ? attacker : user;
                        Direction gravity = GravityChangerAPI.getGravityDirection(shooter);
                        for (int i = 0; i < 3; i++) {
                            launchCapsule(attacker, shooter, gravity, 0.4F, shooter.getYRot() - 45F + i * 45F);
                        }
                    }
            )
            .withInfo(
                    Component.literal("Triple Capsule Launch"),
                    Component.literal("launches 3 capsules close by")
            );

    public static final SimpleAttack<AbstractPurpleHazeEntity<?, ?>> LAUNCH_CAPSULE = new SimpleAttack<AbstractPurpleHazeEntity<?, ?>>(
            8 * 20, 7, 14, 0.75f, 0, 0, 0, 0, 0)
            .withSound(JSoundRegistry.PH_CAPSULE1.get())
            .withCrouchingVariant(LAUNCH_CAPSULES)
            .markRanged()
            .withAction(
                    (attacker, user, ctx, targets) -> {
                        LivingEntity shooter = (attacker.isRemote() && !attacker.remoteControllable()) ? attacker : user;
                        launchCapsule(attacker, shooter, GravityChangerAPI.getGravityDirection(shooter), 0.8F, shooter.getYRot());
                    }
            )
            .withInfo(
                    Component.literal("Capsule Launch"),
                    Component.literal("launches a single, fast capsule at the aimed location")
            );

    public static final SimpleMultiHitAttack<AbstractPurpleHazeEntity<?, ?>> FULL_RELEASE = new SimpleMultiHitAttack<AbstractPurpleHazeEntity<?, ?>>(
            30 * 20, 30, 0.75f, 3f, 11, 1.75f, 0.45f, 0.2f, IntSet.of(14, 24))
            .withSound(JSoundRegistry.PH_ULTIMATE.get())
            .withHitSpark(JParticleType.HIT_SPARK_1)
            .withHitAnimation(CommonHitPropertyComponent.HitAnimation.LOW)
            .withAction(AbstractPurpleHazeEntity::performUlt)
            .withHyperArmor()
            .withInfo(
                    Component.literal("Full Release"),
                    Component.literal("launches 2 sets of 3 capsules in a hexagonal pattern, uninterruptable")
            );


    // .withFollowup() and .withAnim() must be implemented inside inheritors
    public static final KnockdownAttack<AbstractPurpleHazeEntity<?, ?>> REKKA3 = new KnockdownAttack<AbstractPurpleHazeEntity<?, ?>>
            (0, 10, 20, 1f, 5f, 15, 2f, 0.75f, 0.3f, 55)
            .withSound(JSoundRegistry.PH_REKKA3.get())
            .withLaunch()
            .withImpactSound(JSoundRegistry.IMPACT_1.get())
            .withBlockStun(8)
            .withInfo(
                    Component.literal("Rekka (Final Hit)"),
                    Component.literal("knockdown, low blockstun")
            );
    public static final SimpleAttack<AbstractPurpleHazeEntity<?, ?>> REKKA2 = new SimpleAttack<AbstractPurpleHazeEntity<?, ?>>
            (0, 9, 18, 1f, 4f, 16, 1.75f, 0.5f, 0f)
            .withSound(JSoundRegistry.PH_REKKA2.get())
            .withImpactSound(JSoundRegistry.IMPACT_1.get())
            // .withFollowup(REKKA3)
            .withHitAnimation(CommonHitPropertyComponent.HitAnimation.CRUSH)
            .withInfo(
                    Component.literal("Rekka (2nd Hit)"),
                    Component.literal("links into Light")
            );
    public static final SimpleAttack<AbstractPurpleHazeEntity<?, ?>> REKKA1 = new SimpleAttack<AbstractPurpleHazeEntity<?, ?>>
            (160, 7, 14, 1f, 4f, 15, 1.5f, 0.5f, 0f)
            .withSound(JSoundRegistry.PH_REKKA1.get())
            .withImpactSound(JSoundRegistry.IMPACT_1.get())
            // .withFollowup(REKKA2)
            .withExtraHitBox(1.5)
            .withHitAnimation(CommonHitPropertyComponent.HitAnimation.HIGH)
            .withInitAction(AbstractPurpleHazeEntity::lunge)
            .withMobilityType(MobilityType.DASH)
            .withInfo(
                    Component.literal("Rekka Series"),
                    Component.literal("""
                            A set of three attacks, which cancel into each other during recovery.
                            Last hit knocks down for 2.5s""")
            );
    public static final SimpleAttack<AbstractPurpleHazeEntity<?, ?>> GROUND_SLAM = new SimpleAttack<AbstractPurpleHazeEntity<?, ?>>(
            7 * 20, 10, 18, 0.75f, 6f, 10, 1.75f, 0.3f, 0.3f)
            .withSound(JSoundRegistry.PH_GROUNDSLAM.get())
            .withImpactSound(JSoundRegistry.IMPACT_1.get())
            .withHitSpark(JParticleType.HIT_SPARK_2)
            .withHitAnimation(CommonHitPropertyComponent.HitAnimation.LOW)
            .withAction(AbstractPurpleHazeEntity::groundSlam)
            .withInfo(
                    Component.literal("Ground Slam"),
                    Component.literal("places down a Purple Haze cloud")
            );

    protected AbstractPurpleHazeEntity(StandType type, Level worldIn) {
        super(type, worldIn, JSoundRegistry.PH_SUMMON.get());
        idleRotation = 225f;

        proCount = 3;
        conCount = 3;

        freespace =
                """
                        BNBs:
                        Light > Rekka1~Rekka2 > crouching Light > Barrage >...
                            ...crouching Light~Light
                            ...Ground Slam
                            ...Light > Grab
                        """;
    }

    @Override
    public boolean initMove(MoveType type) {
        if (type == MoveType.SPECIAL2) {
            LivingEntity user = getUserOrThrow();
            if (user.hasEffect(JStatusRegistry.DAZED.get())) {
                return false;
            }
            boolean idling = this.getMoveStun() <= 0;
            if (curMove == null || curMove.getMoveType() != MoveType.SPECIAL2) {
                if (idling) {
                    return handleMove(MoveType.SPECIAL2);
                } else {
                    return false;
                }
            } else if (curMove.getFollowup() != null && curMove.hasWindupPassed(this)) {
                setMove(curMove.getFollowup(), (S) curMove.getFollowup().getAnimation());
            }
            return true;
        }
        return super.handleMove(type);
    }

    @Override
    public void tick() {
        super.tick();

        if (!isRemoteAndControllable()) {
            return;
        }

        if (level().isClientSide()) {
            JCraft.getClientEntityHandler().purpleHazeRemoteClientTick(this);
        } else {
            double f = getRemoteForwardInput();
            double s = getRemoteSideInput();
            boolean jump = getRemoteJumpInput();

            tickRemoteMovement(f, s, jump);
            tickRemoteState(f, s, onGround());
        }
    }

    protected abstract void tickRemoteState(double f, double s, boolean dashing);

    /**
     * Code lifted from {@link WhiteSnakeEntity#tickRemoteMovement(double, double, boolean)}
     *
     * @param f    Forward input
     * @param s    +Right/-Left input
     * @param jump Jump input
     */
    public void tickRemoteMovement(double f, double s, boolean jump) {
        Vec3 pos = position();

        // 1 tick of inertia, helping movement be fluid as well as dealing with packet drops
        if (lastRemoteInputTime - tickCount > 2) {
            updateRemoteInputs(0, 0, false, false);
        }
        Vec3 rotVec = new Vec3(getLookAngle().x, 0, getLookAngle().z).normalize();

        double dragMult = getMoveStun() > 0 ? 0.2 : 0.4;
        double moveSpeed = 0.24;
        boolean onGround = onGround();
        boolean climbing = getFeetBlockState().getTags().anyMatch(tag -> tag == BlockTags.CLIMBABLE);
        boolean swimming = !level().getFluidState(blockPosition()).isEmpty();

        if (climbing || swimming) {
            dragMult *= 0.5;
        }

        if ((climbing || swimming) && jump) { // Climb or Swim
            push(0, 0.1, 0);
        } else { // Jump
            if (onGround) {
                if (jump) {
                    push(0, 0.75, 0);
                    setRemoteJumpInput(false);
                }
            } else {
                moveSpeed = 0.024;
                dragMult = 0.4;
            }
        }

        remoteSpeed = remoteSpeed
                .add(rotVec.scale(f * moveSpeed)) // Forward movement
                .add(rotVec.yRot(1.5707963f).scale(s * moveSpeed)); // Side movement

        remoteSpeed = remoteSpeed.scale(dragMult);

        Vec3 userPos = getUserOrThrow().position();
        if (pos.add(remoteSpeed).distanceToSqr(userPos) > 25) {
            remoteSpeed = userPos.subtract(pos).scale(0.05); // 1/20th so it scales with distance
        }

        push(remoteSpeed.x, remoteSpeed.y, remoteSpeed.z);
        hasImpulse = true;
        hurtMarked = true;
    }

    public static void infect(LivingEntity target, int ticks) {
        infect(target, ticks, PHPOISON.get());
    }

    public static void infect(LivingEntity target, int ticks, MobEffect effect) {
        MobEffectInstance instance = target.getEffect(effect);
        if (instance != null) {
            target.addEffect(new MobEffectInstance(effect, instance.getDuration() + ticks, 2));
        } else {
            target.addEffect(new MobEffectInstance(effect, ticks, 2));
        }
    }

    // Attack methods
    private static void lunge(AbstractPurpleHazeEntity<?, ?> attacker, LivingEntity user, MoveContext moveContext) {
        if (attacker.isRemote()) {
            return;
        }
        JUtils.addVelocity(user, attacker.getLookAngle().scale(0.6));
    }

    private static void performUlt(AbstractPurpleHazeEntity<?, ?> attacker, LivingEntity user, MoveContext ctx, Set<LivingEntity> targets) {
        float baseYaw = user.getYRot();
        Direction gravity = GravityChangerAPI.getGravityDirection(attacker);

        if (attacker.getMoveStun() == 6) {
            baseYaw += 60.0F;
        }

        for (int i = 0; i < 3; i++) {
            launchCapsule(attacker, user, gravity, 0.6F, baseYaw + i * 120.0F);
        }
    }

    private static void launchCapsule(AbstractPurpleHazeEntity<?, ?> attacker, LivingEntity user, Direction gravity, float speed, float yaw) {
        PHCapsuleProjectile capsule = new PHCapsuleProjectile(user, attacker.level(), attacker.poisonType);

        Vec2 corrected = RotationUtil.rotPlayerToWorld(yaw, user.getXRot(), gravity);
        // Y,P to P,Y,R
        JUtils.shoot(capsule, user, corrected.y, corrected.x, 0.0F, speed, 0.1F);

        Vec3 upVec = GravityChangerAPI.getEyeOffset(attacker.getUserOrThrow());
        Vec3 heightOffset = upVec.scale(0.5);
        capsule.setPos(attacker.getBaseEntity().position().add(heightOffset));

        attacker.level().addFreshEntity(capsule);
    }

    private static void groundSlam(AbstractPurpleHazeEntity<?, ?> attacker, LivingEntity user, MoveContext ctx, Set<LivingEntity> targets) {
        PurpleHazeCloudEntity cloud = new PurpleHazeCloudEntity(attacker.level(), 3.0f, attacker.poisonType);
        cloud.copyPosition(attacker);
        attacker.level().addFreshEntity(cloud);
    }
}
