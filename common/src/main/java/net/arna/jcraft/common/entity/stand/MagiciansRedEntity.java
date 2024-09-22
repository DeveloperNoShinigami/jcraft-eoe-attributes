package net.arna.jcraft.common.entity.stand;

import lombok.NonNull;
import mod.azure.azurelib.core.animation.AnimationState;
import mod.azure.azurelib.core.animation.RawAnimation;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.attack.core.MoveMap;
import net.arna.jcraft.common.attack.core.MoveType;
import net.arna.jcraft.common.attack.moves.base.AbstractMove;
import net.arna.jcraft.common.attack.moves.magiciansred.*;
import net.arna.jcraft.common.attack.moves.shared.KnockdownAttack;
import net.arna.jcraft.common.attack.moves.shared.SimpleAttack;
import net.arna.jcraft.common.component.living.CommonHitPropertyComponent;
import net.arna.jcraft.common.util.JParticleType;
import net.arna.jcraft.common.util.StandAnimationState;
import net.arna.jcraft.registry.JSoundRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.IceBlock;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.Collection;
import java.util.function.Consumer;

public class MagiciansRedEntity extends StandEntity<MagiciansRedEntity, MagiciansRedEntity.State> {
    public static final RedirectAttack REDIRECT = new RedirectAttack(0, 7, 10, 0.75f)
            .withAnim(State.REDIRECT)
            .withSound(JSoundRegistry.MR_REDIRECT.get())
            .withInfo(
                    Component.literal("Redirect"),
                    Component.literal("redirects all the users ankhs to where they're looking")
            );
    public static final SimpleAttack<MagiciansRedEntity> LIGHT_FOLLOWUP = new SimpleAttack<MagiciansRedEntity>(
            0, 6, 14, 0.65f, 6f, 12, 1.5f, 1.2f, -0.1f)
            .withAnim(State.LIGHT_FOLLOWUP)
            .withImpactSound(JSoundRegistry.IMPACT_1.get())
            .withLaunch()
            .withBlockStun(4)
            .withHitSpark(JParticleType.HIT_SPARK_2)
            .withInfo(
                    Component.literal("Punch"),
                    Component.literal("quick combo finisher")
            );
    public static final SimpleAttack<MagiciansRedEntity> LIGHT = new SimpleAttack<MagiciansRedEntity>(JCraft.LIGHT_COOLDOWN,
            5, 8, 0.75f, 5f, 16, 1.5f, 0.2f, -0.1f)
            .withFollowup(LIGHT_FOLLOWUP)
            .withCrouchingVariant(REDIRECT)
            .withImpactSound(JSoundRegistry.IMPACT_1.get())
            .withInfo(
                    Component.literal("Punch"),
                    Component.literal("quick combo starter")
            );
    public static final KnockdownAttack<MagiciansRedEntity> HEAVY = new KnockdownAttack<MagiciansRedEntity>(100,
            12, 22, 1f, 7f, 10, 1.75f, 0.5f, 0.6f, 40)
            .withAnim(State.HEAVY)
            .withSound(JSoundRegistry.MR_HEAVY.get())
            .withImpactSound(JSoundRegistry.TW_KICK_HIT.get())
            .withInfo(
                    Component.literal("Low Kick"),
                    Component.literal("medium windup knockdown")
            );
    public static final SimpleAttack<MagiciansRedEntity> HAMMERFIST_FLARE = new SimpleAttack<MagiciansRedEntity>(0,
            1, 5, 1f, 6f, 10, 1.75f, 1.5f, -0.2f)
            .withLaunch()
            .withAction((attacker, user, ctx, targets) -> attacker.playSound(SoundEvents.FIRECHARGE_USE, 1.0f, 1.0f))
            .withHitSpark(JParticleType.HIT_SPARK_3)
            .withInfo(
                    Component.literal("Hammerfist Flare"),
                    Component.literal("launcher")
            );
    public static final SimpleAttack<MagiciansRedEntity> HAMMERFIST = new SimpleAttack<MagiciansRedEntity>(100,
            10, 20, 1f, 3f, 13, 1.75f, 0.2f, 0)
            .withSound(JSoundRegistry.MR_CROSSFIRE.get())
            .withFinisher(15, HAMMERFIST_FLARE)
            .withCrouchingVariant(HEAVY)
            .withImpactSound(JSoundRegistry.IMPACT_1.get())
            .withHitAnimation(CommonHitPropertyComponent.HitAnimation.CRUSH)
            .withInfo(
                    Component.literal("Hammerfist"),
                    Component.literal("two-hit launcher")
            );
    public static final FlamethrowerAttack FLAMETHROWER = new FlamethrowerAttack(300, 0,
            40, 0.75f, 0.4f, 0, 2, 0.25f, 0, 3)
            .withArmor(1)
            .withSound(JSoundRegistry.MR_BARRAGE.get())
            .withInfo(
                    Component.literal("Flamethrower"),
                    Component.literal("fast reliable damage cash-out tool, no stun, burns for 3 seconds")
            );
    public static final CrossfireAttack CROSSFIRE = new CrossfireAttack(100, 8, 10, 0.75f)
            .withSound(JSoundRegistry.MR_CROSSFIRE.get())
            .withInfo(
                    Component.literal("Crossfire"),
                    Component.literal("fires 3 stunning ankhs")
            );
    public static final CrossfireVariationAttack CROSSFIRE_VARIATION = new CrossfireVariationAttack(600, 12, 17, 0.75f)
            .withSound(JSoundRegistry.MR_CROSSFIRE.get())
            .withInfo(
                    Component.literal("Crossfire Variation"),
                    Component.literal("summons 6 ankhs that orbit around the user, crouch as they come out to increase orbit distance")
            );
    public static final CrossfireHurricaneAttack CROSSFIRE_HURRICANE = new CrossfireHurricaneAttack(800, 18, 22, 0.75f)
            .withSound(JSoundRegistry.MR_ULT.get())
            .withInfo(
                    Component.literal("Crossfire Hurricane"),
                    Component.literal("summons slow, homing fire hurricane that knocks down, lasts for 3 seconds after hitting anything")
            );
    public static final RedBindAttack RED_BIND = new RedBindAttack(300, 12, 22, 0.75f, 3, 15, 1.5f, 0, 0)
            .withSound(JSoundRegistry.MR_REDBIND.get())
            .withImpactSound(JSoundRegistry.IMPACT_3.get())
            .withInfo(
                    Component.literal("Red Bind"),
                    Component.literal("on hit, wraps opponent in fiery rings that launch them in the direction they are hit")
            );
    public static final LifeDetectorAttack LIFE_DETECTOR = new LifeDetectorAttack(280, 13, 20, 0.75f)
            .withSound(JSoundRegistry.MR_DETECTOR.get())
            .withInfo(
                    Component.literal("Life Detector"),
                    Component.literal("tracks down nearby life, lasts 15s")
            );

