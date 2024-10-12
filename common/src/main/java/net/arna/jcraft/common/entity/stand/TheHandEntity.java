package net.arna.jcraft.common.entity.stand;

import lombok.NonNull;
import mod.azure.azurelib.core.animation.AnimationState;
import net.arna.jcraft.common.attack.core.MoveMap;
import net.arna.jcraft.common.util.StandAnimationState;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

/**
 * The {@link StandEntity} for <a href="https://jojowiki.com/The_Hand">The Hand</a>.
 * @see StandType#THE_HAND
 * @see net.arna.jcraft.client.model.entity.stand.TheHandModel TheHandModel
 * @see net.arna.jcraft.client.renderer.entity.stands.TheHandRenderer TheHandRenderer
 */
public class TheHandEntity extends StandEntity<TheHandEntity, TheHandEntity.State> {

    public TheHandEntity(final Level world) {
        super(StandType.THE_HAND, world);
    }

    @Override
    protected void registerMoves(final MoveMap<TheHandEntity, State> moves) {
        // TODO Arna
    }

    @Override
    public @NonNull TheHandEntity getThis() {
        return this;
    }

    public enum State implements StandAnimationState<TheHandEntity> {
        IDLE,
        BLOCK;

        @Override
        public void playAnimation(final TheHandEntity attacker, final AnimationState<TheHandEntity> state) {
            // TODO Arna
        }
    }

    @Override
    protected State[] getStateValues() {
        return State.values();
    }

    @Override
    protected @Nullable String getSummonAnimation() {
        return "animation.the_hand.summon";
    }

    @Override
    public State getBlockState() {
        return State.BLOCK;
    }
}
