package net.arna.jcraft.common.entity.stand;

import lombok.NonNull;
import mod.azure.azurelib.core.animation.AnimationState;
import mod.azure.azurelib.core.animation.RawAnimation;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.attack.MobilityType;
import net.arna.jcraft.common.attack.core.MoveMap;
import net.arna.jcraft.common.attack.core.MoveType;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.moves.shared.*;
import net.arna.jcraft.common.attack.moves.thehand.EraseAttack;
import net.arna.jcraft.common.component.living.CommonHitPropertyComponent;
import net.arna.jcraft.common.gravity.api.GravityChangerAPI;
import net.arna.jcraft.common.util.JParticleType;
import net.arna.jcraft.common.util.JUtils;
import net.arna.jcraft.common.util.StandAnimationState;
import net.arna.jcraft.platform.JComponentPlatformUtils;
import net.arna.jcraft.registry.JSoundRegistry;
import net.arna.jcraft.registry.JStatusRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.Set;
import java.util.function.Consumer;

/**
 * The {@link StandEntity} for <a href="https://jojowiki.com/The_Hand">The Hand</a>.
 * @see StandType#THE_HAND
 * @see net.arna.jcraft.client.model.entity.stand.TheHandModel TheHandModel
 * @see net.arna.jcraft.client.renderer.entity.stands.TheHandRenderer TheHandRenderer
 */
public class TheHandEntity extends StandEntity<TheHandEntity, TheHandEntity.State> {

    public static final UppercutAttack<TheHandEntity> CROUCHING_LIGHT_FOLLOWUP = new UppercutAttack<TheHandEntity>(0,
            13, 20, 0.6f, 6f, 15, 1.75f, 0.3f, 0.4f, -0.3f)
            .withAnim(State.CROUCHING_LIGHT_FOLLOWUP)
            .withImpactSound(JSoundRegistry.IMPACT_1.get())
            .withImpactSound(JSoundRegistry.IMPACT_2.get())
            .withExtraHitBox(0, 0, 1)
            .withHitAnimation(CommonHitPropertyComponent.HitAnimation.CRUSH)
            .withHitSpark(JParticleType.HIT_SPARK_2)
            .withInfo(
                    Component.literal("Stomp (Second Hit)"),
                    Component.literal("Lifts knocked down enemies off the ground."))
            .withAction(
                    TheHandEntity::offTheGround
            );
    public static final SimpleAttack<TheHandEntity> CROUCHING_LIGHT = new SimpleAttack<TheHandEntity>((int) (JCraft.LIGHT_COOLDOWN * 2.0),
            9, 14, 0.5f, 5f, 15, 1.5f, 0.25f, 0.4f)
            .withFollowup(CROUCHING_LIGHT_FOLLOWUP)
            .withImpactSound(JSoundRegistry.IMPACT_1.get())
            .withHitAnimation(CommonHitPropertyComponent.HitAnimation.LOW)
            .withBlockStun(12)
            .withInfo(
                    Component.literal("Stomp"),
                    Component.literal("""
                                            Relatively quick combo starter.
                                            Shorter range.
                                            High blockstun.""")
            );
    public static final UppercutAttack<TheHandEntity> LIGHT_FOLLOWUP = new UppercutAttack<TheHandEntity>(
            0, 9, 14, 0.75f, 6f, 8, 1.6f, 0.3f, -0.1f, 0.3f)
            .withAnim(State.LIGHT_FOLLOWUP)
            .withImpactSound(JSoundRegistry.IMPACT_1.get())
            .withBlockStun(4)
            .withExtraHitBox(0, 0, 1)
            .withHitSpark(JParticleType.HIT_SPARK_2)
            .withInfo(
                    Component.literal("Gut Punch"),
                    Component.empty());
    public static final SimpleAttack<TheHandEntity> LIGHT = new SimpleAttack<TheHandEntity>(JCraft.LIGHT_COOLDOWN,
            5, 10, 0.75f, 4f, 12, 1.5f, 0.25f, -0.1f)
            .withFollowup(LIGHT_FOLLOWUP)
            .withCrouchingVariant(CROUCHING_LIGHT)
            .withImpactSound(JSoundRegistry.IMPACT_6.get())
            .withHitAnimation(CommonHitPropertyComponent.HitAnimation.HIGH)
            .withInfo(
                    Component.literal("Punch"),
                    Component.literal("Relatively quick combo starter."));