    public MagiciansRedEntity(Level worldIn) {
        super(StandType.MAGICIANS_RED, worldIn, JSoundRegistry.MR_SUMMON.get());
        idleRotation = 225f;

        proCount = 3;
        conCount = 3;

        freespace = """
                PASSIVE: Fire Resistance
                    
                BNBs:
                    -the "this move is fire"
                    Light>Crossfire
                    
                    -the happy camper
                    Light>Low Kick>Variation/Life Detector
                    
                    -the "omg i have setups????"
                    Light>Hammerfist>dash>Light>Red Bind>
                    ...Life Detector/Variation>any physical hit
                    ...Hurricane""";

        auraColors = new Vector3f[]{
                new Vector3f(0.9f, 0.6f, 0.3f),
                new Vector3f(0.8f, 0.3f, 1.0f),
                new Vector3f(1.0f, 0.0f, 0.0f),
                new Vector3f(1.0f, 0.2f, 0.4f)
        };
    }

    @Override
    protected void registerMoves(MoveMap<MagiciansRedEntity, State> moves) {
        moves.registerImmediate(MoveType.LIGHT, LIGHT, State.LIGHT);

        moves.registerImmediate(MoveType.HEAVY, HAMMERFIST, State.HAMMER);
        moves.register(MoveType.BARRAGE, FLAMETHROWER, State.BARRAGE);

        moves.register(MoveType.SPECIAL1, CROSSFIRE, State.CROSSFIRE);
        moves.register(MoveType.SPECIAL2, CROSSFIRE_VARIATION, State.CROSSFIRE_VARIATION);
        moves.register(MoveType.SPECIAL3, RED_BIND, State.RED_BIND);

        moves.register(MoveType.ULTIMATE, CROSSFIRE_HURRICANE, State.CROSSFIRE_HURRICANE);

        moves.register(MoveType.UTILITY, LIFE_DETECTOR, State.DETECTOR);
    }

    @Override
    public boolean initMove(MoveType type) {
        if (type == MoveType.LIGHT && getCurrentMove() != null && getCurrentMove().getMoveType() == MoveType.LIGHT && getMoveStun() < getCurrentMove().getWindupPoint()) {
            AbstractMove<?, ? super MagiciansRedEntity> followup = getCurrentMove().getFollowup();
            if (followup != null) {
                setMove(followup, (State) followup.getAnimation());
            }
        } else {
            return super.initMove(type);
        }

        return true;
    }

