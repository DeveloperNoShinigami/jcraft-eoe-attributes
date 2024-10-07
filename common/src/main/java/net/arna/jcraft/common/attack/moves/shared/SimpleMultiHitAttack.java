package net.arna.jcraft.common.attack.moves.shared;

import it.unimi.dsi.fastutil.ints.IntCollection;
import lombok.NonNull;
import net.arna.jcraft.common.attack.core.IAttacker;
import net.arna.jcraft.common.attack.moves.base.AbstractMultiHitAttack;

/**
 * The simplest implementation of {@link AbstractMultiHitAttack}.
 * Only special feature is that it fires the same hitbox at set points.
 *
 * @param <A>
 */
public final class SimpleMultiHitAttack<A extends IAttacker<? extends A, ?>> extends AbstractMultiHitAttack<SimpleMultiHitAttack<A>, A> {
    public SimpleMultiHitAttack(final int cooldown, final int duration, final float moveDistance, final float damage, final int stun, final float hitboxSize, final float knockback,
                                final float offset, final IntCollection hitMoments) {
        super(cooldown, duration, moveDistance, damage, stun, hitboxSize, knockback, offset, hitMoments);
    }

    /**
     * For light attacks
     *
     * @param duration     The duration after which a new attack can be initiated in ticks.
     * @param moveDistance The distance at which the hitbox is placed.
     * @param damage       The damage this attack deals.
     * @param offset       The amount the hitbox is offset by.
     * @param hitMoments   The ticks at which this attack is performed.
     */
    public static <A extends IAttacker<? extends A, ?>> SimpleMultiHitAttack<A> lightAttack(final int duration, final float moveDistance, final float damage,
                                                                                            final int stun,
                                                                                            final float offset, final IntCollection hitMoments) {
        return new SimpleMultiHitAttack<>(30, duration, moveDistance, damage, stun, 1.5f, 0.75f,
                offset, hitMoments);
    }

    @Override
    protected @NonNull SimpleMultiHitAttack<A> getThis() {
        return this;
    }

    @Override
    public @NonNull SimpleMultiHitAttack<A> copy() {
        return copyExtras(new SimpleMultiHitAttack<>(getCooldown(), getDuration(), getMoveDistance(), getDamage(), getStun(), getHitboxSize(),
                getKnockback(), getOffset(), getHitMoments()));
    }
}