    public static final MainBarrageAttack<TheHandEntity> BARRAGE = new MainBarrageAttack<TheHandEntity>(240, 0,
            40, 0.75f, 0.8f, 30, 2f, 0.25f, 0f, 3, Blocks.DEEPSLATE.defaultDestroyTime())
            .withSound(JSoundRegistry.D4C_BARRAGE.get())
            .withImpactSound(JSoundRegistry.IMPACT_2.get())
            .withInfo(
                    Component.literal("Barrage"),
                    Component.literal("fast reliable combo starter/extender, high stun")
            );

    public static final KnockdownAttack<TheHandEntity> SWEEP = new KnockdownAttack<TheHandEntity>(100, 13, 18, 1.0f,
            9f, 15, 1.6f, 0.4f, 0.3f, 35)
            .withAnim(State.SWEEP)
            .withImpactSound(JSoundRegistry.IMPACT_1.get())
            .withHitSpark(JParticleType.HIT_SPARK_3)
            .withInfo(
                    Component.literal("Sweep"),
                    Component.literal("Can be comboed out of with cr.M1~M1>Barrage")
            );
    public static final UppercutAttack<TheHandEntity> KICK = new UppercutAttack<TheHandEntity>(100, 13, 24, 0.75f,
            9f, 12, 2f, 1.1f, 0.1f, 0.3f)
            .withCrouchingVariant(SWEEP)
            .withImpactSound(JSoundRegistry.IMPACT_1.get())
            .withHitSpark(JParticleType.HIT_SPARK_3)
            .withHyperArmor()
            .withLaunch()
            .withInfo(
                    Component.literal("Home Run!"),
                    Component.literal("Uninterruptible launcher.")
            );
    public static final EraseAttack ERASE_GROUND = new EraseAttack(120, 18,
            29, 0.75f, 8.0f, 15, 2.0f, 0, 0.35f)
            // .withSound(JSoundRegistry.TH_ERASE.get())
            .withAnim(State.ERASE_GROUND)
            .withImpactSound(JSoundRegistry.IMPACT_12.get())
            .withInfo(
                    Component.literal("Erase"),
                    Component.literal("""
                            Erases the ground in front of the user.
                            Works on any non-indestructible block.""")
            )
            .withStaticY()
            .withAction(TheHandEntity::eraseGround);
    public static final EraseAttack ERASE = new EraseAttack(120, 18,
            29, 0.75f, 8.0f, 15, 2.0f, 0, 0)
            // .withSound(JSoundRegistry.TH_ERASE.get())
            .withCrouchingVariant(ERASE_GROUND)
            .withImpactSound(JSoundRegistry.IMPACT_12.get())
            .withInfo(
                    Component.literal("Erase"),
                    Component.literal("Slow, unblockable attack-")
            );
    public static final SimpleAttack<TheHandEntity> GRAB_HIT = new SimpleAttack<TheHandEntity>(0, 14, 16,
            0.75f, 8f, 5, 1.75f, 1.5f, 0f)
            .withImpactSound(JSoundRegistry.IMPACT_1.get())
            .withLaunch()
            .withHitSpark(JParticleType.HIT_SPARK_2)
            .withInfo(
                    Component.literal("Grab (Hit)"),
                    Component.empty());
    public static final GrabAttack<TheHandEntity, State> GRAB = new GrabAttack<>(300, 10, 20,
            0.75f, 0f, 16, 1.5f, 0f, 0f, GRAB_HIT, State.GRAB_HIT)
            // .withSound(JSoundRegistry.TH_GRAB.get())
            .withInfo(
                    Component.literal("Grab"),
                    Component.literal("unblockable, knocks back"));
    public static final EraseAttack ERASE_SPACE = new EraseAttack(300, 12,
            20, 0.75f, 4.0f, 6, 2.0f, -0.5f, 0.0f)
            // .withSound(JSoundRegistry.TH_ERASE.get())
            .withAnim(State.ERASE_GROUND)
            .withImpactSound(JSoundRegistry.IMPACT_12.get())
            .withInfo(
                    Component.literal("Erase Space"),
                    Component.literal("""
                            Brings any looked at entity.
                            If not looking at anything, will bring you forward.""")
            )
            .withMobilityType(MobilityType.DASH)
            .withAction(TheHandEntity::eraseSpace);

