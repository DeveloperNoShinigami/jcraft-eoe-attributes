package net.arna.jcraft.common.entity.stand;

import lombok.NonNull;
import net.arna.jcraft.common.attack.core.MoveMap;
import net.arna.jcraft.common.util.StandAnimationState;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.core.animation.AnimationState;

public class CinderellaEntity extends StandEntity<CinderellaEntity, CinderellaEntity.State> {

    public CinderellaEntity(World world) {
        super(StandType.CINDERELLA, world);
    }

    @Override
    protected void registerMoves(MoveMap<CinderellaEntity, State> moves) {
        // TODO Arna
    }

    @Override
    public @NonNull CinderellaEntity getThis() {
        return this;
    }

    public enum State implements StandAnimationState<CinderellaEntity> {
        IDLE,
        BLOCK;

        @Override
        public void playAnimation(CinderellaEntity attacker, AnimationState state) {
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
        return "animation.cinderella.summon";
    }
}
