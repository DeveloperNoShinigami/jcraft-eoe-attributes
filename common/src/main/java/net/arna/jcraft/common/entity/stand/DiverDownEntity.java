package net.arna.jcraft.common.entity.stand;

import lombok.NonNull;
import mod.azure.azurelib.core.animation.AnimationState;
import net.arna.jcraft.api.StandData;
import net.arna.jcraft.api.StandInfo;
import net.arna.jcraft.api.attack.MoveSetManager;
import net.arna.jcraft.common.attack.core.MoveMap;
import net.arna.jcraft.api.attack.MoveSet;
import net.arna.jcraft.common.util.StandAnimationState;
import net.arna.jcraft.registry.JStandTypeRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

/**
 * The {@link StandEntity} for <a href="https://jojowiki.com/Diver_Down">Diver Down</a>.
 * @see JStandTypeRegistry#DIVER_DOWN
 * @see net.arna.jcraft.client.model.entity.stand.DiverDownModel DiverDownModel
 * @see net.arna.jcraft.client.renderer.entity.stands.DiverDownRenderer DiverDownRenderer
 */
public class DiverDownEntity extends StandEntity<DiverDownEntity, DiverDownEntity.State> {
    public static final MoveSet<DiverDownEntity, State> MOVE_SET = MoveSetManager.create(JStandTypeRegistry.DIVER_DOWN,
            DiverDownEntity::registerMoves, State.class);
    public static final StandData DATA = StandData.of(StandInfo.of(Component.translatable("entity.jcraft.diver_down")))
            .withObtainable(false);

    public DiverDownEntity(Level world) {
        super(JStandTypeRegistry.DIVER_DOWN.get(), world);
    }

    private static void registerMoves(MoveMap<DiverDownEntity, State> moves) {
        // TODO Arna
    }

    public enum State implements StandAnimationState<DiverDownEntity> {
        IDLE,
        BLOCK;

        @Override
        public void playAnimation(DiverDownEntity attacker, AnimationState<DiverDownEntity> state) {
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
}