    public TheHandEntity(final Level world) {
        super(StandType.THE_HAND, world);

        proCount = 4;
        conCount = 2;

        freespace =
                """
                        BNBs:
                            -the lazy zoner
                            Light>Barrage>Light>Grab/Charge
                            
                            -the western
                            Light>Summon Gun>Barrage>Light~stand.OFF>M2>M2>M2>~s.ON+Light>Charge""";

        auraColors = new Vector3f[] {
                new Vector3f(0, 0, 1.0f),
                new Vector3f(0.6f, 0.2f, 0f),
                new Vector3f(0.8f, 0.2f, 0.8f),
                new Vector3f(0.2f, 0, 0.5f),
        };
    }

    private static void eraseGround(TheHandEntity attacker, LivingEntity user, MoveContext ctx, Set<LivingEntity> targets) {
        final Level level = user.level();
        if (level.getGameRules().getBoolean(JCraft.STAND_GRIEFING)) {
            final Vec3 rotVec = attacker.getLookAngle();
            final Vec3i rotVecI = new Vec3i((int) Math.round(rotVec.x), (int) Math.round(rotVec.y), (int) Math.round(rotVec.z));

            /*
            PATTERN:
            [][][]
            [][]
            WHERE TOP LEFT IS ATTACKER STANDING BLOCK POSITION, AND RIGHT IS ATTACKER FORWARD
             */

            final Vec3i gravityNormal = GravityChangerAPI.getGravityDirection(user).getNormal();

            final BlockPos lowBlock1 = attacker.getOnPos();
            final BlockPos lowBlock2 = lowBlock1.offset(rotVecI);
            final BlockPos block1 = lowBlock1.subtract(gravityNormal);
            final BlockPos block2 = block1.offset(rotVecI);
            final BlockPos block3 = block2.offset(rotVecI);
            eraseBlock(level, lowBlock1);
            eraseBlock(level, lowBlock2);
            eraseBlock(level, block1);
            eraseBlock(level, block2);
            eraseBlock(level, block3);
        }
    }

