package net.arna.jcraft.common.entity.stand;

import lombok.NonNull;
import mod.azure.azurelib.core.animation.AnimationState;
import net.arna.jcraft.common.attack.core.MoveMap;
import net.arna.jcraft.common.util.StandAnimationState;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class DragonsDreamEntity extends StandEntity<DragonsDreamEntity, DragonsDreamEntity.State> {

    public DragonsDreamEntity(Level world) {
        super(StandType.DRAGONS_DREAM, world);
    }

    public enum State implements StandAnimationState<DragonsDreamEntity> {
        IDLE,
        BLOCK;

        @Override
        public void playAnimation(DragonsDreamEntity attacker, AnimationState state) {
            // TODO Arna
        }
    }

    @Override
    public @NonNull DragonsDreamEntity getThis() {
        return this;
    }

    @Override
    protected State[] getStateValues() {
        return State.values();
    }

    @Override
    protected @Nullable String getSummonAnimation() {
        return "animation.dragons_dream.summon";
    }

    @Override
    public State getBlockState() {
        return State.BLOCK;
    }

    @Override
    protected void registerMoves(MoveMap<DragonsDreamEntity, State> moves) {
        // TODO Arna
    }
}