    public static void ignite(Level world, BlockPos blockPos) {
        BlockState state = world.getBlockState(blockPos);
        Block block = state.getBlock();
        Collection<Property<?>> properties = state.getProperties();

        boolean cantIgnite = false;
        if (properties.contains(BlockStateProperties.WATERLOGGED)) {
            cantIgnite = state.getValue(BlockStateProperties.WATERLOGGED);
        }
        if (block == Blocks.REDSTONE_LAMP) {
            return;
        }
        if (cantIgnite) {
            return;
        }

        if (properties.contains(BlockStateProperties.LIT)) {
            world.setBlockAndUpdate(blockPos, state.setValue(BlockStateProperties.LIT, true));
        }
        if (block == Blocks.WET_SPONGE) { // WetSpongeBlock has no drying function to call
            world.setBlock(blockPos, Blocks.SPONGE.defaultBlockState(), 3);
            world.levelEvent(2009, blockPos, 0);
            world.playSound(null, blockPos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 1.0F, (1.0F + world.getRandom().nextFloat() * 0.2F) * 0.7F);
        }
        if (world.getBlockEntity(blockPos) instanceof AbstractFurnaceBlockEntity furnaceBlock) {
            furnaceBlock.litTime = 220;
        }
        if (block instanceof IceBlock iceBlock) {
            iceBlock.melt(state, world, blockPos);
        }
    }

    @Override
    public MoveSelectionResult specificMoveSelectionCriterion(AbstractMove<?, ? super MagiciansRedEntity> attack, LivingEntity mob, LivingEntity target, int stunTicks, int enemyMoveStun, double distance, StandEntity<?, ?> enemyStand, AbstractMove<?, ?> enemyAttack) {
        if (attack.getOriginalMove() == REDIRECT && mob.getRandom().nextFloat() > 0.05f) return MoveSelectionResult.STOP;
        return MoveSelectionResult.PASS;
    }

    @Override
    public void tick() {
        super.tick();

        if (!hasUser()) {
            return;
        }

        if (level().isClientSide && getState() == State.BARRAGE && FLAMETHROWER.hasWindupPassed(this)) {
            Vec3 rotVec = getLookAngle();
            Vec3 mouthPos = getEyePosition().add(rotVec);
            for (int i = 0; i < 16; i++) {
                Vec3 vel = getUserOrThrow().getDeltaMovement().add(
                        rotVec
                                .xRot(random.nextFloat() - 0.5f)
                                .yRot(random.nextFloat() - 0.5f)
                                .zRot(random.nextFloat() - 0.5f)
                                .scale(0.2)
                );
                level().addParticle(
                        random.nextInt(6) == 5 ? ParticleTypes.LAVA : ParticleTypes.FLAME,
                        mouthPos.x, mouthPos.y, mouthPos.z,
                        vel.x, vel.y, vel.z
                );
            }
        }

        getUserOrThrow().addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 20, 0, true, false));
        CROSSFIRE_HURRICANE.tickHurricane(this);
    }

    @Override
    @NonNull
    public MagiciansRedEntity getThis() {
        return this;
    }

    // Animation code
    public enum State implements StandAnimationState<MagiciansRedEntity> {
        IDLE(builder -> builder.setAnimation(RawAnimation.begin().thenLoop("animation.mr.idle"))),
        LIGHT(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.mr.light"))),
        BLOCK(builder -> builder.setAnimation(RawAnimation.begin().thenLoop("animation.mr.block"))),
        HEAVY(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.mr.heavy"))),
        BARRAGE(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.mr.barrage"))),
        CROSSFIRE(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.mr.crossfire"))),
        CROSSFIRE_HURRICANE(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.mr.crossfirehurricane"))),
        CROSSFIRE_VARIATION(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.mr.crossfirevariation"))),
        REDIRECT(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.mr.redirect"))),
        RED_BIND(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.mr.redbind"))),
        DETECTOR(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.mr.detector"))),
        LIGHT_FOLLOWUP(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.mr.light_followup"))),
        HAMMER(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.mr.hammer")));

        private final Consumer<AnimationState<MagiciansRedEntity>> animator;

        State(Consumer<AnimationState<MagiciansRedEntity>> animator) {
            this.animator = animator;
        }

        @Override
        public void playAnimation(MagiciansRedEntity attacker, AnimationState<MagiciansRedEntity> builder) {
            animator.accept(builder);
        }
    }

    @Override
    protected State[] getStateValues() {
        return State.values();
    }

    @Override
    protected @Nullable String getSummonAnimation() {
        return "animation.mr.summon";
    }

    @Override
    public State getBlockState() {
        return State.BLOCK;
    }
}