    private static void eraseBlock(final Level level, final BlockPos lookedBlock) {
        if (level.getBlockState(lookedBlock).getBlock().defaultDestroyTime() <= 0.0f) return;
        level.setBlock(lookedBlock, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
    }

    private static void eraseSpace(final TheHandEntity attacker, final LivingEntity user, final MoveContext ctx, final Set<LivingEntity> targets) {
        final Vec3 rotVec = user.getLookAngle();
        final Vec3 eyePos = user.position().add(GravityChangerAPI.getEyeOffset(user));
        final HitResult hitResult = JUtils.raycastAll(attacker,
                eyePos,
                eyePos.add(rotVec.scale(16.0)),
                ClipContext.Fluid.NONE);

        if (hitResult.getType() == HitResult.Type.ENTITY) {
            final Entity hitEntity = JUtils.getUserIfStand(((EntityHitResult) hitResult).getEntity());
            JUtils.addVelocity(hitEntity, rotVec.scale(-1.25));
            hitEntity.setOnGround(false);

            if (hitEntity instanceof LivingEntity living) {
                living.addEffect(
                        new MobEffectInstance(MobEffects.LEVITATION, 5, 0, true, false)
                );
            }
        } else {
            JUtils.addVelocity(user, rotVec.scale(1.25));
        }
    }

    private static void offTheGround(final TheHandEntity attacker, final LivingEntity user, final MoveContext ctx, final Set<LivingEntity> targets) {
        JComponentPlatformUtils.getShockwaveHandler(attacker.level())
                .addShockwave(attacker.position().add(user.getLookAngle()), new Vec3(GravityChangerAPI.getGravityDirection(attacker).step()), 2.5f);

        targets.forEach(livingEntity -> livingEntity.removeEffect(JStatusRegistry.KNOCKDOWN.get()));
    }

    @Override
    protected void registerMoves(final MoveMap<TheHandEntity, State> moves) {
        var light = moves.register(MoveType.LIGHT, LIGHT, State.LIGHT);
        light.withFollowUp(State.LIGHT_FOLLOWUP);
        light.withCrouchingVariant(State.CROUCHING_LIGHT).withFollowUp(State.CROUCHING_LIGHT_FOLLOWUP);

        moves.registerImmediate(MoveType.HEAVY, KICK, State.KICK);
        moves.register(MoveType.BARRAGE, BARRAGE, State.BARRAGE);
        moves.registerImmediate(MoveType.SPECIAL1, ERASE, State.ERASE);

        moves.registerImmediate(MoveType.SPECIAL3, GRAB, State.GRAB);

        moves.register(MoveType.UTILITY, ERASE_SPACE, State.ERASE_SPACE);
    }

    @Override
    public boolean initMove(MoveType type) {
        if (tryFollowUp(type, MoveType.LIGHT)) return true;
        return super.initMove(type);
    }

    @Override
    public boolean shouldOffsetHeight() {
        if (getState() == State.ERASE_GROUND) return false;
        return super.shouldOffsetHeight();
    }

    @Override
    public @NonNull TheHandEntity getThis() {
        return this;
    }

    public enum State implements StandAnimationState<TheHandEntity> {
        IDLE(builder -> builder.setAnimation(RawAnimation.begin().thenLoop("animation.the_hand.idle"))),
        LIGHT(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.the_hand.light"))),
        LIGHT_FOLLOWUP(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.the_hand.light2"))),
        CROUCHING_LIGHT(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.the_hand.crouching_light"))),
        CROUCHING_LIGHT_FOLLOWUP(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.the_hand.crouching_light2"))),
        BLOCK(builder -> builder.setAnimation(RawAnimation.begin().thenLoop("animation.the_hand.block"))),
        KICK(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.the_hand.heavy"))),
        BARRAGE(builder -> builder.setAnimation(RawAnimation.begin().thenLoop("animation.the_hand.barrage"))),
        ERASE(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.the_hand.erase"))),
        ERASE_GROUND(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.the_hand.erase_ground"))),
        ERASE_SPACE(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.the_hand.erase_space"))),
        SWEEP(builder -> builder.setAnimation(RawAnimation.begin().thenLoop("animation.the_hand.sweep"))),
        GRAB(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.the_hand.grab"))),
        GRAB_HIT(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.the_hand.grab_hit"))),
        ;

        private final Consumer<AnimationState<TheHandEntity>> animator;

        State(Consumer<AnimationState<TheHandEntity>> animator) {
            this.animator = animator;
        }

        @Override
        public void playAnimation(TheHandEntity attacker, AnimationState<TheHandEntity> state) {
            animator.accept(state);
        }
    }

    @Override
    protected State[] getStateValues() {
        return State.values();
    }

    @Override
    protected @Nullable String getSummonAnimation() {
        return "animation.the_hand.summon";
    }

    @Override
    public State getBlockState() {
        return State.BLOCK;
    }
}
