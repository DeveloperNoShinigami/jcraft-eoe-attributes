package net.arna.jcraft.common.attack.moves.shared;

import lombok.NonNull;
import net.arna.jcraft.common.attack.moves.base.AbstractChargeAttack;
import net.arna.jcraft.common.entity.stand.StandEntity;
import net.arna.jcraft.common.util.StandAnimationState;

public final class ChargeAttack<S extends StandEntity<S, A>, A extends Enum<A> & StandAnimationState<S>>
        extends AbstractChargeAttack<ChargeAttack<S, A>, S, A> {
    public ChargeAttack(final int cooldown, final int windup, final int duration, final float moveDistance, final float damage, final int stun,
                        final float hitboxSize, final float knockback, final float offset, final A hitAnimState) {
        super(cooldown, windup, duration, moveDistance, damage, stun, hitboxSize, knockback, offset, hitAnimState);
    }

    @Override
    protected @NonNull ChargeAttack<S, A> getThis() {
        return this;
    }

    @Override
    public @NonNull ChargeAttack<S, A> copy() {
        return copyExtras(new ChargeAttack<>(getCooldown(), getWindup(), getDuration(), getMoveDistance(), getDamage(), getStun(),
                getHitboxSize(), getKnockback(), getOffset(), getHitAnimState()));
    }
}
