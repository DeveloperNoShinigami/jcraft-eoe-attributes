package net.arna.jcraft.common.attack.moves.base;

import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntLinkedOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSortedSet;
import it.unimi.dsi.fastutil.ints.IntSortedSets;
import lombok.Getter;
import lombok.NonNull;
import net.arna.jcraft.common.attack.core.IAttacker;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * A simple attack that performs at set points.
 * These points are the time in ticks from when the attack is initiated.
 *
 * @param <T>
 * @param <A>
 */
@Getter
public abstract class AbstractMultiHitAttack<T extends AbstractMultiHitAttack<T, A>, A extends IAttacker<? extends A, ?>> extends AbstractSimpleAttack<T, A> {
    private IntSortedSet hitMoments;

    protected AbstractMultiHitAttack(final int cooldown, final int duration, final float moveDistance, final float damage, final int stun,
                                     final float hitboxSize, final float knockback, final float offset, final @NonNull IntCollection hitMoments) {
        super(cooldown, hitMoments.intStream().min().orElse(0), duration, moveDistance, damage, stun, hitboxSize, knockback, offset);

        withHitMoments(hitMoments);
    }

    public T withHitMoments(final IntCollection hitMoments) {
        // Ensure hitMoments is sorted
        IntSortedSet intermediary = new IntLinkedOpenHashSet();
        hitMoments.intStream()
                .sorted()
                .forEachOrdered(intermediary::add);
        this.hitMoments = IntSortedSets.unmodifiable(intermediary);
        return getThis();
    }

    @Override
    public boolean shouldPerform(final A attacker, final int moveStun) {
        return attacker.hasUser() && hitMoments.contains(getDuration() - moveStun);
    }

    @Override
    public int getBlow(final A attacker) {
        int tick = getDuration() - attacker.getMoveStun();
        AtomicInteger blow = new AtomicInteger(-1);
        hitMoments.forEach(i -> {
            if (tick >= i) {
                blow.getAndIncrement();
            }
        });

        return blow.get();
    }
}
