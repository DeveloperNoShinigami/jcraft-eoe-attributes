package net.arna.jcraft.common.attack.moves.base;

import com.mojang.datafixers.Products;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.util.Function9;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntLinkedOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSortedSet;
import it.unimi.dsi.fastutil.ints.IntSortedSets;
import lombok.Getter;
import lombok.NonNull;
import net.arna.jcraft.common.attack.core.AttackMoveExtras;
import net.arna.jcraft.common.attack.core.BaseMoveExtras;
import net.arna.jcraft.common.attack.core.IAttacker;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A simple attack that performs at set points.
 * These points are the time in ticks from when the attack is initiated.
 *
 * @param <T>
 * @param <A>
 */
@SuppressWarnings("UnusedReturnValue")
@Getter
public abstract class AbstractMultiHitAttack<T extends AbstractMultiHitAttack<T, A>, A extends IAttacker<? extends A, ?>> extends AbstractSimpleAttack<T, A> {
    private IntSortedSet hitMoments;

    protected AbstractMultiHitAttack(final int cooldown, final int duration, final float moveDistance, final float damage,
                                     final int stun, final float hitboxSize, final float knockback, final float offset,
                                     final @NonNull IntCollection hitMoments) {
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

    protected abstract static class Type<M extends AbstractMultiHitAttack<? extends M, ?>> extends AbstractSimpleAttack.Type<M> {
        protected RecordCodecBuilder<M, IntSortedSet> hitMoments() {
            return Codec.INT.listOf()
                    .<IntSortedSet>xmap(IntLinkedOpenHashSet::new, ArrayList::new)
                    .optionalFieldOf("hit_moments", IntSortedSets.EMPTY_SET)
                    .forGetter(AbstractMultiHitAttack::getHitMoments);
        }

        protected Products.P11<RecordCodecBuilder.Mu<M>, BaseMoveExtras, AttackMoveExtras, Integer, Integer, Float,
                Float, Integer, Float, Float, Float, IntSortedSet>
        multiHitDefault(RecordCodecBuilder.Instance<M> instance) {
            return instance.group(extras(), attackExtras(), cooldown(), duration(), moveDistance(), damage(), stun(),
                    hitboxSize(), knockback(), offset(), hitMoments());
        }

        protected App<RecordCodecBuilder.Mu<M>, M> multiHitDefault(RecordCodecBuilder.Instance<M> instance, Function9<Integer, Integer, Float,
                Float, Integer, Float, Float, Float, IntSortedSet, M> function) {
            return multiHitDefault(instance).apply(instance, applyAttackExtras(function));
        }
    }
}
