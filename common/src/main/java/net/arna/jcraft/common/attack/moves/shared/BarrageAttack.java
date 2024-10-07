package net.arna.jcraft.common.attack.moves.shared;

import lombok.NonNull;
import net.arna.jcraft.common.attack.core.IAttacker;
import net.arna.jcraft.common.attack.moves.base.AbstractBarrageAttack;

/**
 * A simple attack that performs at a set interval.
 */
public final class BarrageAttack<A extends IAttacker<? extends A, ?>> extends AbstractBarrageAttack<BarrageAttack<A>, A> {

    public BarrageAttack(final int cooldown, final int windup, final int duration, final float attackDistance, final float damage, final int stun,
                         final float hitboxSize, final float knockback, final float offset, final int interval) {
        super(cooldown, windup, duration, attackDistance, damage, stun, hitboxSize, knockback, offset, interval);
    }

    @Override
    protected @NonNull BarrageAttack<A> getThis() {
        return this;
    }

    @Override
    public @NonNull BarrageAttack<A> copy() {
        return copyExtras(new BarrageAttack<>(getCooldown(), getWindup(), getDuration(), getMoveDistance(), getDamage(), getStun(),
                getHitboxSize(), getKnockback(), getOffset(), getInterval()));
    }
}
