package net.arna.jcraft.common.entity.stand;

import lombok.NonNull;
import mod.azure.azurelib.core.animation.AnimationState;
import mod.azure.azurelib.core.animation.RawAnimation;
import net.arna.jcraft.common.attack.core.MoveMap;
import net.arna.jcraft.common.attack.core.MoveType;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.moves.shared.SimpleAttack;
import net.arna.jcraft.common.entity.projectile.ScalpelProjectile;
import net.arna.jcraft.common.gravity.api.GravityChangerAPI;
import net.arna.jcraft.common.util.JParticleType;
import net.arna.jcraft.common.util.JUtils;
import net.arna.jcraft.common.util.StandAnimationState;
import net.arna.jcraft.registry.JSoundRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.Set;
import java.util.function.Consumer;

public class MetallicaEntity extends StandEntity<MetallicaEntity, MetallicaEntity.State> {
    private static final EntityDataAccessor<BlockPos> SIPHON_POS;
    static {
        SIPHON_POS = SynchedEntityData.defineId(MetallicaEntity.class, EntityDataSerializers.BLOCK_POS);
    }

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
    public static final SimpleAttack<MetallicaEntity> PRECISE_TOSS = new SimpleAttack<MetallicaEntity>(
            60, 7, 12, 0.75f, 0, 0, 0, 0, 0)
            .withInfo(
                    Component.literal("Scalpel Toss (Precise)"),
                    Component.literal("""
                                    Relatively slow, very low cooldown.
                                    Fires 3 scalpels in the exact pointed direction.""")
            )
            .markRanged()
            .withAction(MetallicaEntity::preciseToss)
            .withInitAction((attacker, user, ctx) -> JUtils.playAnimIfUnoccupied(user, "mtl.pt"));

    private static void preciseToss(MetallicaEntity stand, LivingEntity user, MoveContext context, Set<LivingEntity> livingEntities) {
        Vec3 pos = stand.position();
        Vec3 upVec = GravityChangerAPI.getEyeOffset(user);
        for (int i = 0; i < 3; i++) {
            ScalpelProjectile scalpel = new ScalpelProjectile(user.level(), user);
            scalpel.setPos(pos.add(upVec.scale(0.25 * i)));
            scalpel.shootFromRotation(user, user.getXRot(), user.getYRot(), 0.0F, 1.0F, 0.0F);
            stand.level().addFreshEntity(scalpel);
        }
    }

    public MetallicaEntity(Level worldIn) {
        super(StandType.METALLICA, worldIn, JSoundRegistry.STAND_SUMMON.get());
        
        idleDistance = 0;

        auraColors = new Vector3f[] {
                new Vector3f(0.1f, 0.1f, 0.4f)
        };
    }

    @Override
    public boolean initMove(MoveType type) {
        if (tryFollowUp(type, MoveType.LIGHT)) return true;
        return super.initMove(type);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(SIPHON_POS, null);
    }

    @Override
    protected void registerMoves(MoveMap<MetallicaEntity, MetallicaEntity.State> moves) {
        var light = moves.register(MoveType.LIGHT, LIGHT, State.LIGHT);
        light.withFollowUp(State.LIGHT_FOLLOWUP).withFollowUp(State.LIGHT_FINAL);

        moves.register(MoveType.SPECIAL1, PRECISE_TOSS, State.PRECISE_TOSS);
    }

    @Override
    public @NonNull MetallicaEntity getThis() {
        return this;
    }

    // Animations
    public enum State implements StandAnimationState<MetallicaEntity> {
        IDLE(builder -> builder.setAnimation(RawAnimation.begin().thenLoop("animation.metallica.idle"))),
        BLOCK(builder -> builder.setAnimation(RawAnimation.begin().thenLoop("animation.metallica.block"))),
        LIGHT(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.metallica.light"))),
        LIGHT_FOLLOWUP(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.metallica.light2"))),
        LIGHT_FINAL(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.metallica.light3"))),
        PRECISE_TOSS(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.metallica.precise_toss"))),
        ;

        private final Consumer<AnimationState> animator;

        State(Consumer<AnimationState> animator) {
            this.animator = animator;
        }

        @Override
        public void playAnimation(MetallicaEntity attacker, AnimationState builder) {
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
