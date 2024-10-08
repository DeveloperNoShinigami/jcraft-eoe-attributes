package net.arna.jcraft.common.entity.stand;

import lombok.NonNull;
import mod.azure.azurelib.core.animation.AnimationState;
import net.arna.jcraft.common.attack.core.MoveMap;
import net.arna.jcraft.common.util.StandAnimationState;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

/**
 * The {@link StandEntity} for <a href="https://jojowiki.com/Diver_Down">Diver Down</a>.
 * @see StandType#DIVER_DOWN
 * @see net.arna.jcraft.client.model.entity.stand.DiverDownModel DiverDownModel
 * @see net.arna.jcraft.client.renderer.entity.stands.DiverDownRenderer DiverDownRenderer
 */
public class DiverDownEntity extends StandEntity<DiverDownEntity, DiverDownEntity.State> {

    public DiverDownEntity(Level world) {
        super(StandType.DIVER_DOWN, world);
    }

    public enum State implements StandAnimationState<DiverDownEntity> {
        IDLE,
        BLOCK;

        @Override
        public void playAnimation(DiverDownEntity attacker, AnimationState state) {
            // TODO Arna
        }
    }

    @Override
    public @NonNull DiverDownEntity getThis() {
        return this;
    }

    @Override
    protected State[] getStateValues() {
        return State.values();
    }

    @Override
    protected @Nullable String getSummonAnimation() {
        return "animation.my.summon";
    }

    @Override
    public State getBlockState() {
        return State.BLOCK;
    }

    @Override
    protected void registerMoves(MoveMap<DiverDownEntity, State> moves) {
        // TODO Arna
    }
}
