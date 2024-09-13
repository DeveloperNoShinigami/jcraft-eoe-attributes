package net.arna.jcraft.common.entity.stand;

import lombok.NonNull;
import mod.azure.azurelib.core.animation.AnimationState;
import mod.azure.azurelib.core.animation.RawAnimation;
import net.arna.jcraft.common.attack.core.MoveMap;
import net.arna.jcraft.common.util.StandAnimationState;
import net.arna.jcraft.registry.JSoundRegistry;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class MetallicaEntity extends StandEntity<MetallicaEntity, MetallicaEntity.State> {
    public MetallicaEntity(Level worldIn) {
        super(StandType.METALLICA, worldIn, JSoundRegistry.STAND_SUMMON.get());
        
        idleDistance = 0;
    }

    @Override
    protected void registerMoves(MoveMap<MetallicaEntity, MetallicaEntity.State> moves) {
        
    }

    @Override
    public @NonNull MetallicaEntity getThis() {
        return this;
    }

    // Animations
    public enum State implements StandAnimationState<MetallicaEntity> {
        IDLE(builder -> builder.setAnimation(RawAnimation.begin().thenLoop("animation.metallica.idle"))),
        DUAL_CHOP(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.metallica.dual_chop"))),
        BLOCK(builder -> builder.setAnimation(RawAnimation.begin().thenLoop("animation.metallica.block"))),
        OVERHEAD(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.metallica.overhead"))),
        DONUT(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.metallica.donut"))),
        BARRAGE(builder -> builder.setAnimation(RawAnimation.begin().thenLoop("animation.metallica.barrage"))),
        EYE_CHOP(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.metallica.eye_chop"))),
        TIME_ERASE(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.metallica.time_erase"))),
        EPITAPH(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.metallica.epitaph"))),
        HEAVY(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.metallica.heavy"))),
        BLOOD_THROW(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.metallica.bloodthrow"))),
        PREDICT(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.metallica.predict"))),
        COUNTER_MISS(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.metallica.counter_miss"))),
        SWEEP(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.metallica.sweep"))),
        TIME_SKIP(builder -> builder.setAnimation(RawAnimation.begin().thenLoop("animation.metallica.idle")));

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
