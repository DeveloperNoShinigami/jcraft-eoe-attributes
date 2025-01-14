package net.arna.jcraft.common.entity.stand;

import lombok.NonNull;
import mod.azure.azurelib.core.animation.AnimationState;
import net.arna.jcraft.common.attack.core.MoveMap;
import net.arna.jcraft.common.attack.core.data.MoveSet;
import net.arna.jcraft.common.util.StandAnimationState;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

/**
 * The {@link StandEntity} for <a href="https://jojowiki.com/Dragon%27s_Dream">Dragon's Dream</a>.
 * @see StandType#DRAGONS_DREAM
 * @see net.arna.jcraft.client.model.entity.stand.DragonsDreamModel DragonsDreamModel
 * @see net.arna.jcraft.client.renderer.entity.stands.DragonsDreamRenderer DragonsDreamRenderer
 */
public class DragonsDreamEntity extends StandEntity<DragonsDreamEntity, DragonsDreamEntity.State> {
    public static final MoveSet<DragonsDreamEntity, State> MOVE_SET = MoveSet.create(StandType.DRAGONS_DREAM,
            DragonsDreamEntity::registerMoves, State.class);

    public DragonsDreamEntity(Level world) {
        super(StandType.DRAGONS_DREAM, world);
    }

    private static void registerMoves(MoveMap<DragonsDreamEntity, State> moves) {
        // TODO Arna
    }

    public enum State implements StandAnimationState<DragonsDreamEntity> {
        IDLE,
        BLOCK;

        @Override
        public void playAnimation(DragonsDreamEntity attacker, AnimationState<DragonsDreamEntity> state) {
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
}
