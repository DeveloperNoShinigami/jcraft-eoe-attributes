package net.arna.jcraft.common.attack.moves.base;

import lombok.Getter;
import net.arna.jcraft.common.attack.core.IAttacker;
import net.arna.jcraft.common.attack.core.MoveInputType;

@Getter
public abstract class AbstractHoldableMove<T extends AbstractHoldableMove<T, A, S>, A extends IAttacker<A, S>, S>
        extends AbstractMove<T, A> {
    private final AbstractMove<?, ? super A> followupMove;
    protected boolean setMoveStun = false;
    private final S followupState;
    private final int minimumCharge;
    // Maximum charge is the end of the move

    protected AbstractHoldableMove(final int cooldown, final int windup, final int duration, final float attackDistance,
                                   final AbstractMove<?, ? super A> followupMove, final S followupState, final int minimumCharge) {
        super(cooldown, windup, duration, attackDistance);

        this.followupMove = followupMove;
        this.followupState = followupState;
        this.minimumCharge = minimumCharge;

        withHoldable();
    }

    public T shouldSetMoveStun() {
        setMoveStun = true;
        return getThis();
    }

    @Override
    public void tick(final A attacker, final int moveStun) {
        super.tick(attacker, moveStun);
        final boolean charged = moveStun <= getDuration() - minimumCharge;
        if (moveStun == 1 || (charged && (!attacker.isHolding() || attacker.getHoldingType().getMoveType() != getMoveType()))) {
            followUp(attacker);
        }
    }

    @Override
    public void onUserMoveInput(final A attacker, final MoveInputType type, final boolean pressed, final boolean moveInitiated) {
        if (!pressed && moveInitiated && type.getMoveType() == getMoveType()) {
            onRelease(attacker);
        }
    }

    public void onRelease(final A attacker) {
        if (attacker.getMoveStun() <= getDuration() - minimumCharge) {
            followUp(attacker);
        }
    }

    private void followUp(final A attacker) {
        attacker.setMove(followupMove, followupState);
        if (setMoveStun) {
            attacker.setMoveStun(followupMove.getDuration());
        }
    }
}
