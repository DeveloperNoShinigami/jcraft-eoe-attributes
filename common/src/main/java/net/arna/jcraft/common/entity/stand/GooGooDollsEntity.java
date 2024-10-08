package net.arna.jcraft.common.entity.stand;

import lombok.NonNull;
import mod.azure.azurelib.core.animation.AnimationState;
import net.arna.jcraft.common.attack.core.MoveMap;
import net.arna.jcraft.common.util.StandAnimationState;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

/**
 * The {@link StandEntity} for <a href="https://jojowiki.com/Goo_Goo_Dolls">Goo Goo Dolls</a>.
 * @see StandType#GOO_GOO_DOLLS
 * @see net.arna.jcraft.client.model.entity.stand.GooGooDollsModel GooGooDollsModel
 * @see net.arna.jcraft.client.renderer.entity.stands.GooGooDollsRenderer GooGooDollsRenderer
 */
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
