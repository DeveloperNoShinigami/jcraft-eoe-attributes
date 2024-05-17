package net.arna.jcraft.common.entity.stand;

import lombok.NonNull;
import net.arna.jcraft.common.attack.core.MoveMap;
import net.arna.jcraft.common.util.StandAnimationState;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import mod.azure.azurelib.core.animation.AnimationState;

public class HorusEntity extends StandEntity<HorusEntity, HorusEntity.State> {

    public HorusEntity(Level world) {
        super(StandType.HORUS, world);
        // TODO Arna
    }

    @Override
    protected void registerMoves(MoveMap<HorusEntity, HorusEntity.State> moves) {
        // TODO Arna
    }

    @Override
    public @NonNull HorusEntity getThis() {
        return this;
    }

    // Animation code
    public enum State implements StandAnimationState<HorusEntity> {
        // TODO Arna
        IDLE,
        BLOCK;

        @Override
        public void playAnimation(HorusEntity attacker, AnimationState state) {
            // TODO Arna
        }
    }

    @Override
    protected HorusEntity.State[] getStateValues() {
        return State.values();
    }

    @Override
    protected @Nullable String getSummonAnimation() {
        return "animation.horus.summon";
    }

    @Override
    public HorusEntity.State getBlockState() {
        return State.BLOCK;
    }
}
