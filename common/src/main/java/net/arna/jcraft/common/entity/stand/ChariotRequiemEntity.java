package net.arna.jcraft.common.entity.stand;

import lombok.NonNull;
import mod.azure.azurelib.core.animation.AnimationState;
import net.arna.jcraft.common.attack.core.MoveMap;
import net.arna.jcraft.common.attack.core.data.MoveSet;
import net.arna.jcraft.common.util.StandAnimationState;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

/**
 * The {@link StandEntity} for <a href="https://jojowiki.com/Chariot_Requiem">Chariot Requiem</a>.
 * @see StandType#CHARIOT_REQUIEM
 * @see net.arna.jcraft.client.model.entity.stand.ChariotRequiemModel ChariotRequiemModel
 * @see net.arna.jcraft.client.renderer.entity.stands.ChariotRequiemRenderer ChariotRequiemRenderer
 */
public class ChariotRequiemEntity extends StandEntity<ChariotRequiemEntity, ChariotRequiemEntity.State> {
    public static final MoveSet<ChariotRequiemEntity, State> MOVE_SET = MoveSet.create(StandType.CHARIOT_REQUIEM,
            ChariotRequiemEntity::registerMoves, State.class);

    public ChariotRequiemEntity(Level world) {
        super(StandType.CHARIOT_REQUIEM, world);
    }

    private static void registerMoves(MoveMap<ChariotRequiemEntity, State> moves) {
        // TODO Arna
    }

    @Override
    public @NonNull ChariotRequiemEntity getThis() {
        return this;
    }

    public enum State implements StandAnimationState<ChariotRequiemEntity> {
        IDLE,
        BLOCK;

        @Override
        public void playAnimation(ChariotRequiemEntity attacker, AnimationState<ChariotRequiemEntity> state) {
            // TODO Arna
        }
    }

    @Override
    protected State[] getStateValues() {
        return State.values();
    }

    @Override
    protected @Nullable String getSummonAnimation() {
        return "animation.chariotrequiem.summon";
    }

    @Override
    public State getBlockState() {
        return State.BLOCK;
    }
}
