package net.arna.jcraft.common.entity.stand;

import lombok.NonNull;
import mod.azure.azurelib.core.animation.AnimationState;
import mod.azure.azurelib.core.animation.RawAnimation;
import net.arna.jcraft.common.attack.core.MoveMap;
import net.arna.jcraft.common.attack.core.MoveType;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.moves.base.AbstractMove;
import net.arna.jcraft.common.attack.moves.horus.HorusBarrageAttack;
import net.arna.jcraft.common.attack.moves.shared.SimpleAttack;
import net.arna.jcraft.common.component.living.CommonHitPropertyComponent;
import net.arna.jcraft.common.entity.projectile.IcicleProjectile;
import net.arna.jcraft.common.entity.projectile.LargeIcicleProjectile;
import net.arna.jcraft.common.gravity.api.GravityChangerAPI;
import net.arna.jcraft.common.gravity.util.RotationUtil;
import net.arna.jcraft.common.util.JParticleType;
import net.arna.jcraft.common.util.StandAnimationState;
import net.arna.jcraft.registry.JSoundRegistry;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.Set;
import java.util.function.Consumer;

public class HorusEntity extends StandEntity<HorusEntity, HorusEntity.State> {
    public static final SimpleAttack<HorusEntity> LIGHT_CROUCHING_FOLLOWUP = new SimpleAttack<HorusEntity>(
            0, 15, 25, 0.75f, 7f, 25, 1.85f, 2f, 0.2f)
            .withAnim(State.IMPALE)
            .withImpactSound(JSoundRegistry.IMPACT_9.get())
            .withBlockStun(25)
            .withHitAnimation(CommonHitPropertyComponent.HitAnimation.CRUSH)
            .withHitSpark(JParticleType.HIT_SPARK_2)
            .withInfo(
                    Component.literal("Impale"),
                    Component.literal("slow reset tool, high stun and blockstun")
            );
    public static final SimpleAttack<HorusEntity> LIGHT_FOLLOWUP = new SimpleAttack<HorusEntity>(
            0, 9, 13, 0.75f, 6f, 10, 1.5f, 1f, 0.2f)
            .withAnim(State.LIGHT_FOLLOWUP)
            .withCrouchingVariant(LIGHT_CROUCHING_FOLLOWUP)
            .withImpactSound(JSoundRegistry.IMPACT_3.get())
            .withLaunch()
            .withBlockStun(4)
            .withHitSpark(JParticleType.HIT_SPARK_2)
            .withInfo(
                    Component.literal("Finisher"),
                    Component.literal("quick combo finisher")
            );
    public static final SimpleAttack<HorusEntity> LIGHT = SimpleAttack.<HorusEntity>lightAttack(
                    6, 11, 0.75f, 5f, 12, 0.2f, 0f)
            .withFollowup(LIGHT_FOLLOWUP)
            // .withCrouchingVariant(UPPERCUT)
            .withImpactSound(JSoundRegistry.IMPACT_3.get())
            .withInfo(
                    Component.literal("Punch"),
                    Component.literal("quick combo starter")
            );
    public static final HorusBarrageAttack BARRAGE = new HorusBarrageAttack(
            240, 5, 80, 0.75f, 0, 0, 0, 0, 0, 5
    )
            .withInfo(
                    Component.literal("Barrage"),
                    Component.literal("4s max duration, can be held")
            ).withAction(HorusEntity::fireIcicle);
    public static final SimpleAttack<HorusEntity> STOMP = new SimpleAttack<HorusEntity>(
            7, 11, 22, 0.75f, 9f, 12, 1.3f, 0.6f, 0.4f)
            .withAnim(State.STOMP)
            .withImpactSound(JSoundRegistry.IMPACT_1.get())
            .withHitAnimation(CommonHitPropertyComponent.HitAnimation.LOW)
            .withHitSpark(JParticleType.HIT_SPARK_3)
            .withInfo(
                    Component.literal("Stomp"),
                    Component.literal("summons a large icicle, press Heavy again to detonate it")
            ).withAction(HorusEntity::heavyIcicle);

    public HorusEntity(Level world) {
        super(StandType.HORUS, world);

        auraColors = new Vector3f[]{
                new Vector3f(0.4f, 0.7f, 1.0f),
                new Vector3f(0.3f, 0.6f, 1.0f),
                new Vector3f(1.0f, 0.3f, 0.7f),
                new Vector3f(1.0f, 0.0f, 0.0f)
        };
    }

