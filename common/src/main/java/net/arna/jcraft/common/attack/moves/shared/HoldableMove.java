package net.arna.jcraft.common.attack.moves.shared;

import lombok.NonNull;
import net.arna.jcraft.common.attack.core.IAttacker;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.moves.base.AbstractHoldableMove;
import net.arna.jcraft.common.attack.moves.base.AbstractMove;
import net.minecraft.world.entity.LivingEntity;
import java.util.Set;

public final class HoldableMove<A extends IAttacker<A, S>, S extends Enum<S>> extends AbstractHoldableMove<HoldableMove<A, S>, A, S> {
    public HoldableMove(final int cooldown, final int windup, final int duration, final float attackDistance, final AbstractMove<?, ? super A> followupMove, final S followupState, final int minimumCharge) {
        super(cooldown, windup, duration, attackDistance, followupMove, followupState, minimumCharge);
        withFollowup(followupMove);
    }

    @Override
    public @NonNull Set<LivingEntity> perform(final A attacker, final LivingEntity user, final MoveContext ctx) {
        return Set.of();
    }

    @Override
    protected @NonNull HoldableMove<A, S> getThis() {
        return this;
    }

    @Override
    public @NonNull HoldableMove<A, S> copy() {
        HoldableMove<A, S> copy = new HoldableMove<A, S>(getCooldown(), getWindup(), getDuration(), getMoveDistance(), getFollowupMove(), getFollowupState(), getMinimumCharge());
        if (setMoveStun) {
            copy.shouldSetMoveStun();
        }
        return copyExtras(copy);
    }
}
