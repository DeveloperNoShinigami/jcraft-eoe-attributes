package net.arna.jcraft.common.entity.stand;

import lombok.NonNull;
import mod.azure.azurelib.core.animation.AnimationState;
import mod.azure.azurelib.core.animation.RawAnimation;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.attack.core.BlockableType;
import net.arna.jcraft.common.attack.core.MoveMap;
import net.arna.jcraft.common.attack.core.MoveType;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.moves.shared.MainBarrageAttack;
import net.arna.jcraft.common.attack.moves.shared.SimpleAttack;
import net.arna.jcraft.common.attack.moves.shared.UppercutAttack;
import net.arna.jcraft.common.component.living.CommonHitPropertyComponent;
import net.arna.jcraft.common.gravity.api.GravityChangerAPI;
import net.arna.jcraft.common.util.JParticleType;
import net.arna.jcraft.common.util.StandAnimationState;
import net.arna.jcraft.registry.JSoundRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
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

    public static final UppercutAttack<TheHandEntity> LIGHT_FOLLOWUP = new UppercutAttack<TheHandEntity>(
            0, 9, 14, 0.75f, 6f, 8, 1.6f, 0.3f, -0.1f, 0.3f)
            .withAnim(TheHandEntity.State.LIGHT_FOLLOWUP)
            .withSound(JSoundRegistry.D4C_LIGHT.get())
            .withImpactSound(JSoundRegistry.IMPACT_1.get())
            .withBlockStun(4)
            .withExtraHitBox(0, 0, 1)
            .withHitSpark(JParticleType.HIT_SPARK_2)
            .withInfo(
                    Component.literal("Gut Punch"),
                    Component.empty());
    public static final SimpleAttack<TheHandEntity> LIGHT = new SimpleAttack<TheHandEntity>(JCraft.LIGHT_COOLDOWN,
            5, 11, 0.75f, 4f, 12, 1.5f, 0.25f, -0.1f)
            .withFollowup(LIGHT_FOLLOWUP)
            .withImpactSound(JSoundRegistry.IMPACT_6.get())
            .withHitAnimation(CommonHitPropertyComponent.HitAnimation.HIGH)
            .withInfo(
                    Component.literal("Chop"),
                    Component.literal("Relatively quick combo starter."));

    public static final MainBarrageAttack<TheHandEntity> BARRAGE = new MainBarrageAttack<TheHandEntity>(240, 0,
            40, 0.75f, 0.8f, 30, 2f, 0.25f, 0f, 3, Blocks.DEEPSLATE.defaultDestroyTime())
            .withSound(JSoundRegistry.D4C_BARRAGE.get())
            .withImpactSound(JSoundRegistry.IMPACT_2.get())
            .withInfo(
                    Component.literal("Barrage"),
                    Component.literal("fast reliable combo starter/extender, high stun")
            );

    public static final SimpleAttack<TheHandEntity> ERASE_GROUND = new SimpleAttack<TheHandEntity>(100, 18,
            29, 0.75f, 10.0f, 15, 2.0f, 0, 0.35f)
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
            .withAction(TheHandEntity::eraseGround)
            .withBlockableType(BlockableType.NON_BLOCKABLE);

    public static final SimpleAttack<TheHandEntity> ERASE = new SimpleAttack<TheHandEntity>(100, 18,
            29, 0.75f, 10.0f, 15, 2.0f, 0, 0)
            // .withSound(JSoundRegistry.TH_ERASE.get())
            .withCrouchingVariant(ERASE_GROUND)
            .withImpactSound(JSoundRegistry.IMPACT_12.get())
            .withInfo(
                    Component.literal("Erase"),
                    Component.literal("Slow, unblockable attack-")
            )
            .withBlockableType(BlockableType.NON_BLOCKABLE);

    public TheHandEntity(final Level world) {
        super(StandType.THE_HAND, world);

        auraColors = new Vector3f[] {
                new Vector3f(0, 0, 1.0f),
                new Vector3f(0.8f, 0.6f, 0f),
                new Vector3f(0.8f, 0.2f, 0.8f),
                new Vector3f(0.2f, 0, 0.5f),
        };
    }

    private static void eraseGround(TheHandEntity attacker, LivingEntity user, MoveContext ctx, Set<LivingEntity> targets) {
        final Level level = user.level();
        if (level.getGameRules().getBoolean(JCraft.STAND_GRIEFING)) {
            final Vec3 rotVec = attacker.getLookAngle();
            final Vec3i rotVecI = new Vec3i((int) Math.round(rotVec.x), (int) Math.round(rotVec.y), (int) Math.round(rotVec.z));
            JCraft.createParticle((ServerLevel) level, attacker.getX() + rotVecI.getX(), attacker.getY() + rotVecI.getY(), attacker.getZ() + rotVecI.getZ(), JParticleType.STUN_PIERCE);

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

    @Override
    protected void registerMoves(final MoveMap<TheHandEntity, State> moves) {
        moves.register(MoveType.LIGHT, LIGHT, State.LIGHT).withFollowUp(State.LIGHT_FOLLOWUP);
        moves.register(MoveType.BARRAGE, BARRAGE, State.BARRAGE);
        moves.registerImmediate(MoveType.SPECIAL1, ERASE, State.ERASE);
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
        BLOCK(builder -> builder.setAnimation(RawAnimation.begin().thenLoop("animation.the_hand.block"))),
        HEAVY(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.the_hand.heavy"))),
        BARRAGE(builder -> builder.setAnimation(RawAnimation.begin().thenLoop("animation.the_hand.barrage"))),
        ERASE(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.the_hand.erase"))),
        ERASE_GROUND(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.the_hand.erase_ground"))),
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
