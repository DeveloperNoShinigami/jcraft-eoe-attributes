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
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class CinderellaEntity extends StandEntity<CinderellaEntity, CinderellaEntity.State> {
    public static final SimpleAttack<CinderellaEntity> LIGHT = SimpleAttack.<CinderellaEntity>lightAttack(
                    7, 11, 0.75f, 4f, 11, 0.15f, 0.2f)
            // .withFollowup(LIGHT_FOLLOWUP)
            // .withCrouchingVariant(CROUCHING_LIGHT)
            // .withAerialVariant(AIR_LIGHT)
            .withImpactSound(JSoundRegistry.IMPACT_2.get())
            .withInfo(
                    Component.literal("Punch"),
                    Component.literal("quick combo starter")
            );

    public CinderellaEntity(Level world) {
        super(StandType.CINDERELLA, world);
    }

    @Override
    protected void registerMoves(MoveMap<CinderellaEntity, State> moves) {
        moves.registerImmediate(MoveType.LIGHT, LIGHT, State.LIGHT);
    }

    @Override
    public void tick() {
        super.tick();
        // impossibly ghetto fix. we do not care.
        if (!hasUser()) {
            discard();
            return;
        }
        if (level().isClientSide()) return;
        if (getUserOrThrow().getFirstPassenger() != this) discard();
    }

    @Override
    public @NonNull CinderellaEntity getThis() {
        return this;
    }

    public enum State implements StandAnimationState<CinderellaEntity> {
        IDLE(builder -> builder.setAnimation(RawAnimation.begin().thenLoop("animation.cinderella.idle"))),
        LIGHT(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.cinderella.light"))),
        BLOCK(builder -> builder.setAnimation(RawAnimation.begin().thenLoop("animation.cinderella.block"))),
        ;

        private final Consumer<AnimationState<CinderellaEntity>> animator;

        State(Consumer<AnimationState<CinderellaEntity>> animator) {
            this.animator = animator;
        }

        @Override
        public void playAnimation(CinderellaEntity attacker, AnimationState<CinderellaEntity> state) {
            animator.accept(state);
        }
    }

    @Override
    protected State[] getStateValues() {
        return State.values();
    }

    @Override
    public State getBlockState() {
        return State.BLOCK;
    }

    @Override
    protected @Nullable String getSummonAnimation() {
        return "animation.cinderella.summon";
    }
}
