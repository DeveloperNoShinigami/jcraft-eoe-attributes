package net.arna.jcraft.common.entity.stand;

import lombok.NonNull;
import mod.azure.azurelib.core.animation.AnimationState;
import net.arna.jcraft.common.attack.core.MoveMap;
import net.arna.jcraft.common.util.StandAnimationState;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class GooGooDollsEntity extends StandEntity<GooGooDollsEntity, GooGooDollsEntity.State> {

    public GooGooDollsEntity(Level world) {
        super(StandType.GOO_GOO_DOLLS, world);
    }

    public enum State implements StandAnimationState<GooGooDollsEntity> {
        IDLE,
        BLOCK;

        @Override
        public void playAnimation(GooGooDollsEntity attacker, AnimationState state) {
            // TODO Arna
        }
    }

    @Override
    public @NonNull GooGooDollsEntity getThis() {
        return this;
    }

    @Override
    protected State[] getStateValues() {
        return State.values();
    }

    @Override
    protected @Nullable String getSummonAnimation() {
        return "animation.goo_goo_dolls.summon";
    }

    @Override
    public State getBlockState() {
        return State.BLOCK;
    }

    @Override
    protected void registerMoves(MoveMap<GooGooDollsEntity, State> moves) {
        // TODO Arna
    }
}