    private static void fireIcicle(HorusEntity attacker, LivingEntity user, MoveContext ctx, Set<LivingEntity> targets) {
        IcicleProjectile icicle = new IcicleProjectile(attacker.level(), user);
        icicle.shootFromRotation(user, user.getXRot(), user.getYRot(), 0.0F, 1.75F, 0.1F);

        Vec3 upVec = GravityChangerAPI.getEyeOffset(attacker.getUserOrThrow());
        Vec3 heightOffset = upVec.scale(0.75);
        icicle.setPos(attacker.getBaseEntity().position().add(heightOffset).add(
                attacker.getRandom().nextGaussian() / 3,
                attacker.getRandom().nextGaussian() / 3,
                attacker.getRandom().nextGaussian() / 3
        ));
        //icicle.withReflect();

        attacker.level().addFreshEntity(icicle);
    }

    private static void heavyIcicle(HorusEntity attacker, LivingEntity user, MoveContext context, Set<LivingEntity> targets) {
        LargeIcicleProjectile icicle = new LargeIcicleProjectile(attacker.level(), user);

        // Shoot slightly upwards
        Direction gravity = GravityChangerAPI.getGravityDirection(user);
        Vec3 velocity = user.getLookAngle()
                .add(RotationUtil.vecPlayerToWorld(new Vec3(0, 1, 0), gravity))
                .scale(0.01);
        double e = velocity.x;
        double f = velocity.y;
        double g = velocity.z;
        double l = velocity.horizontalDistance();
        icicle.moveTo(attacker.getX(), attacker.getY(), attacker.getZ(),
                (float) (Mth.atan2(-e, -g) * 57.2957763671875),
                (float) (Mth.atan2(f, l) * 57.2957763671875)
        );
        icicle.setDeltaMovement(velocity);

        attacker.level().addFreshEntity(icicle);
    }

    @Override
    protected void registerMoves(MoveMap<HorusEntity, HorusEntity.State> moves) {
        moves.registerImmediate(MoveType.LIGHT, LIGHT, State.LIGHT);
        moves.register(MoveType.BARRAGE, BARRAGE, State.BARRAGE);
        moves.register(MoveType.HEAVY, STOMP, State.STOMP);
    }

    @Override
    public boolean initMove(MoveType type) {
        if (type == MoveType.LIGHT && curMove != null && curMove.getMoveType() == MoveType.LIGHT && getMoveStun() < curMove.getWindupPoint()) {
            AbstractMove<?, ? super HorusEntity> followup = curMove.getFollowup();
            if (followup != null) {
                if (getUserOrThrow().isDiscrete()) followup = followup.getCrouchingVariant();
                setMove(followup, (State) followup.getAnimation());
            }

            return true;
        } else {
            return super.initMove(type);
        }
    }

    @Override
    public @NonNull HorusEntity getThis() {
        return this;
    }

    // Animation code
    public enum State implements StandAnimationState<HorusEntity> {

        IDLE(builder -> builder.setAnimation(RawAnimation.begin().thenLoop("animation.horus.idle"))),
        BLOCK(builder -> builder.setAnimation(RawAnimation.begin().thenLoop("animation.horus.block"))),
        LIGHT(builder -> builder.setAnimation(RawAnimation.begin().thenPlay("animation.horus.light"))),
        LIGHT_FOLLOWUP(builder -> builder.setAnimation(RawAnimation.begin().thenPlay("animation.horus.light_followup"))),
        IMPALE(builder -> builder.setAnimation(RawAnimation.begin().thenPlay("animation.horus.impale"))),
        BARRAGE(builder -> builder.setAnimation(RawAnimation.begin().thenPlay("animation.horus.barrage"))),
        STOMP(builder -> builder.setAnimation(RawAnimation.begin().thenPlay("animation.horus.stomp"))),

        ;

        private final Consumer<AnimationState> animator;

        State(Consumer<AnimationState> animator) {
            this.animator = animator;
        }

        @Override
        public void playAnimation(HorusEntity attacker, AnimationState builder) {
            animator.accept(builder);
        }
    }

    @Override
    protected HorusEntity.State[] getStateValues() {
        return State.values();
    }

    @Override
    protected @Nullable String getSummonAnimation() {
        return "animation.horus.summon";
    }

    @Override
    public HorusEntity.State getBlockState() {
        return State.BLOCK;
    }
}
