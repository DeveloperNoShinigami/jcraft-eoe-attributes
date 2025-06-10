package net.arna.jcraft.common.attack.moves.shared;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import net.arna.jcraft.api.attack.IAttacker;
import net.arna.jcraft.api.attack.MoveType;
import net.arna.jcraft.api.attack.moves.AbstractMove;
import net.minecraft.world.entity.LivingEntity;
import java.util.Set;

/**
 * Not really an attack, but rather a placeholder to indicate that you've
 * missed your counter and are punished for it.
 */
public final class CounterMissMove<A extends IAttacker<? extends A, ?>> extends AbstractMove<CounterMissMove<A>, A> {
    public CounterMissMove(final int duration) {
        super(0, duration + 1, duration, 1f);
    }

    @Override
    public @NonNull MoveType<CounterMissMove<A>> getMoveType() {
        return Type.INSTANCE.cast();
    }

    @Override
    public @NonNull Set<LivingEntity> perform(final A attacker, final LivingEntity user) {
        return Set.of();
    }

    @Override
    protected @NonNull CounterMissMove<A> getThis() {
        return this;
    }

    @Override
    public @NonNull CounterMissMove<A> copy() {
        return copyExtras(new CounterMissMove<>(getDuration()));
    }

    public static class Type extends AbstractMove.Type<CounterMissMove<?>> {
        public static final Type INSTANCE = new Type();

        @Override
        protected @NonNull App<RecordCodecBuilder.Mu<CounterMissMove<?>>, CounterMissMove<?>> buildCodec(RecordCodecBuilder.Instance<CounterMissMove<?>> instance) {
            return instance.group(extras(), duration()).apply(instance, applyExtras(CounterMissMove::new));
        }
    }
}
