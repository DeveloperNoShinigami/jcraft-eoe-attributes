package net.arna.jcraft.common.entity.stand;

import lombok.NonNull;
import mod.azure.azurelib.core.animation.AnimationState;
import mod.azure.azurelib.core.animation.RawAnimation;
import net.arna.jcraft.common.attack.core.MoveMap;
import net.arna.jcraft.common.attack.core.MoveType;
import net.arna.jcraft.common.attack.moves.shared.SimpleAttack;
import net.arna.jcraft.common.util.StandAnimationState;
import net.arna.jcraft.registry.JSoundRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class MetallicaEntity extends StandEntity<MetallicaEntity, MetallicaEntity.State> {
    public static final SimpleAttack<MetallicaEntity> LIGHT_FOLLOWUP = SimpleAttack.<MetallicaEntity>lightAttack(
                    6, 13, 0.75f, 5f, 10, 0.25f, 0.2f)
            .withAnim(State.LIGHT_FOLLOWUP)
            // .withFollowup(LIGHT_LAUNCH)
            .withImpactSound(SoundEvents.PLAYER_ATTACK_SWEEP)
            .withInfo(
                    Component.literal("Slice"),
                    Component.literal("quick combo starter")
            );
    public static final SimpleAttack<MetallicaEntity> LIGHT = SimpleAttack.<MetallicaEntity>lightAttack(
                    6, 10, 0.75f, 5f, 11, 0.15f, 0.2f)
            .withFollowup(LIGHT_FOLLOWUP)
            // .withCrouchingVariant(CROUCHING_LIGHT)
            // .withAerialVariant(AIR_LIGHT)
            .withImpactSound(SoundEvents.PLAYER_ATTACK_SWEEP)
            .withInfo(
                    Component.literal("Slice"),
                    Component.literal("quick combo starter")
            );

    public MetallicaEntity(Level worldIn) {
        super(StandType.METALLICA, worldIn, JSoundRegistry.STAND_SUMMON.get());
        
        idleDistance = 0;
    }

    @Override
    public boolean initMove(MoveType type) {
        if (tryFollowUp(type, MoveType.LIGHT)) return true;
        return super.initMove(type);
    }

    @Override
    protected void registerMoves(MoveMap<MetallicaEntity, MetallicaEntity.State> moves) {
        moves.registerImmediate(MoveType.LIGHT, LIGHT, State.LIGHT);
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
