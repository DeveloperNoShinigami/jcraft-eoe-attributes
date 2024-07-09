package net.arna.jcraft.common.entity.stand;

import lombok.NonNull;
import mod.azure.azurelib.core.animation.AnimationState;
import net.arna.jcraft.common.attack.core.MoveMap;
import net.arna.jcraft.common.util.StandAnimationState;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class FooFightersEntity extends StandEntity<FooFightersEntity, FooFightersEntity.State> {

    public FooFightersEntity(Level world) {
        super(StandType.FOO_FIGHTERS, world);
    }

    public enum State implements StandAnimationState<FooFightersEntity> {
        IDLE,
        BLOCK;

        @Override
        public void playAnimation(FooFightersEntity attacker, AnimationState state) {
            // TODO Arna
        }
    }

    @Override
    public @NonNull FooFightersEntity getThis() {
        return this;
    }

    @Override
    protected State[] getStateValues() {
        return State.values();
    }

    @Override
    protected @Nullable String getSummonAnimation() {
        return "animation.foo_fighters.summon";
    }

    @Override
    public State getBlockState() {
        return State.BLOCK;
    }

    @Override
    protected void registerMoves(MoveMap<FooFightersEntity, State> moves) {
        // TODO Arna
    }
}
