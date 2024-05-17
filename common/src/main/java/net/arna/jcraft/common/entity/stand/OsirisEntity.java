package net.arna.jcraft.common.entity.stand;

import lombok.NonNull;
import net.arna.jcraft.common.attack.core.MoveMap;
import net.arna.jcraft.common.util.StandAnimationState;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import mod.azure.azurelib.core.animation.AnimationState;

public class OsirisEntity extends StandEntity<OsirisEntity, OsirisEntity.State> {

    public OsirisEntity(Level world) {
        super(StandType.OSIRIS, world);
    }

    @Override
    protected void registerMoves(MoveMap<OsirisEntity, State> moves) {
        // TODO Arna
    }

    @Override
    public @NonNull OsirisEntity getThis() {
        return this;
    }

    public enum State implements StandAnimationState<OsirisEntity> {
        IDLE,
        BLOCK;

        @Override
        public void playAnimation(OsirisEntity attacker, AnimationState state) {
            // TODO Arna
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
        return "animation.osiris.summon";
    }
}
