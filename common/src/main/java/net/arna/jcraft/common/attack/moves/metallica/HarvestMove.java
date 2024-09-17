package net.arna.jcraft.common.attack.moves.metallica;

import lombok.NonNull;
import net.arna.jcraft.common.attack.core.MoveInputType;
import net.arna.jcraft.common.attack.moves.base.AbstractBarrageAttack;
import net.arna.jcraft.common.entity.stand.MetallicaEntity;

public class HarvestMove extends AbstractBarrageAttack<HarvestMove, MetallicaEntity> {
    public HarvestMove() {
        super(0, 0, 60 * 20, 0.75f, 0, 0, 0, 0, 0, 3);
        withHoldable();
    }

    @Override
    public void onUserMoveInput(MetallicaEntity attacker, MoveInputType type, boolean pressed, boolean moveInitiated) {
        super.onUserMoveInput(attacker, type, pressed, moveInitiated);
        // Must be held
        if (type.getMoveType() == getMoveType() && !pressed) attacker.cancelMove();
    }

    @Override
    protected @NonNull HarvestMove getThis() {
        return this;
    }

    @Override
    public @NonNull HarvestMove copy() {
        return copyExtras(new HarvestMove());
    }
}
